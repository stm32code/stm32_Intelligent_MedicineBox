#ifndef __USART2_H
#define __USART2_H

#include "stm32f10x.h"
#include <stdio.h>
#include "git.h"
typedef enum
{
	PRINTF2,
	CLEAN2,
	OTHER2
} Usart2_Funtctions;
/*********************************************************************************
 * @Function	:  USART5 GPIO 引脚宏定义
 **********************************************************************************/
#define USART_2 USART2
#define USART2_CLK RCC_APB1Periph_USART2
#define USART2_APBxClkCmd RCC_APB1PeriphClockCmd

#define USART2_GPIO_CLK (RCC_APB2Periph_GPIOA)
#define USART2_GPIO_APBxClkCmd RCC_APB2PeriphClockCmd

#define USART2_TX_GPIO_PORT GPIOA
#define USART2_TX_GPIO_PIN GPIO_Pin_2
#define USART2_RX_GPIO_PORT GPIOA
#define USART2_RX_GPIO_PIN GPIO_Pin_3

#define USART2_IRQ USART2_IRQn
#define USART2_IRQHandler USART2_IRQHandler

#define UART2_RXBUF_SIZE 256 // 串口2接收缓冲区大小

/*********************************************************************************
* @Function	:  USART GPIO 配置,工作参数配置
**********************************************************************************/
void Usart2_Init(U32 bound, U8 PreemptionPriority, U8 SubPriority);
/*********************************************************************************
 * @Function	:  处理接收到的串口三数据
 **********************************************************************************/
void Send_Usart2(void);
/*********************************************************************************
 * @Function	:  Usart3 的一些基本功能,输出、清空缓存数据,标志位等
 **********************************************************************************/
void Uart2_funtction(Usart2_Funtctions CMD);

void u2_printf(char *fmt, ...);
#endif /* __USART_H */
