
#define _MAIN_433_
#include "definizioni.h"
#include "brugne433.h"
#include <avr/sleep.h>
#include "funzioni433.h"
#include <avr/interrupt.h>
#include <avr/eeprom.h>




//selezionare solo uno dei due.
#define TRASMETTITORE
//#define RICEVITORE

int main(void) 
{
	cli();
    init_timer1_compare(); //inizializzo timer 1 compare
    baud_setup(2000); //inizializzo ocr1a al valore corretto 
	port_setup(); //DDR, PU, PORT
	    init_timer0_ovf(); //inziializzo timer0 per ciclo main
	    //

	    //da raggruppare
	    ACSR |= 1<<ACD; //disable comparator
	    ADCSRA = 0;   //disable the ADC
	    WDTCR |= (1<<WDE) | (1<<WDCE); //abilito wtd altrimewnti non posso disabilitarlo
	    WDTCR &= ~(1<<WDE);  //spengo WTD
	    MCUCR |= 1<<SM1; //power down setup
	    MCUCR &= ~(1<<SM0);
	    MCUCR |= 1<<SE; //sleep enable bit
	    
	    sei(); //abilito interrupt globale
	    //eeprom_write_byte((BYTE*)0x02, 85);
	    OSCCAL= eeprom_read_byte((BYTE*)0x02);
	    //OSCCAL=82;
	    start_timer1_compareA(); //start timer interrupt
	    start_timer0_ovf(); //start timer interrupt
	    set_timer10ms(TIMER_10MS_BTN_START, TIME_10ms_002s); //guardo se nei primi 20ms il pulsante è premuto
	    BYTE contatore_pulsante=0;
	    //digitalWrite(13, FALSE);
	    //BYTE test= eeprom_read_byte((BYTE*)0x01);
	    //if (test==3)
	    //digitalWrite(13, TRUE);
	    //while(1);
	while(!is_timer10ms_expired(TIMER_10MS_BTN_START))
	{

		if (BTN_PRESSED)
			if (contatore_pulsante<200)
				contatore_pulsante++;
	}
	if (contatore_pulsante > 7)
	{
		 init_button();
	}
	else
		set_timer10ms(TIMER_1S_STARTUP, 0); //per sicurezza lo metto a zero così non interviene nell'interrupt timer.
	
	address_n= eeprom_read_byte((BYTE*)0x01);
	if (address_n==0 || address_n >15)
	{
		BYTE blink_count=20;
		BYTE led_on=FALSE;
		while (blink_count)
		{
			
			if (is_timer10ms_expired(TIMER_1S_LED))
			{
				blink_count--;
				
				if (led_on)
				{
					led_on=FALSE;
					LED_LOW;
				}

				else
				{
					led_on=TRUE;
					LED_HIGH;
					
				}
				
				set_timer10ms(TIMER_1S_LED, TIME_10ms_01s); //timer ciclo main
			}
			
			
		}
		go_to_sleep();
	}
	
	
	prepara_adc();

	battery_voltage_to_transmit= calcola_tensione_batteria();

    main_loop();
	
	
}

void init_button(void)
{
	static BYTE passo_bottone=STARTUP_ENTRY;
	BYTE indirizzo=0;
	BYTE indirizzo_copia = 0;
	BYTE led_on=FALSE;
	BYTE btn_state = PREMUTO;
	cont_btn_pressed=0;
	cont_btn_released=0;
	btn_press= FALSE;
	btn_press_2s=FALSE;
	btn_released=FALSE;
	BYTE blink_led_count=20;
	LED_HIGH;
	set_timer10ms(TIMER_1S_STARTUP, TIME_10ms_10s); //timer ciclo main
	while(1)
	{
		if (is_timer10ms_expired(TIMER_1S_STARTUP))
			break;
		else
		{
			switch (passo_bottone)
			{
				case STARTUP_ENTRY:
				{
					if (btn_released)
					{
						btn_state = RILASCIATO;
						passo_bottone = INSERIMENTO_INDIRIZZO;
						LED_LOW;
					}
				}
				break;
				case INSERIMENTO_INDIRIZZO:
				{
					if (btn_state == PREMUTO) //se è premuto, me poi lo rilascio, incremento indirizzo, tanto btn_press e btn_press_2s vengono resettati nel timer int
					{
						if (btn_released)
						{
							indirizzo++;
							btn_state = RILASCIATO;
						}
					}
					if (btn_press_2s)
					{
						
						LED_HIGH;
						if (!IsBtnPressed())
						{
							indirizzo_copia=indirizzo;
							passo_bottone = VISUALIZZA_INDIRIZZO;
							LED_LOW;
							set_timer10ms(TIMER_1S_LED, TIME_10ms_1s*3); //timer ciclo main
						}
					}
					else if (btn_press)
					{
						btn_state = PREMUTO;
					}
				}
				break;
				case VISUALIZZA_INDIRIZZO:
				{
					while (indirizzo)
					{
						if (is_timer10ms_expired(TIMER_1S_LED))
						{
							set_timer10ms(TIMER_1S_STARTUP, TIME_10ms_10s); //timer ciclo main
							if (led_on)
							{
								led_on=FALSE;
								LED_LOW;
							}

							else
							{
								led_on=TRUE;
								LED_HIGH;
								indirizzo --;
							}
							
							set_timer10ms(TIMER_1S_LED, TIME_10ms_1s); //timer ciclo main
						}
							
						

					}
					passo_bottone = VALIDA_INDIRIZZO;
					
				}
				break;

				case VALIDA_INDIRIZZO:
				{
					if (btn_press_2s)
					{
						if (indirizzo_copia<16 && indirizzo_copia>0)
							eeprom_write_byte((BYTE*)0x01, indirizzo_copia);
						passo_bottone = BLINK_LED_FAST;
						set_timer10ms(TIMER_1S_LED, TIME_10ms_01s); //timer ciclo main
					}

				}
				break;
				case BLINK_LED_FAST:
				{
					while (blink_led_count)
					{
						
						if (is_timer10ms_expired(TIMER_1S_LED))
						{
							blink_led_count--;
							
							if (led_on)
							{
								led_on=FALSE;
								LED_LOW;
							}

							else
							{
								led_on=TRUE;
								LED_HIGH;
								
							}
							
							set_timer10ms(TIMER_1S_LED, TIME_10ms_01s); //timer ciclo main
						}
						
						
					}
					LED_LOW;
					break;
				}
				break;


			}

		}

	}
	LED_LOW;
}
char *msg_ON = "LED_ON";
char *msg_OFF = "LED_OFF";
int main_loop(void) 
{
	buffer_dati[0]=0x60;
	buffer_dati[1]=0xA1;
	buffer_dati[2]=address_n;
	buffer_dati[3]=battery_voltage_to_transmit;
	// address_n| ((battery_voltage_to_transmit-30)<<4); //nibble basso indirizzo, nibble alto =0-13 tensione batteria , 0=30, 13=43
	//buffer_dati[0]=address_n;    //carico l'header nel pacchetto di trasmissione, rimane uguale ad ogni trasmissione
	//buffer_dati[1]=battery_voltage_to_transmit;
	//buffer_dati[2]=address_n;
	//buffer_dati[3]=battery_voltage_to_transmit;
	buffer_dati[4]=crc_calc_bytearray(&buffer_dati[0], HEADER_LENGTH+ MESSAGE_LENGTH);
	buffer_dati[5]=0x7E;
	buffer_dati[6]=0x0F;
  
    pacchetti_inviati=0;

	going_to_sleep = FALSE;
	cont_apertura_chiusura=0;
	aperto_chiuso= CHIUSO;
	//set_timer1ms(TIMER_1MS_CICLO, TIME_2MS);
	stop_timer0_ovf();
	tx_stop();
	//OSCCAL=0;
    while (1)
    {
		#ifdef TRASMETTITORE
		{

			VCC_HIGH;
			if(pacchetti_inviati>=150)
			{
				
				//OSCCAL++;
				pacchetti_inviati=0;
				going_to_sleep=FALSE;
				go_to_sleep();
				
			}
				
			//if (is_timer1ms_expired(TIMER_1MS_CICLO))
			//{
				
				if (!is_transmitting())
				{
					
					pacchetti_inviati ++;
					send_packet(&buffer_dati[0], HEADER_LENGTH+ MESSAGE_LENGTH+ CRC_LENGTH+ STOP_LENGTH);
					
					
					
				}
				//set_timer1ms(TIMER_1MS_CICLO, TIME_1MS);
				
				
			//}
			
		}
		#else 
		{
			rx_start();
			
		}
		#endif
		
    }
	return 0;
	
/*
	BYTE fatto=0;
	
	while (1)
	{
		fatto=rx_done;
        if (fatto)
         {
		 msg_rec++;
		 if (msg_rec>50000)
		 digitalWrite(13, TRUE);
            rx_start();
            msg_rec++;
            if (msg_rec>1 && msg_rec<4)
            {
                digitalWrite(13, TRUE);
                
            }
            else 
            {
                digitalWrite(13, FALSE);
                if (msg_rec >99)
                    msg_rec=0;
            }
			
            
        }
	}
	return 0;
	
	*/
	
}