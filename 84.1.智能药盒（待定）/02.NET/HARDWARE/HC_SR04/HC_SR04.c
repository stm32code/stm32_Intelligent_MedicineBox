#include "HC_SR04.h"
#include "delay.h"

//////////////////////////////////////////////////////////////////////////////////

u16 TIM3_UPDATA = 0;
u32 temp1 = 0;

void HC_SR04_IO2_Init(void)
{

	GPIO_InitTypeDef GPIO_InitStructure;

	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE); // 使能PB端口时钟
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_12 ;			  // 端口配置
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING; // 推挽输出
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;	  // IO口速度为50MHz
	GPIO_Init(GPIOB, &GPIO_InitStructure);				  // 根据设定参数初始化GPIOB3
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_13;			  // 端口配置
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz; // IO口速度为50MHz
	GPIO_Init(GPIOB, &GPIO_InitStructure);			  // 根据设定参数初始化GPIOB4
}
u16 Get_SR04_Distance2(void)
{

	SR04_Trlg2 = 1; // 触发信号是高电平脉冲，宽度大于10us
	delay_us(20);
	SR04_Trlg2 = 0;
	while (!SR04_Echo2)
		;					 // 等待高电平
	TIM_SetCounter(TIM3, 0); // 重填计数器值
	TIM3_UPDATA = 0;		 // 溢出次数清零
	while (SR04_Echo2)
		;																			 // 等待低电平
	TIM_Cmd(TIM3, DISABLE);															 // 暂时关闭定时器，保证数据正确性
	temp1 = (int)(((double)(TIM_GetCounter(TIM3) + (7200 * TIM3_UPDATA))) / 72 / 2); // 得到高电平总时间，单位us（定时器的计数值加上溢出的值才是高电平的时间），除以2是计算单程的时间
	// （7200* TIM2_UPDATA这里为溢出的时间us，可以转换为100* TIM2_UPDATA,因为在公式后面除以了72。可最终理解为TIM2_UPDATA个100us）
	TIM_Cmd(TIM3, ENABLE);
	return temp1;
}
void TIM3_Init(u16 arr, u16 psc)
{
	TIM_TimeBaseInitTypeDef TIM_TimeBaseStructure;
	NVIC_InitTypeDef NVIC_InitStructure;

	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM3, ENABLE); // 使能TIM2时钟

	// 初始化定时器3
	TIM_TimeBaseStructure.TIM_Period = arr;						// 设定计数器自动重装值
	TIM_TimeBaseStructure.TIM_Prescaler = psc;					// 预分频器
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1;		// 设置时钟分割:TDTS = Tck_tim
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up; // TIM向上计数模式
	TIM_TimeBaseInit(TIM3, &TIM_TimeBaseStructure);				// 根据TIM_TimeBaseInitStruct中指定的参数初始化TIMx的时间基数单位
	// 中断分组初始化
	NVIC_InitStructure.NVIC_IRQChannel = TIM3_IRQn;			  // TIM3中断
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 3; // 先占优先级2级
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 2;		  // 从优先级0级
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;			  // IRQ通道被使能
	NVIC_Init(&NVIC_InitStructure);							  // 根据NVIC_InitStruct中指定的参数初始化外设NVIC寄存器

	TIM_ITConfig(TIM3, TIM_IT_Update, ENABLE); // 允许更新中断 ,

	TIM_Cmd(TIM3, ENABLE); // 使能定时器2
}
// 定时器3中断服务程序
void TIM3_IRQHandler(void)
{
	if (TIM_GetITStatus(TIM3, TIM_IT_Update) != RESET) // 检查指定的TIM中断发生与否:TIM 中断源
	{
		TIM3_UPDATA++;								// 当回响信号很长时，记录溢出次数，每加一次代表加100us
		TIM_ClearITPendingBit(TIM3, TIM_IT_Update); // 清除中断标志位
	}
}
