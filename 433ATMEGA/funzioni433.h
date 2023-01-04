#ifdef EXT
    #undef EXT
#endif
#ifdef _FUNZIONI433_
    #define EXT
#else
    #define EXT extern
#endif

#include "definizioni.h"

void baud_setup(WORD baud_rate);
void start_timer1_compareA(void);
void init_timer1_compare(void);
void start_timer0_ovf(void);
void init_timer0_ovf(void);
void set_tx_pin(BYTE pin);
void set_rx_pin(BYTE pin);
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
BOOL is_timer10ms_expired(BYTE timer);

BYTE strlenn(char* _string);


typedef enum
{
	TIMER_10MS_STOP_RX,
	TIMER_10MS_PAUSE_TX,
	MAX_TIMER_10MS
} t_10ms_timer;

typedef enum
{
	PREPARE,
	CHECK_HEADER,
	CHECK_LENGTH,
	GET_PAYLOAD,
	CRC_VALIDATE
	
}stati_ricezione;

EXT BYTE n_3bit;
EXT BYTE n_3bit_alti;
EXT BYTE n_3bit_bassi;
 EXT WORD timer_10ms_array[MAX_TIMER_10MS];
 EXT BYTE buffer_dati[HEADER_LENGTH+MAX_MESSAGE_LENGTH+CRC_LENGTH];
 EXT BYTE buff_rx_circolare[50];
 EXT BYTE buff_rx[5];
 EXT BYTE buff_to_raspberry[HEADER_LENGTH+MESSAGE_LENGTH+CRC_LENGTH+STOP_LENGTH];
 EXT BYTE packet_to_raspberry;
 EXT BYTE ptr_to_buff_raspberry;
 EXT BYTE n_bit_alti;
 EXT BYTE n_bit_bassi;
 EXT BYTE previous_bit;
 EXT BYTE n_campioni;
 EXT BYTE header;
 EXT volatile BOOL rx_done;
EXT BYTE rampa;
EXT unsigned long long data_bit;
EXT BYTE merda;
EXT BYTE btn_state, old_btn_state;
EXT BYTE cont, cont_btn;
EXT BYTE btn_press, btn_press_3s, btn_press_5s;
EXT WORD msg_rec;


 