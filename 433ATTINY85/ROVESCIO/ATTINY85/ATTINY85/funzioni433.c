#include <avr/io.h>
#define _FUNZIONI433_
#include "definizioni.h"
#include "brugne433.h"
#include <avr/sleep.h>
#include "funzioni433.h"
#include <avr/interrupt.h>

void init_timer1_compare(void)
{
    TCCR1=0x83; //attivo ultimo bit CTC1 = compare su OCR1C e CS12 = CLK/8
}

void start_timer1_compareA(void)
{
    TIMSK |= 1<< OCIE1A;    //abilito interrupt sul compare A
}

void baud_setup (WORD baud_rate)
{
     /*importante!! non spedisco un bit ad ogni tick del timer interrupt, ma spedisco per 8 volte di fila
    lo stesso bit, in modo tale da ridurre errore; il valore del ricevitore sara basso o alto a seconda
    se ho ricevuto più bit bassi o più bit alti dentro un ciclo di 8 ticks.... si può migliorare?
    siccome ho impostato il prescalare a 1, allora per calcolare il valore di OCR1A divido per 8 (8 campioni per bit)

    ESEMPIO: se baud=2000,  F_CPU=16Mhz,   16mhz/2000/8=1000;
    cio� scrivo 1000 sul registro di confronto;
    siccome ho 1/16MHZ= 0,00000000625 secondi,  arrivo a 1000 in 0,0000625 sec= 62,5 microsecondi
    ogni 62,5 micro si attiva l'interrupt che spedisce un bit
    siccome ogni bit  viene letto 8 volte di fila,  spedisco un bit  ogni  62,5*8= 0,0005 sec.
    al secondo quindi spedisco 2000 bit (0,0005*2000=1)*/

    /* purtroppo su attiny ho solo 8 bit di risoluzione sul tiemr, quindi ho messo prescalare a 8--> cioè clock/8 , quindi divido nuovamente per 8 nella formula*/
    BYTE ocr1c_set= (F_CPU/baud_rate)/32;
    OCR1C= ocr1c_set;


    tx_busy=0;  //init
    rx_busy=0;
}


void init_timer0_ovf(void)
{
    
    TCCR0A =0x00;
    TCCR0B |= ((1<<CS02)|(1<<CS00));  //prescalare a 1024
    /* 16MHZ/1024=15625
    1/15625=0,000064;
    0,000064*155= 0,01 10 millisecondi; mi basta inizializzare tcnt0 a 100;
    */
   // TCNT0=100;
   
   //ho cambiato a 1ms
   TCNT0=248;
   ms_to_10ms=0;
}

void start_timer0_ovf(void)
{
     TIMSK |= 1<< TOIE0;    //abilito interrupt sul  ovf zero
}

void stop_timer0_ovf(void)
{
	TIMSK &= ~(1<< TOIE0);    //abilito interrupt sul  ovf zero
}


void port_setup()
{   //   NU    NU    PB5     PB4       PB3     PB2       PB1        PB0
	//   X      X     0      1          0       1        1          0
	//   X      X    reset   led       btn      vcc_tx   data_tx     unused
	//   X      X     in      out       in     out       out       in
	//DDRB= 0x16;
	DDRB= 0x0E;

	//   NU    NU    PB5     PB4       PB3     PB2       PB1        PB0
	//   X      X     1      0          1       0        0          1
	//   X      X    reset   led       btn     vcc_tx   data_tx     unused
	//   X      X    in_PU   out_low    in_PU  out_low  out_low     in_PU

	//PORTB=0x29;
	PORTB=0x31;
}

void go_to_sleep()
{
	TX_LOW;
	LED_LOW;
	VCC_LOW;
	cli();
	// MCUCR |= (1<<BODSE) | (1<<BODS); // timed sequence
	 //MCUCR &= ~(1<<BODSE); //BODS is already high.
	//BYTE mcu_copy= MCUCR;
	//mcu_copy|= 1<<BODS;
	//mcu_copy &= ~(1<<BODSE);
	//MCUCR |= ((1<<BODSE) | (1<<BODS));
	//MCUCR= mcu_copy;
	
	sleep_cpu();
}





void send_packet(BYTE* array_to_transmit, BYTE length)
{

   
    msg_length=length; 
    tx_index=0;
    bit_inviato=0;  
    campioni_8_bit=0;
    tx_busy=1;
	
}




BOOL is_transmitting(void)
{
    return tx_busy;
}

BOOL is_receiving (void)
{
    return rx_busy;
}

void tx_stop(void)
{
    // Disable the transmitter hardware
 
    TX_LOW;

    // No more ticks for the transmitter
    tx_busy = FALSE;
}

void rx_start()
{
    if (!rx_busy)
    {
	    rx_busy=TRUE;
        rx_done=FALSE;
	    //rx_active = FALSE; // Never restart a partial message
        //first_cycle_complete=0;
        n_campioni=0;
        //inversioni=0;
        n_bit_alti=0;
        n_bit_bassi=0;
        header=0;
        //val_atteso= PREPARE;
        data_bit = 0x0000;

    }
}


void filtra_messaggio(BYTE bit_rx)
{


  //  digitalWrite(13, TRUE);
    // Integrate each sample
    if (bit_rx)
	n_bit_alti++;

    if (bit_rx != previous_bit)
    {
	// Transition, advance if ramp > 80, retard if < 80
	rampa += ((rampa < RAMP_HALF) 
			   ? RAMP_RIT 
			   : RAMP_ANT);
	previous_bit = bit_rx;
    }
    else
    {
	// No transition
	// Advance ramp by standard 20 (== 160/8 samples)
	rampa += RAMP_DEF;
    }
    if (rampa >= RAMP_MAX)
    {
	    // Add this to the 12th bit of vw_rx_bits, LSB first
	    // The last 12 bits are kept
	    data_bit >>= 1;

	    // Check the integrator to see how many samples in this cycle were high.
	    // If < 5 out of 8, then its declared a 0 bit, else a 1;
	    if (n_bit_alti >= 5)
	     data_bit |= 0x8000;

	    rampa -= RAMP_MAX;
	    n_bit_alti = 0; // Clear the integral for the next cycle

	    if (header)
	    {
            n_campioni++;
            if (n_campioni>=8)
            {
				if(!rx_length)
				{
					BYTE test=(data_bit & 0xFF00)>>8;
					if (test== (BYTE)6)
						rx_length=6;
					else //ritento
					{
						header=0;
						rampa=0;
						n_bit_alti=0;
					}
				}
				else
				{
				BYTE test=(data_bit & 0xFF00)>>8;
				 buff_rx[cont++]= test;
				 if (cont==7)
				 {	
					cont=0;
					BYTE crc=0x00;
					//crc=crc_update(6,crc);
					//for (int i=0; i<6; i++)
						//crc=crc_update(buff_rx[i], crc);
					if (crc==buff_rx[6])
					{
						rx_done=TRUE;
						//digitalWrite(13,TRUE);
						
					}
					
					else //ritento
					{
						header=0;
						rampa=0;
						n_bit_alti=0;
						
					}
				 }
					

				}

				
                n_campioni=0;    
            }
	    
	    }
	// Not in a message, see if we have a start symbol
	     if (data_bit == 0xF0B5)
	    {
		   // digitalWrite(13, TRUE);
	        // Have start symbol, start collecting message
	        header = TRUE;
	        n_campioni = 0;
	        rx_length = 0;
	        rx_done = FALSE; // Too bad if you missed the last message
	    }
       
    }
 
}

BYTE get_message(BYTE* msg)
{
    

    // Message available?
    if (!rx_done)
	    return FALSE;
    for (int i=1; i<rx_length; i++)
        *msg=buffer_dati[i];    //copio il messaggio dal buffer dati all'array in uscita chiamante
    rx_busy=FALSE;
    rx_start();   //reinizializzo seriale ricezione
    return TRUE;

}



BOOL is_timer10ms_expired(BYTE timer)
{
    if (timer_10ms_array[timer])
        return 0;
    else 
        return 1;
}

BOOL is_timer1ms_expired(BYTE timer)
{
	if (timer_1ms_array[timer])
		return 0;
	else
		return 1;
}

void set_timer10ms(BYTE timer, WORD value)
{
    timer_10ms_array[timer]= value;
}


void set_timer1ms(BYTE timer, WORD value)
{
	timer_1ms_array[timer]= value;
}

BYTE strlenn(char* _string)
{
    BYTE lenght=0;
    while (*_string)//finche c'� qualcosa
    {
        lenght++;
        _string++;
    }
    return lenght;
}


 unsigned char const crc8_table[] = {
	0xea, 0xd4, 0x96, 0xa8, 0x12, 0x2c, 0x6e, 0x50, 0x7f, 0x41, 0x03, 0x3d,
	0x87, 0xb9, 0xfb, 0xc5, 0xa5, 0x9b, 0xd9, 0xe7, 0x5d, 0x63, 0x21, 0x1f,
	0x30, 0x0e, 0x4c, 0x72, 0xc8, 0xf6, 0xb4, 0x8a, 0x74, 0x4a, 0x08, 0x36,
	0x8c, 0xb2, 0xf0, 0xce, 0xe1, 0xdf, 0x9d, 0xa3, 0x19, 0x27, 0x65, 0x5b,
	0x3b, 0x05, 0x47, 0x79, 0xc3, 0xfd, 0xbf, 0x81, 0xae, 0x90, 0xd2, 0xec,
	0x56, 0x68, 0x2a, 0x14, 0xb3, 0x8d, 0xcf, 0xf1, 0x4b, 0x75, 0x37, 0x09,
	0x26, 0x18, 0x5a, 0x64, 0xde, 0xe0, 0xa2, 0x9c, 0xfc, 0xc2, 0x80, 0xbe,
	0x04, 0x3a, 0x78, 0x46, 0x69, 0x57, 0x15, 0x2b, 0x91, 0xaf, 0xed, 0xd3,
	0x2d, 0x13, 0x51, 0x6f, 0xd5, 0xeb, 0xa9, 0x97, 0xb8, 0x86, 0xc4, 0xfa,
	0x40, 0x7e, 0x3c, 0x02, 0x62, 0x5c, 0x1e, 0x20, 0x9a, 0xa4, 0xe6, 0xd8,
	0xf7, 0xc9, 0x8b, 0xb5, 0x0f, 0x31, 0x73, 0x4d, 0x58, 0x66, 0x24, 0x1a,
	0xa0, 0x9e, 0xdc, 0xe2, 0xcd, 0xf3, 0xb1, 0x8f, 0x35, 0x0b, 0x49, 0x77,
	0x17, 0x29, 0x6b, 0x55, 0xef, 0xd1, 0x93, 0xad, 0x82, 0xbc, 0xfe, 0xc0,
	0x7a, 0x44, 0x06, 0x38, 0xc6, 0xf8, 0xba, 0x84, 0x3e, 0x00, 0x42, 0x7c,
	0x53, 0x6d, 0x2f, 0x11, 0xab, 0x95, 0xd7, 0xe9, 0x89, 0xb7, 0xf5, 0xcb,
	0x71, 0x4f, 0x0d, 0x33, 0x1c, 0x22, 0x60, 0x5e, 0xe4, 0xda, 0x98, 0xa6,
	0x01, 0x3f, 0x7d, 0x43, 0xf9, 0xc7, 0x85, 0xbb, 0x94, 0xaa, 0xe8, 0xd6,
	0x6c, 0x52, 0x10, 0x2e, 0x4e, 0x70, 0x32, 0x0c, 0xb6, 0x88, 0xca, 0xf4,
	0xdb, 0xe5, 0xa7, 0x99, 0x23, 0x1d, 0x5f, 0x61, 0x9f, 0xa1, 0xe3, 0xdd,
	0x67, 0x59, 0x1b, 0x25, 0x0a, 0x34, 0x76, 0x48, 0xf2, 0xcc, 0x8e, 0xb0,
	0xd0, 0xee, 0xac, 0x92, 0x28, 0x16, 0x54, 0x6a, 0x45, 0x7b, 0x39, 0x07,
0xbd, 0x83, 0xc1, 0xff};


/*BYTE crc_update(BYTE msg, BYTE crc)
{
    crc^=msg;
    crc=look_up_table_crc8[(crc>>4)&0x0f][crc&0x0f];
    return crc;
}*/


BYTE crc_calc_bytearray(BYTE* array_to_transmit, BYTE length)
{
	BYTE crc =0x00;
	
	while (length--)
	{
		crc= crc8_table[crc ^ *array_to_transmit++];
		
	}
	return crc;

}

BYTE IsBtnPressed(void)
{
    if (BTN_PRESSED)
        return PREMUTO;
    else
        return RILASCIATO;
}
void prepara_adc()
{
	ADCSRA |=1<< ADEN;  //accendo adc
	ADMUX &= ~(1<<REFS0);
	ADMUX &= ~(1<<REFS1); //reference voltage = Vcc, AREF disconnected
	ADMUX |= ((1<<MUX3) | (1<<MUX2)); 
	ADMUX &= ~(1<<MUX1);
	ADMUX &= ~(1<<MUX0);  //canale 1100 =Vbg (1.1V su attiny85)
	
	//abilito interrupt flag
	ADCSRA |= 1<<ADIE;
	
	set_timer1ms(TIMER_1MS_CICLO, TIME_2MS); //attendo 2ms per assestamento Vbg
	while(!is_timer1ms_expired(TIMER_1MS_CICLO))
	{
		
	}
	
	//avvio ADC
	n_conversioni_adc=0;
	battery_voltage=0;
	conversione_terminata=0;
	ADCSRA |= 1<<ADSC;
	

	
	
}

BYTE calcola_tensione_batteria()
{
	BYTE esci_loop=FALSE;
	BYTE real_battery_voltage=0;
	while(TRUE)
	{
		esci_loop= conversione_terminata;
		if (esci_loop)
		{
			battery_voltage = battery_voltage/32;
			/*faro i conti su 3,1v, VBG più bassa, cio 1V per attiny85, quindi 1*1023/3,1=330  +10 perchè se no  mi becca anche alcuni 3.2 e 3.3..*/
			//a tre voltre avrei Vbg*1023/3 = 443,  a 4,3V avrei 310, a 5v 265
			//mappo quindi  la scala 310-443 in 4.3-3
			if (battery_voltage>340)
				real_battery_voltage=30;
			else if (battery_voltage<237)
				real_battery_voltage=43;
			else
			{			
				real_battery_voltage= ((340 - battery_voltage) * (43 - 30) / (340 - 237)) + 30;
				if (real_battery_voltage==30)
					real_battery_voltage++;
			}
			break;	
		}
	}
	
	return real_battery_voltage;
	
	
}