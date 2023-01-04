
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
   
	//diventa PB5 sulla board arduino
	DDRC |= 1<<PC3;//led, basso
	PORTC &=~ (1<<PC3);
	DDRB |=1 <<PB5;//led, basso
	PORTB &=~ (1<<PB5);
	
	
	DDRD &=~ (1<<PD3);//int 1 input, no PU
	PORTD &=~ (1<<PD3);
	MCUCR |= (1<<ISC11)|(1<<ISC10); //rising edge di int1
	GICR |=1 <<INT1;  //abilito interrupt su pin0
	OSCCAL=10;
	start_timer0_ovf(); //start timer interrupt
	sei(); //abilito interrupt globale
	while (1)
	{
		
	}
	return 0;
}
	
	