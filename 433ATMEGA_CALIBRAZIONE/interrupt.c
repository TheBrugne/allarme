#include "funzioni433.h"
#include "brugne433.h"
#include <avr/interrupt.h>
#include <avr/eeprom.h>

unsigned char val_atteso= 200;
volatile unsigned int ovf_count=0; //8mhz/256=31250;     1/31250=0,000032 every tick; siccome il master manda 100ms, cioè 200ms fra due rising, mi servono 6250 tick, ovvero 6250/256= 24,4 cioè   24 ovf + 105 di TCNT0
volatile unsigned char contatore=0;
volatile unsigned char ptr=0;
unsigned char high[10];
unsigned char low[10];
unsigned char freq[10];
unsigned char diff_ovf_count=0;
unsigned char diff_tcnt_count=0;
unsigned char prev_diff=0;
unsigned char cambio=0;
volatile unsigned int jitter=0;
volatile unsigned char a=0;
void init_timer0_ovf(void)
{
	
	TCCR0 =0x00;
	TCCR0 |= (1<<CS02);//256,   //prescalare a 8; visto che il master manda una quadra a 5khz=200microsecondi, 1/1MHZ=1microsecondo, mi aspetto che sia tarato quando TCNT0 vale circa 200
	TCNT0=0;
	
}

void start_timer0_ovf(void)
{
	TIMSK |= 1<< TOIE0;    //abilito interrupt sul  ovf zero
}

void stop_timer0_ovf(void)
{
	TIMSK &= ~(1<< TOIE0);    //abilito interrupt sul  ovf zero
}


ISR(TIMER0_OVF_vect) //1ms
{
	if (jitter<100)
	jitter++;
	TCNT0=0;
	ovf_count++;
	if (ovf_count==50)
	{
		contatore=0;
		a++;
		OSCCAL=a;
		ovf_count=0;
	}
}

unsigned char getbestfreq()
{
	unsigned int diff= 65535;
	unsigned int loc_diff;
	unsigned char b=0;
	for (int i =0; i<ptr; i++)
	{
		loc_diff= high[i]*255 + low[i];
		if (loc_diff<diff)
		{
			diff=loc_diff;
			b=i;
		}
	}
	return freq[b];
}
ISR(INT1_vect)
{
	sei();
	if (contatore==0 && jitter >10)
	{
		jitter=0;
		ovf_count=0;
		TCNT0=0;
		contatore=1;
	}
	if (contatore==1 && jitter >10)
	{
		jitter=0;
		if (ovf_count>=26)
		{
			PORTC |=1<<PC3;
			PORTB |=1<<PB5;
			//cli();
			stop_timer0_ovf();
			
			GICR&=~(1<<INT1);
			sei();
			//PORTB |=1<<PB1;
			eeprom_write_byte((BYTE*)0x02, getbestfreq());
			
			PORTC |=1<<PC3;
			PORTB |=1<<PB5;
			
		}
		else if(ovf_count>=24 )
		{
			//diff=val_atteso-TCNT0;
			//prev_diff=diff;
			//if(TCNT0>20)
			high[ptr]=ovf_count-24;
			if(TCNT0>diff_tcnt_count)
			high[ptr]=TCNT0-105;
			else
			high[ptr]=105 - TCNT0;
			freq[ptr]=a;
			if (ptr<9)
			ptr++;
			//PORTB |=1<<PB1;
		}
		
		//if (OSCCAL<255)
		//OSCCAL++;
		//if(OSCCAL>200)
		//PORTB |=1<<PB1;
		
		
		
		contatore=2;
	}
	if (contatore>1)
	{
		
		
	}
	
}