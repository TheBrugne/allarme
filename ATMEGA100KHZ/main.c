

#include <avr/interrupt.h>

#define CLOCK_PIN 12

void init_timer0_ovf(void)
{
    DDRD |=1<<PD6;
	DDRB |=1<<PB5;
	PORTB &=~(1<<PB5);
    TCCR0 =0x00;
    TCCR0 |= (1<<CS02);  //prescalare a 256
    /* 16MHZ/1024=15625
    1/15625=0,000064;
    0,000064*155= 0,01 10 millisecondi; mi basta inizializzare tcnt0 a 100;
    */
    TCNT0=6; // 20khz
	
}

void start_timer0_ovf(void)
{
     TIMSK |= 1<< TOIE0;    //abilito interrupt sul  ovf zero
}

int main(void) 
{
	
	
    init_timer0_ovf(); //inziializzo timer0 per ciclo main
    start_timer0_ovf(); //start timer interrupt
    sei(); //abilito interrupt globale
     while (1)
     {
	     
	     
	     
     }
     return 0;
     
	
	
}



