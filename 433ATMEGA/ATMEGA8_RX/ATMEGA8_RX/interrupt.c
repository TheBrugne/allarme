#include "funzioni433.h"
#include "brugne433.h"
#include <avr/interrupt.h>

ISR(TIMER1_COMPA_vect)
{

     bit_ricevuto = digitalRead(rx_pin);

    // Do transmitter stuff first to reduce transmitter bit jitter due 
    // to variable receiver processing
    if (tx_busy && bit_inviato++ == 0) // al primo bit entro qui, poi per 7 volte di fila faccio solo il ++ dentro if (cioè spedisco 8 bit)
    {
       
      if (tx_index >= msg_length)  // messaggio finito
	       tx_stop();
		
        else
        {
		//digitalWrite(13, TRUE);
			
	        digitalWrite(tx_pin, (buffer_dati[tx_index] & (1<<campioni_8_bit))); //metto un bit del byte in posizione tx_index in uscita
			campioni_8_bit++;
			/*BYTE a = campioni_8_bit -1;
			
			if ((buffer_dati[tx_index]) & (1<<a))
			merda |= 1<<a;
			else
			merda &=~(1<<a);
		if (tx_index==1 && merda== 0x06)	digitalWrite(13, TRUE);
		*/
			
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

ISR(TIMER0_OVF_vect)
{

	TCNT0=100;
    sei();
    for(int i=0; i<MAX_TIMER_10MS; i++)
    {
        if (timer_10ms_array[i]>0)
            timer_10ms_array[i]--;
		if (packet_to_raspberry && is_timer10ms_expired(TIMER_10MS_PAUSE_TX))
			UCSRB |=1<<UDRIE; 
    }

    //parte iniziale gestione bottone
 
}
ISR(USART_UDRE_vect)
{
	UDR = buff_to_raspberry[ptr_to_buff_raspberry];
	ptr_to_buff_raspberry++;
	if (ptr_to_buff_raspberry>6)
	{
		ptr_to_buff_raspberry=0;
		packet_to_raspberry--;
		//if (!packet_to_raspberry)
		//UCSRB &=~(1<<UDRIE);
		UCSRB &=~(1<<UDRIE);
		set_timer10ms(TIMER_10MS_PAUSE_TX, TIME_10ms_01s);
	}
}