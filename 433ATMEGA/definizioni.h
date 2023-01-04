

//processore
#define F_CPU 8000000UL
#define USART_BAUDRATE 2400
#define UBRR_VALUE (((F_CPU / (USART_BAUDRATE * 16UL))) - 1)

//tipi
#define BYTE unsigned char
#define WORD    unsigned int
#define SBYTE   signed char
#define SINT    short
#define LINT    long
#define LLINT   long long 
#define BOOL    unsigned char

//valori
#define FALSE 0
#define TRUE 1
#define INPUT 0
#define OUTPUT 1

//tempi a 10ms
#define TIME_10ms_10s   1000
#define TIME_10ms_5s   500
#define TIME_10ms_2s   200
#define TIME_10ms_1s   100
#define TIME_10ms_05s  50
#define TIME_10ms_01s  10
#define TIME_10ms_001s 1

//trasmissione
#define MAX_MESSAGE_LENGTH 20
#define HEADER_LENGTH 2
#define MESSAGE_LENGTH 2
#define CRC_LENGTH 1 //ho messo due inr ealtà è 1, ma ci va anche la lunghezza nel buffer dati.
#define STOP_LENGTH 2
#define HEADER 0x95

#define RAMP_MAX 160
#define RAMP_HALF 80
#define RAMP_RIT 11
#define RAMP_ANT 29
#define RAMP_DEF 20


#define RILASCIATO 0
#define PREMUTO 1


#define STARTUP_ENTRY 0
#define ATTESA_RILASCIO_STARTUP 1
#define INSERIMENTO_INDIRIZZO 2
#define VISUALIZZA_INDIRIZZO 3
#define VALIDA_INDIRIZZO 4



