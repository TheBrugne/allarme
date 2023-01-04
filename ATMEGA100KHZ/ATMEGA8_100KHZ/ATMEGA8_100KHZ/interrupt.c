#include "funzioni433.h"
#include "brugne433.h"
#include <avr/interrupt.h>

unsigned int ctr=0;
unsigned char inverti;
ISR(TIMER0_OVF_vect) // 1 tick ogni 0,000016sec;   0,000016 *250 *25= 0,1
{
	ctr++;
	if (ctr==25)
	{
		ctr=0;
		if (inverti%2==0)
		{
			PORTD|=1<<PD6;
			PORTB|=1<<PB5;
			inverti=1;
			
		}
		else
		{
			PORTD&=~(1<<PD6);
			PORTB&=~(1<<PB5);
			inverti=0;
		}
		//TCNT0=56;
	}
	TCNT0=6;
    
 
}
