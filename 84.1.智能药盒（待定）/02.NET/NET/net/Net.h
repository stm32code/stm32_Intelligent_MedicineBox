#ifndef _ONENET_H_
#define _ONENET_H_

#include "stm32f10x_it.h"
#include "git.h"
extern U8 Connect_Net;

_Bool DevLink(void);

void SendMqtt(U8 Cmd);
void SendData(void);

void RevPro(unsigned char *cmd);
void Link_OneNet(u8 Link);
_Bool Subscribe(const char *topics[], unsigned char topic_cnt);
_Bool Publish(const char *topic, const char *msg);

#endif
