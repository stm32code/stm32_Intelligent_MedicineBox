#include "ds1302.h"
#include "delay.h"
#include <string.h>
#include "usart.h"
/*
DS1302接口:
	VCC -> 5.0/3.3
	GND -> GND
	GPIOB_12 ->DS1302_RST
	GPIOB_13 ->DS1302_DAT
	GPIOB_14 ->DS1302_CLK
*/
// DS1302地址定义
#define DS1302_SEC_ADDR 0x80	 // 秒数据地址
#define DS1302_MIN_ADDR 0x82	 // 分数据地址
#define DS1302_HOUR_ADDR 0x84	 // 时数据地址
#define DS1302_DAY_ADDR 0x86	 // 日数据地址
#define DS1302_MONTH_ADDR 0x88	 // 月数据地址
#define DS1302_WEEK_ADDR 0x8a	 // 星期数据地址
#define DS1302_YEAR_ADDR 0x8c	 // 年数据地址
#define DS1302_CONTROL_ADDR 0x8e // 控制数据地址
#define DS1302_CHARGER_ADDR 0x90 // 充电功能地址
#define DS1302_CLKBURST_ADDR 0xbe

// 初始时间定义


u8 time_buf[8] = {0x20, 0x23, 0x12, 0x13, 0x13, 0x010, 0x00, 0x03}; // 初始时间
char *TIME_SET = "2023121622104706";
u8 time_set[8] = {0x20, 0x23, 0x12, 0x13, 0x14, 0x40, 0x00, 0x03};		   // 初始时间
char *ALARM1_SET = "1111";
char *ALARM2_SET = "2222";
u8 alarm1_set[2] = {0, 0};		   // 闹钟1
u8 alarm2_set[2] = {0, 0};		   // 闹钟2
u8 alarm3_set[2] = {0, 0};		   // 闹钟3
u8 alarm4_set[2] = {0, 0};		   // 闹钟4
u8 alarm5_set[2] = {0, 0};		   // 闹钟5
u8 alarm6_set[2] = {0, 0};		   // 闹钟6

u8 sec_buf = 0;	 // 秒缓存
u8 sec_flag = 0; // 秒标志位

void DS1302_Init() // CE,SCLK端口初始化
{
	GPIO_InitTypeDef GPIO_InitStructure;
	RCC_APB2PeriphClockCmd(DS1302_CE_GPIO_CLK, ENABLE);
	GPIO_InitStructure.GPIO_Pin = DS1302_CE_GPIO_PIN; // PC.11  CE
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; // 推挽输出
	GPIO_Init(DS1302_CE_GPIO_PORT, &GPIO_InitStructure);	 // 初始化GPIOC.11
	GPIO_ResetBits(DS1302_CE_GPIO_PORT, DS1302_CE_GPIO_PIN);

	GPIO_InitStructure.GPIO_Pin = DS1302_SCLK_GPIO_PIN; // PC.12  SCLK
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; // 推挽输出
	GPIO_Init(DS1302_SCLK_GPIO_PORT, &GPIO_InitStructure); // 初始化GPIOC.12
	GPIO_ResetBits(DS1302_SCLK_GPIO_PORT, DS1302_SCLK_GPIO_PIN);
}
// 配置双向I/O端口为输出态
static void ds1032_DATAOUT_init()
{
	GPIO_InitTypeDef GPIO_InitStructure;
	RCC_APB2PeriphClockCmd(DS1302_DATA_GPIO_CLK , ENABLE);

	GPIO_InitStructure.GPIO_Pin = DS1302_DATA_GPIO_PIN; // PC.10  DATA
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
	GPIO_Init(DS1302_DATA_GPIO_PORT, &GPIO_InitStructure); // 初始化GPIOC.10
	GPIO_ResetBits(DS1302_DATA_GPIO_PORT, DS1302_DATA_GPIO_PIN);
}
// 配置双向I/O端口为输入态
static void ds1032_DATAINPUT_init()
{
	GPIO_InitTypeDef GPIO_InitStructure;
	RCC_APB2PeriphClockCmd(DS1302_DATA_GPIO_CLK , ENABLE);
	GPIO_InitStructure.GPIO_Pin = DS1302_DATA_GPIO_PIN; // PC.10 DATA
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
	GPIO_Init(DS1302_DATA_GPIO_PORT, &GPIO_InitStructure); // 初始化GPIOC.10
}
// 向DS1302写入一字节数据
void DS1302_WriteByte(u8 addr, u8 data)
{
	u8 i;
	DS1302_RST = 0; // 禁止数据传输 ！！！这条很重要
	DS1302_CLK = 0; // 确保写数据前SCLK为低电平
	DS1302_RST = 1; // 启动DS1302总线
	ds1032_DATAOUT_init();
	addr = addr & 0xFE;		// 最低位置零，寄存器0位为0时写，为1时读
	for (i = 0; i < 8; i++) // 写入目标地址：addr
	{
		if (addr & 0x01)
			DS1302_OUT = 1;
		else
			DS1302_OUT = 0;
		DS1302_CLK = 1; // 时钟上升沿写入数据
		DS1302_CLK = 0;
		addr = addr >> 1;
	}
	for (i = 0; i < 8; i++) // 写入数据：data
	{
		if (data & 0x01)
			DS1302_OUT = 1;
		else
			DS1302_OUT = 0;
		DS1302_CLK = 1; // 时钟上升沿写入数据
		DS1302_CLK = 0;
		data = data >> 1;
	}
	DS1302_CLK = 1; // 将时钟电平置于高电平状态 ，处于已知状态
	DS1302_RST = 0; // 停止DS1302总线
}

// 从DS1302读出一字节数据
u8 DS1302_ReadByte(u8 addr)
{
	u8 i, temp;
	DS1302_RST = 0; // 这条很重要
	DS1302_CLK = 0; // 先将SCLK置低电平,确保写数居前SCLK被拉低
	DS1302_RST = 1; // 启动DS1302总线
	ds1032_DATAOUT_init();
	// 写入目标地址：addr
	addr = addr | 0x01; // 最低位置高，寄存器0位为0时写，为1时读
	for (i = 0; i < 8; i++)
	{
		if (addr & 0x01)
			DS1302_OUT = 1;
		else
			DS1302_OUT = 0;
		DS1302_CLK = 1; // 写数据
		DS1302_CLK = 0;
		addr = addr >> 1;
	}
	// 从DS1302读出数据：temp
	ds1032_DATAINPUT_init();
	for (i = 0; i < 8; i++)
	{
		temp = temp >> 1;
		if (DS1302_IN)
			temp |= 0x80;
		else
			temp &= 0x7F;
		DS1302_CLK = 1;
		DS1302_CLK = 0;
	}
	DS1302_CLK = 1; // 将时钟电平置于已知状态
	DS1302_RST = 0; // 停止DS1302总线
	return temp;
}

// 向DS1302写入时钟数据,用于时间校准修改
void DS1302_WriteTime()
{

	u32 outlen = 0;
	u8 buf_hex[18];

	StringToHex(TIME_SET, buf_hex, &outlen);
	memcpy(time_set, buf_hex, outlen);


	DS1302_WriteByte(DS1302_CONTROL_ADDR, 0x00); // 关闭写保护
	DS1302_WriteByte(DS1302_SEC_ADDR, 0x80);	 // 暂停时钟
	// DS1302_WriteByte(DS1302_CHARGER_ADDR,0xa9);     //涓流充电
	DS1302_WriteByte(DS1302_YEAR_ADDR, time_set[1]);  // 年
	DS1302_WriteByte(DS1302_MONTH_ADDR, time_set[2]); // 月
	DS1302_WriteByte(DS1302_DAY_ADDR, time_set[3]);	  // 日
	DS1302_WriteByte(DS1302_HOUR_ADDR, time_set[4]);  // 时
	DS1302_WriteByte(DS1302_MIN_ADDR, time_set[5]);	  // 分
	DS1302_WriteByte(DS1302_SEC_ADDR, time_set[6]);	  // 秒
	DS1302_WriteByte(DS1302_WEEK_ADDR, time_set[7]);  // 周
	DS1302_WriteByte(DS1302_CHARGER_ADDR, 0xA5);	  // 打开充电功能 选择2K电阻充电方式
	DS1302_WriteByte(DS1302_CONTROL_ADDR, 0x80);	  // 打开写保护
}
// 读取闹钟数据
void DS1302_AlarmTime(u8 cmd)
{

	u32 outlen1,outlen2 = 0;
	u8 buf1_hex[4],buf2_hex[4];
	switch(cmd){
		case 1:
				StringToHex(ALARM1_SET, buf1_hex, &outlen1);
				memcpy(alarm1_set, buf1_hex, outlen1);
				StringToHex(ALARM2_SET, buf2_hex, &outlen2);
				memcpy(alarm2_set, buf2_hex, outlen2);
			break;
		case 2:
				StringToHex(ALARM1_SET, buf1_hex, &outlen1);
				memcpy(alarm3_set, buf1_hex, outlen1);
				StringToHex(ALARM2_SET, buf2_hex, &outlen2);
				memcpy(alarm4_set, buf2_hex, outlen2);
			break;
		case 3:
				StringToHex(ALARM1_SET, buf1_hex, &outlen1);
				memcpy(alarm5_set, buf1_hex, outlen1);
				StringToHex(ALARM2_SET, buf2_hex, &outlen2);
				memcpy(alarm6_set, buf2_hex, outlen2);
			break;
	}

}
// 从DS1302读出时钟数据
void DS1302_ReadTime(void)
{
	time_buf[1] = DS1302_ReadByte(DS1302_YEAR_ADDR);		 // 年
	time_buf[2] = DS1302_ReadByte(DS1302_MONTH_ADDR);		 // 月
	time_buf[3] = DS1302_ReadByte(DS1302_DAY_ADDR);			 // 日
	time_buf[4] = DS1302_ReadByte(DS1302_HOUR_ADDR);		 // 时
	time_buf[5] = DS1302_ReadByte(DS1302_MIN_ADDR);			 // 分
	time_buf[6] = (DS1302_ReadByte(DS1302_SEC_ADDR)) & 0x7f; // 秒，屏蔽秒的第7位，避免超出59
	time_buf[7] = DS1302_ReadByte(DS1302_WEEK_ADDR);		 // 周
}

// 主函数
void DS1302_GetTime(DS1302_Time_t *time)
{
	DS1302_ReadTime();																									  // 读取时间
	time->year = (time_buf[0] >> 4) * 1000 + (time_buf[0] & 0x0F) * 100 + (time_buf[1] >> 4) * 10 + (time_buf[1] & 0x0F); // 计算年份
	time->month = (time_buf[2] >> 4) * 10 + (time_buf[2] & 0x0F);														  // 计算月份
	time->day = (time_buf[3] >> 4) * 10 + (time_buf[3] & 0x0F);															  // 计算日期
	time->hour = (time_buf[4] >> 4) * 10 + (time_buf[4] & 0x0F);														  // 计算小时
	time->minute = (time_buf[5] >> 4) * 10 + (time_buf[5] & 0x0F);														  // 计算分钟
	time->second = (time_buf[6] >> 4) * 10 + (time_buf[6] & 0x0F);														  // 计算秒钟
	time->week = (time_buf[7] & 0x0F);																					  // 计算星期
																														  // printf("时间:%d-%d-%d %d:%d:%d %d \n",TimeData.year,TimeData.month,TimeData.day,TimeData.hour,TimeData.minute,TimeData.second,TimeData.week);
}
// 十六进制字符串转换成十六进制数组
u32 StringToHex(char *str, u8 *out, u32 *outlen)
{
	char *p = str;
	char high = 0, low = 0;
	int tmplen = strlen(p), cnt = 0;
	tmplen = strlen(p);
	while (cnt < (tmplen / 2))
	{
		high = ((*p > '9') && ((*p <= 'F') || (*p <= 'f'))) ? *p - 48 - 7 : *p - 48;
		low = (*(++p) > '9' && ((*p <= 'F') || (*p <= 'f'))) ? *(p)-48 - 7 : *(p)-48;
		out[cnt] = ((high & 0x0f) << 4 | (low & 0x0f));
		p++;
		cnt++;
	}
	if (tmplen % 2 != 0)
		out[cnt] = ((*p > '9') && ((*p <= 'F') || (*p <= 'f'))) ? *p - 48 - 7 : *p - 48;

	if (outlen != NULL)
		*outlen = tmplen / 2 + tmplen % 2;
	return tmplen / 2 + tmplen % 2;
}

