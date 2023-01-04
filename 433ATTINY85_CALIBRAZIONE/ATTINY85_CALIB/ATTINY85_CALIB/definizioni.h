

//processore
#define F_CPU 8000000UL
//modificare per release
#define TX_PIN PB1
#define LED_PIN PB4
#define VCC_PIN PB2
#define BTN_PIN PB3
//unused
#define RX_PIN 8

//definizioni simil funzioni
#define BTN_PRESSED (!(PINB & (1<< BTN_PIN)))
#define TX_HIGH (PORTB |= 1<<TX_PIN )
#define TX_LOW (PORTB  &= ~(1<<TX_PIN))
#define LED_HIGH (PORTB |= 1<<LED_PIN )
#define LED_LOW (PORTB  &= ~(1<<LED_PIN))
#define VCC_HIGH (PORTB |= 1<<VCC_PIN )
#define VCC_LOW (PORTB  &= ~(1<<VCC_PIN))
//tipi
#define BYTE    unsigned char
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
#define HIGH 1
#define LOW 0

//tempi a 10ms
#define TIME_10ms_10s  1000
#define TIME_10ms_3s   300
#define TIME_10ms_1s   100
#define TIME_10ms_05s  50
#define TIME_10ms_01s  10
#define TIME_10ms_002s 2
#define TIME_10ms_001s 1


#define TIME_1MS 1
#define TIME_2MS 2
#define TIME_5MS 5

//trasmissione
#define MESSAGE_LENGTH 2 //indirizzo e tensione batteria 
#define HEADER_LENGTH 2 //due byte header 60 A1
#define CRC_LENGTH 1 //crc1 byte
#define STOP_LENGTH 2 //0x7e 0F


#define RAMP_MAX 160
#define RAMP_HALF 80
#define RAMP_RIT 11
#define RAMP_ANT 29
#define RAMP_DEF 20


#define RILASCIATO 0
#define PREMUTO 1

#define APERTO 1
#define CHIUSO 0

//#define STARTUP_PRE_ENTRY 0
#define STARTUP_ENTRY 0
//#define ATTESA_RILASCIO_STARTUP 2
#define INSERIMENTO_INDIRIZZO 1
#define VISUALIZZA_INDIRIZZO 2
#define VALIDA_INDIRIZZO 3
#define BLINK_LED_FAST 4



