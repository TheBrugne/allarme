#include <avr/io.h>
#define _FUNZIONI433_
#include "definizioni.h"
#include "brugne433.h"
#include <avr/interrupt.h>
#include "funzioni433.h"


void init_timer0_ovf(void)
{
    
    TCCR0 =0x00;
    TCCR0 |=(1<<CS00);  //prescalare a 0
    /* 16MHZ/1024=15625
    1/15625=0,000064;
    0,000064*155= 0,01 10 millisecondi; mi basta inizializzare tcnt0 a 100;
    */
    TCNT0=0;
}

void start_timer0_ovf(void)
{
     TIMSK |= 1<< TOIE0;    //abilito interrupt sul  ovf zero
}



