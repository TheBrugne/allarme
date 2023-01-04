#ifdef EXT
    #undef EXT
#endif
#ifdef _FUNZIONI433_
    #define EXT
#else
    #define EXT extern
#endif

#include "definizioni.h"

BYTE calcola_tensione_batteria(void);
void prepara_adc(void);
void baud_setup(WORD baud_rate);
void port_setup(void);
void go_to_sleep();
void start_timer1_compareA(void);
void init_timer1_compare(void);
void start_timer0_ovf(void);
void stop_timer0_ovf(void);
void init_timer0_ovf(void);


BOOL is_transmitting(void);
void tx_stop(void);
void filtra_messaggio(BYTE bit_rx);
BYTE get_message(BYTE* msg);
void rx_start();
void pinMode(BYTE pin, BOOL value); 
BYTE digitalRead(BYTE pin);
void digitalWrite(BYTE pin, BOOL value) ;

void send_packet(BYTE* msg, BYTE length);
BYTE crc_calc_bytearray(BYTE* msg, BYTE length);
BYTE crc_update(BYTE msg, BYTE crc);
BYTE IsBtnPressed(void);
void set_timer10ms(BYTE timer, WORD value);
void set_timer1ms(BYTE timer, WORD value);
BOOL is_timer10ms_expired(BYTE timer);
BOOL is_timer1ms_expired(BYTE timer);
BYTE strlenn(char* _string);


typedef enum
{
	
	TIMER_10MS_BTN_START,
	TIMER_1S_STARTUP,
	TIMER_1S_LED,
	MAX_TIMER_10MS
} t_10ms_timer;

typedef enum
{
	TIMER_1MS_CICLO,
	MAX_TIMER_1MS
} t_1ms_timer;

typedef enum
{
	PREPARE,
	CHECK_HEADER,
	CHECK_LENGTH,
	GET_PAYLOAD,
	CRC_VALIDATE
	
}stati_ricezione;


EXT volatile BYTE conversione_terminata;
 EXT volatile WORD timer_10ms_array[MAX_TIMER_10MS];
 EXT  volatile WORD timer_1ms_array[MAX_TIMER_1MS];
 EXT BYTE address_n;
EXT WORD battery_voltage;
EXT BYTE battery_voltage_to_transmit;
EXT BYTE n_conversioni_adc;
 EXT BYTE ms_to_10ms;

 EXT BYTE buffer_dati[HEADER_LENGTH+MESSAGE_LENGTH+CRC_LENGTH+ STOP_LENGTH];
 EXT BYTE buff_rx_circolare[50];
 EXT BYTE buff_rx[MESSAGE_LENGTH*10];
 EXT BYTE n_bit_alti;
 EXT BYTE n_bit_bassi;
 EXT BYTE previous_bit;
 EXT BYTE n_campioni;
 EXT BYTE header;
 EXT BYTE byte_count;
 EXT volatile BOOL rx_done;
EXT BYTE rampa;
EXT WORD data_bit;
EXT BYTE merda;
//EXT BYTE btn_state, old_btn_state;
EXT BYTE cont, cont_btn_pressed,cont_btn_released;
EXT BYTE cont_apertura_chiusura, aperto_chiuso;
EXT BYTE going_to_sleep;
EXT BYTE btn_press, btn_press_2s, btn_press_5s, btn_released;
EXT WORD msg_rec;
EXT  WORD pacchetti_inviati;

 