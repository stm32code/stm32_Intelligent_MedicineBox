#ifndef __BUS_H
#define __BUS_H
#include "sys.h"
#include "git.h"
//计步
#define X_SUB 100
#define Y_SUB 100
#define Z_SUB 100

//跌倒
#define DX_SUB 600
#define DY_SUB 600
#define DZ_SUB 600

extern u32 fall_tick;//

extern u16 BUS_Angle;
extern u16 FALL_Angle;

extern u8    fall_flag;
extern u32   bus;//步数
extern short last_angx,last_angy,last_angz; 
extern u8    bus_flag; 
void  obtain_bus(void);

#endif
















