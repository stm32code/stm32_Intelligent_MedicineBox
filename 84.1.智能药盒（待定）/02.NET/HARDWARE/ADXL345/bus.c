#include "bus.h"
#include "math.h"
#include "usart.h"
#include "adxl345.h"
#include "stdlib.h"

u32 fall_tick; //
u32 bus;	   // 步数
u8 fall_flag = 0;
u8 bus_flag, last_busflag;
short last_angx, last_angy, last_angz;


u16 BUS_Angle = 100;
u16 FALL_Angle = 600;

void obtain_bus()
{

	ADXL345_Read_Average(&x, &y, &z, 10); // 读取X,Y,Z三个方向的加速度值

	now_angx = ADXL345_Get_Angle(x, y, z, 1);
	now_angy = ADXL345_Get_Angle(x, y, z, 2);
	now_angz = ADXL345_Get_Angle(x, y, z, 0);

	if (abs(now_angx - last_angx) >= BUS_Angle || abs(now_angy - last_angy) >= BUS_Angle)
	{
		bus++;
	}

	if (abs(now_angx - last_angx) >= FALL_Angle || abs(now_angy - last_angy) >= FALL_Angle)
	{
		//摔倒
		device_state_init.Fall_State = 1;
	}

	last_angx = now_angx;
	last_angy = now_angy;
	last_angz = now_angz;
	// printf("%d,%d\r\n",now_angx,now_angy);
}
