#include "funzioni433.h"
#include "brugne433.h"
#include <avr/interrupt.h>

ISR(TIMER1_COMPA_vect)
{

     // rasp bit_ricevuto = digitalRead(rx_pin);

    // Do transmitter stuff first to reduce transmitter bit jitter due 
    // to variable receiver processing
    if (tx_busy && bit_inviato++ == 0) // al primo bit entro qui, poi per 7 volte di fila faccio solo il ++ dentro if (cioè spedisco 8 bit)
    {
       
      if (tx_index >= msg_length)  // messaggio finito
	       tx_stop();
		
        else
        {
		
			if (buffer_dati[tx_index] & (1<<campioni_8_bit))
				TX_HIGH;
			else
				TX_LOW;
			campioni_8_bit++;
			
	        if (campioni_8_bit > 7) //all'ottavo ciclo, ho su digitalwritye l'ultimo bit del byte, ed eseguo questo if, cioè passo al prossimo byte
	        {
			
	            campioni_8_bit = 0;
                tx_index++;
	        }
        }
    }
    if (bit_inviato > 7)
	{

	
	bit_inviato= 0; //spedisco nuovo bit dopo 8 cicli di interrupt
	//digitalWrite(13, TRUE);
	}

    if (rx_busy && !tx_busy) //se sono un ricevitore, filtro il bit in ingresso. ( verifico quanti degli 8 bit hanno lo stesso valore)
        filtra_messaggio(bit_ricevuto);

}

ISR(TIMER0_OVF_vect) //1ms
{

	TCNT0=248; //16 tick
	
    sei();
	ms_to_10ms ++;
	//timer a 1ms
	for(int i=0; i<MAX_TIMER_1MS; i++)
	{
		if (timer_1ms_array[i]>0)
			timer_1ms_array[i]--;
	}
	
	if (ms_to_10ms==10) //timer a 10ms
	{
		ms_to_10ms =0;
		for(int i=0; i<MAX_TIMER_10MS; i++)
		{
			if (timer_10ms_array[i]>0)
				timer_10ms_array[i]--;
		}
	
		//parte iniziale gestione bottone
		if(!is_timer10ms_expired(TIMER_1S_STARTUP))
		{
			if (IsBtnPressed()) 
			{
				set_timer10ms(TIMER_1S_STARTUP, TIME_10ms_10s);
				if (cont_btn_pressed<250)
					cont_btn_pressed++;
				if (cont_btn_pressed>=3) //30ms
				{
					cont_btn_released=0;
					btn_released=FALSE;
					btn_press = TRUE;
				}
				if (cont_btn_pressed>=200) //2sec
				{
					btn_press_2s = TRUE;
				}
			
			}
			else
			{
			   if (cont_btn_released<250)
					cont_btn_released++;
				if (cont_btn_released >=3) //30ms
				{
					btn_press=FALSE;
					 btn_press_2s=FALSE;
					 btn_press_5s=FALSE;
					 cont_btn_pressed=0;
					 btn_released=TRUE;
				
				}
           
			}
		}
    }
	
	//parte gestion apertura chiusura porta e mandata in sleep
	/*
	if (aperto_chiuso== APERTO) 
	{
		if (PORTD & PD3) //se è ancora aperto
		{
			if (cont_apertura_chiusura<4)//in 3 ms sono sicuro che è effettivamente aperta)
				cont_apertura_chiusura ++;
			else
			{
				going_to_sleep= TRUE;
				if (pacchetti_inviati<65000)
					pacchetti_inviati++;
				
			}
		}
		else
		{
			going_to_sleep = FALSE;
			aperto_chiuso= CHIUSO;
			cont_apertura_chiusura=0;
		}
		
	}
	else //chiuso
	
	{
		if (!(PORTD & PD3)) //se è ancora chiuso
		{
			if (cont_apertura_chiusura<4)//in 3 ms sono sicuro che è effettivamente chiuso)
				cont_apertura_chiusura ++;
			else
			{
				going_to_sleep= TRUE;
			}
		}
		else
		{
			going_to_sleep = FALSE;
			aperto_chiuso= APERTO;
			cont_apertura_chiusura=0;
		}
		
		
	}
	*/
}
ISR(ADC_vect)
{
	WORD loc_conversion_value=0;
	n_conversioni_adc++;
	
	if (n_conversioni_adc>32) //le prime 32 le scarto, poi ne accumulo 32 e divido per 32)
	{
		
		loc_conversion_value = ADCL;
		loc_conversion_value |= (ADCH<<8);
		battery_voltage += loc_conversion_value;
		if (n_conversioni_adc>=64)
		{
			conversione_terminata=TRUE;
			ADCSRA &= ~(1<<ADIE); //disabilito interrupt + disabilito conversioni+spengo adc
			ADCSRA &= ~(1<<ADSC);
			ADCSRA &= ~(1<< ADEN);  
		}
	}
	if (n_conversioni_adc<64)
		ADCSRA |= 1<<ADSC;
	
	
}

