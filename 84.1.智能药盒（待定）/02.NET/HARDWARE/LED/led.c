#include "led.h"
#include "delay.h"



// 初始化PB5和PE5为输出口.并使能这两个口的时钟
// LED IO初始化
void LED_Init(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;

	RCC_APB2PeriphClockCmd(State_GPIO_CLK|LED1_GPIO_CLK, ENABLE); // 使能端口时钟C

	GPIO_InitStructure.GPIO_Pin = State_GPIO_PIN;	  // 端口配置, 推挽输出
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP;  // 推挽输出
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz; // IO口速度为50MHz
	GPIO_Init(State_GPIO_PORT, &GPIO_InitStructure);  // 推挽输出 ，IO口速度为50MHz

	 GPIO_InitStructure.GPIO_Pin = LED1_GPIO_PIN;	// 端口配置, 推挽输出
	 GPIO_Init(LED1_GPIO_PORT, &GPIO_InitStructure); // 推挽输出 ，IO口速度为50MHz

//	 GPIO_InitStructure.GPIO_Pin = LED2_GPIO_PIN;	// 端口配置, 推挽输出
//	 GPIO_Init(LED2_GPIO_PORT, &GPIO_InitStructure); // 推挽输出 ，IO口速度为50MHz

//	 GPIO_InitStructure.GPIO_Pin = LED3_GPIO_PIN;	// 端口配置, 推挽输出
//	 GPIO_Init(LED3_GPIO_PORT, &GPIO_InitStructure); // 推挽输出 ，IO口速度为50MHz

//	 GPIO_InitStructure.GPIO_Pin = LED4_GPIO_PIN;	// 端口配置, 推挽输出
//	 GPIO_Init(LED4_GPIO_PORT, &GPIO_InitStructure); // 推挽输出 ，IO口速度为50MHz

	State = OFF;
	// LED1_OFF;
	// LED2_OFF;
	// LED3_OFF;
	// LED4_OFF;
}

/*********************************************************************************
 * @Function	:	系统设置PB3和PB4为普通IO口
 * @Input		:	deviceSta,设备状态
 * @Output		: 	None
 * @Return		: 	None
 * @Others		: 	JTAG调试方式会受影响
 * @Date			:  2020-04-22
 **********************************************************************************/
void System_PB34_setIO(void)
{
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_AFIO, ENABLE); // 打开AFIO时钟
	GPIO_PinRemapConfig(GPIO_Remap_SWJ_Disable, ENABLE);
	GPIO_PinRemapConfig(GPIO_Remap_SWJ_JTAGDisable, ENABLE);
}
