#ifndef __ds1302_H
#define __ds1302_H

#include "sys.h"

/*
DS1302接口:
	VCC -> 5.0/3.3
	GND -> GND
	GPIOB_12 ->DS1302_RST
	GPIOB_13 ->DS1302_DAT
	GPIOB_14 ->DS1302_CLK
*/

// #define TIME_SET "20231215221047"

/* 定义LED连接的GPIO端口, 用户只需要修改下面的代码即可改变控制的LED引脚 */
#define DS1302_CE_GPIO_PORT GPIOB				   /* GPIO端口 */
#define DS1302_CE_GPIO_CLK RCC_APB2Periph_GPIOB   /* GPIO端口时钟 */
#define DS1302_CE_GPIO_PIN GPIO_Pin_3			   /* 连接到SCL时钟线的GPIO */
#define DS1302_SCLK_GPIO_PORT GPIOB			   /* GPIO端口 */
#define DS1302_SCLK_GPIO_CLK RCC_APB2Periph_GPIOB /* GPIO端口时钟 */
#define DS1302_SCLK_GPIO_PIN GPIO_Pin_5		   /* 连接到SCL时钟线的GPIO */
#define DS1302_DATA_GPIO_PORT GPIOB			   /* GPIO端口 */
#define DS1302_DATA_GPIO_CLK  RCC_APB2Periph_GPIOB /* GPIO端口时钟 */
#define DS1302_DATA_GPIO_PIN GPIO_Pin_4		   /* 连接到SCL时钟线的GPIO */

// DS1302引脚定义,可根据实际情况自行修改端口定义
// #define DS1302_OutPut_Mode() {GPIOB->CRL &= 0xF0FFFFFF;GPIOB->CRL |= 0x03000000;}//将 GPIOA 口的 PA6 引脚设置成推挽输出模式，并清空了其他引脚的配置
// #define DS1302_InPut_Mode()  {GPIOB->CRL &= 0xF0FFFFFF;GPIOB->CRL |= 0x08000000;}//PA7 引脚既可以作为普通的 GPIO 输出口

#define DS1302_IN PBin(4)
#define DS1302_OUT PBout(4)
#define DS1302_RST PBout(3)
#define DS1302_CLK PBout(5)

typedef struct TIMEData
{
	u16 year;
	u8 month;
	u8 day;
	u8 hour;
	u8 minute;
	u8 second;
	u8 week;
} DS1302_Time_t;
// 创建TIMEData结构体方便存储时间日期数据

void DS1302_Init(void);
void DS1302_WriteByte(u8 addr, u8 data);
u8 DS1302_ReadByte(u8 addr);
void DS1302_WriteTime(void);
void DS1302_ReadTime(void);
void DS1302_GetTime(DS1302_Time_t *time);
// 设置时间
void DS1302_SetTime(DS1302_Time_t *time);
// 十六进制字符串转换成十六进制数组
u32 StringToHex(char *str, u8 *out, u32 *outlen);

// 读取闹钟数据
void DS1302_AlarmTime(u8 cmd);
// 整数转换成字符串
char *Int2String(int num, char *str); // 10进制
#endif
