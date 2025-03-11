#ifndef __HC_SR04_H
#define __HC_SR04_H
#include "sys.h"

#define SR04_Trlg2 PBout(13) // PC
#define SR04_Echo2 PBin(12)  // PC11

void HC_SR04_IO2_Init(void);
void TIM3_Init(u16 arr, u16 psc);
u16 Get_SR04_Distance2(void);

#endif
