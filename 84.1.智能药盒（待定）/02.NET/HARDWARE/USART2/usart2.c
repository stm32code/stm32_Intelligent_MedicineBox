#include "usart2.h"
#include "usart.h"
#include "delay.h"
#include "timer.h"

#include "stdio.h"
#include "stdlib.h"
#include "string.h"
#include "stdarg.h"

/*********************************************************************************
 * @Function	:  状态参数
 **********************************************************************************/
#if UART2_RXBUF_SIZE

u8 USART2_RX_BUF[UART2_RXBUF_SIZE]; // 接收缓冲,最大USART3_MAX_RECV_LEN个字节.
u8 USART2_TX_BUF[UART2_RXBUF_SIZE]; // 发送缓冲,最大USART3_MAX_SEND_LEN字节

U16 Uart2_RecvCount = 0;   // 串口接收计数
U8 Uart2_EnterRecvFlg = 0; // 串口有数据接收标识

extern U16 Usart2_Time; // 串口有数据接收标识
extern U8 bTimeout10ms; /** < 10毫秒定时器超时标志 */

/*********************************************************************************
 * @Function	:  配置嵌套向量中断控制器NVIC
 **********************************************************************************/
static void NVIC_Configuration(U8 PreemptionPriority, U8 SubPriority)
{
	NVIC_InitTypeDef NVIC_InitStructure;

	/* 嵌套向量中断控制器组选择 */
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);
	NVIC_InitStructure.NVIC_IRQChannel = USART2_IRQ;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = PreemptionPriority;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = SubPriority;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
}
/*********************************************************************************
 * @Function	:  USART GPIO 配置,工作参数配置
 **********************************************************************************/
void Usart2_Init(U32 bound, U8 PreemptionPriority, U8 SubPriority)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;
	// 打开串口GPIO的时钟
	USART2_GPIO_APBxClkCmd(USART2_GPIO_CLK, ENABLE);
	// 打开串口外设的时钟
	USART2_APBxClkCmd(USART2_CLK, ENABLE);
	// 将USART Tx的GPIO配置为推挽复用模式
	GPIO_InitStructure.GPIO_Pin = USART2_TX_GPIO_PIN;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_Init(USART2_TX_GPIO_PORT, &GPIO_InitStructure);
	// 将USART Rx的GPIO配置为浮空输入模式
	GPIO_InitStructure.GPIO_Pin = USART2_RX_GPIO_PIN;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
	GPIO_Init(USART2_RX_GPIO_PORT, &GPIO_InitStructure);

	// 配置串口的工作参数
	// 配置波特率
	USART_InitStructure.USART_BaudRate = bound;
	// 配置 针数据字长
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;
	// 配置停止位
	USART_InitStructure.USART_StopBits = USART_StopBits_1;
	// 配置校验位
	USART_InitStructure.USART_Parity = USART_Parity_No;
	// 配置硬件流控制
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
	// 配置工作模式，收发一起
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
	// 完成串口的初始化配置
	USART_Init(USART_2, &USART_InitStructure);

	// 串口中断优先级配置
	NVIC_Configuration(PreemptionPriority, SubPriority);
	// 使能串口接收中断
	USART_ITConfig(USART_2, USART_IT_RXNE, ENABLE);
	// 使能串口
	USART_Cmd(USART_2, ENABLE);
	// 清除发送完成标志
	// USART_ClearFlag(USART1, USART_FLAG_TC);
}
/*********************************************************************************
 * @Function	:  Usart4 的一些基本功能,输出、清空缓存数据,标志位等
 **********************************************************************************/
void Uart2_funtction(Usart2_Funtctions CMD)
{
	switch (CMD)
	{
	case PRINTF2: // 输出接收到的串口消息
		printf("uart2 recv: %d, %s\r\n", Uart2_RecvCount, USART2_RX_BUF);
		break;
	case CLEAN2: // 清空串口消息和状态
		memset(USART2_RX_BUF, 0, UART2_RXBUF_SIZE);
		Uart2_RecvCount = 0;
		Uart2_EnterRecvFlg = 0;
#if GENERAL_TIM4
		Usart2_Time = 0;
#endif
		break;
	case OTHER2:
		break;
	default:
		break;
	}
}

/*********************************************************************************
 * @Function	:  处理接收到的串口二的数据
 **********************************************************************************/
void Send_Usart2(void)
{

	if (Usart2_Time > 9 && bTimeout10ms == MY_FALSE)
	{
		Uart2_EnterRecvFlg = 1;
	}
	if (Uart2_EnterRecvFlg)
	{
		printf("%s\n:", USART2_RX_BUF);
		// 解析json
		if ((char *)USART2_RX_BUF != NULL)
		{
			massage_parse_json((char *)USART2_RX_BUF); // 接收命令
		}
		Uart2_funtction(CLEAN2); // 清空
	}
}
// 确保一次发送数据不超过USART3_MAX_SEND_LEN字节
void u2_printf(char *fmt, ...)
{
	u16 i, j;
	va_list ap;
	va_start(ap, fmt);
	vsprintf((char *)USART2_TX_BUF, fmt, ap);
	va_end(ap);
	i = strlen((const char *)USART2_TX_BUF); // 此次发送数据的长度
	for (j = 0; j < i; j++)					 // 循环发送数据
	{
		while (USART_GetFlagStatus(USART2, USART_FLAG_TC) == RESET)
			; // 循环发送,直到发送完毕
		USART_SendData(USART2, USART2_TX_BUF[j]);
	}
}

/*********************************************************************************
 * @Function	:  串口3收发中断
 * @Input		:  None
 * @Output		:  None
 * @Return		:  None
 * @Others		:  None
 * @Date			:  2022-07-30
 **********************************************************************************/

void USART2_IRQHandler(void)
{
	uint8_t ucCh;
	if (USART_GetITStatus(USART2, USART_IT_RXNE) != RESET)
	{
		ucCh = USART_ReceiveData(USART2);
		if (Uart2_RecvCount < (UART2_RXBUF_SIZE - 1))
		{
			// 预留1个字节写结束符
			USART2_RX_BUF[Uart2_RecvCount++] = ucCh;
			// printf("%s\n:",USART2_RX_BUF);
		}
#if GENERAL_TIM4
		Usart2_Time = 1;
		bTimeout10ms = MY_TRUE; /** < 10毫秒定时器超时标志 */
#endif
		USART_ClearFlag(USART2, USART_FLAG_RXNE);
		ucCh = 0;
	}
}
#endif
