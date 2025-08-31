#ifndef __ds1302_H
#define __ds1302_H

#include "sys.h"

/*
DS1302�ӿ�:
	VCC -> 5.0/3.3
	GND -> GND
	GPIOB_12 ->DS1302_RST
	GPIOB_13 ->DS1302_DAT
	GPIOB_14 ->DS1302_CLK
*/

// #define TIME_SET "20231215221047"

/* ����LED���ӵ�GPIO�˿�, �û�ֻ��Ҫ�޸�����Ĵ��뼴�ɸı���Ƶ�LED���� */
#define DS1302_CE_GPIO_PORT GPIOB				   /* GPIO�˿� */
#define DS1302_CE_GPIO_CLK RCC_APB2Periph_GPIOB   /* GPIO�˿�ʱ�� */
#define DS1302_CE_GPIO_PIN GPIO_Pin_3			   /* ���ӵ�SCLʱ���ߵ�GPIO */
#define DS1302_SCLK_GPIO_PORT GPIOB			   /* GPIO�˿� */
#define DS1302_SCLK_GPIO_CLK RCC_APB2Periph_GPIOB /* GPIO�˿�ʱ�� */
#define DS1302_SCLK_GPIO_PIN GPIO_Pin_5		   /* ���ӵ�SCLʱ���ߵ�GPIO */
#define DS1302_DATA_GPIO_PORT GPIOB			   /* GPIO�˿� */
#define DS1302_DATA_GPIO_CLK  RCC_APB2Periph_GPIOB /* GPIO�˿�ʱ�� */
#define DS1302_DATA_GPIO_PIN GPIO_Pin_4		   /* ���ӵ�SCLʱ���ߵ�GPIO */

// DS1302���Ŷ���,�ɸ���ʵ����������޸Ķ˿ڶ���
// #define DS1302_OutPut_Mode() {GPIOB->CRL &= 0xF0FFFFFF;GPIOB->CRL |= 0x03000000;}//�� GPIOA �ڵ� PA6 �������ó��������ģʽ����������������ŵ�����
// #define DS1302_InPut_Mode()  {GPIOB->CRL &= 0xF0FFFFFF;GPIOB->CRL |= 0x08000000;}//PA7 ���żȿ�����Ϊ��ͨ�� GPIO �����

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
// ����TIMEData�ṹ�巽��洢ʱ����������

void DS1302_Init(void);
void DS1302_WriteByte(u8 addr, u8 data);
u8 DS1302_ReadByte(u8 addr);
void DS1302_WriteTime(void);
void DS1302_ReadTime(void);
void DS1302_GetTime(DS1302_Time_t *time);
// ����ʱ��
void DS1302_SetTime(DS1302_Time_t *time);
// ʮ�������ַ���ת����ʮ����������
u32 StringToHex(char *str, u8 *out, u32 *outlen);

// ��ȡ��������
void DS1302_AlarmTime(u8 cmd);
// ����ת�����ַ���
char *Int2String(int num, char *str); // 10����
#endif
