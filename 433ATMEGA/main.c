
#define _MAIN_433_
#include "definizioni.h"
#include "brugne433.h"

#include "funzioni433.h"
#include <avr/interrupt.h>
#include <avr/eeprom.h>
#define TX_PIN 12
#define RX_PIN 8


#define BTN_PREESED (!(PORTX & 1<< Y))


//selezionare solo uno dei due.
//#define TRASMETTITORE
#define RICEVITORE

int main(void) 
{
	
    UBRRH = (unsigned char)(UBRR_VALUE>>8);
    UBRRL = (unsigned char)UBRR_VALUE;
    // Set frame format to 8 data bits, no parity, 1 stop bit
    UCSRB |= (1<<TXEN);
    UCSRC |= (1<<URSEL)|(1<<UCSZ1)|(1<<UCSZ0);
	
    OSCCAL= eeprom_read_byte((BYTE*)0x02);
	
    set_rx_pin(RX_PIN); //setto pin di ricezione
    init_timer1_compare(); //inizializzo timer 1 compare
    baud_setup(2000); //inizializzo ocr1a al valore corretto e imposto piedini
    init_timer0_ovf(); //inziializzo timer0 per ciclo main
    start_timer1_compareA(); //start timer interrupt
    start_timer0_ovf(); //start timer interrupt
    sei(); //abilito interrupt globale

    main_loop();

	
}



int main_loop(void) 
{
 
	n_3bit=0;
	n_3bit_bassi=0;
	n_3bit_alti=0;
	pinMode(13, OUTPUT);
	digitalWrite(13, FALSE);
	pinMode(0, OUTPUT);
	digitalWrite(0, TRUE); //tengo alta linea TX
	set_timer10ms(TIMER_10MS_STOP_RX,0);
	packet_to_raspberry=0;
	ptr_to_buff_raspberry=0;
    while (1)
    {
		if (is_timer10ms_expired(TIMER_10MS_STOP_RX))
			rx_start();
		
		
    }
	return 0;
	

}