
#define _MAIN_433_
#include "definizioni.h"
#include "brugne433.h"
#include <avr/sleep.h>
#include "funzioni433.h"
#include <avr/interrupt.h>





//selezionare solo uno dei due.
#define TRASMETTITORE
//#define RICEVITORE

int main(void) 
{


    init_timer0_ovf(); //inziializzo timer0 per ciclo main
   
	
	DDRB|=1<<PB1;//led, basso
	PORTB&=~(1<<PB1);
	DDRB&=~(1<<PB2);//int 0 input, no PU
	PORTB&=~(1<<PB2);
	MCUCR|=(1<<ISC01)|(1<<ISC00); //rising edge di int0
	GIMSK|=1<<INT0;  //abilito interrupt su pin0
	OSCCAL=10;
	start_timer0_ovf(); //start timer interrupt
	sei(); //abilito interrupt globale
	while (1)
	{
		
	}
	return 0;
}
	
	