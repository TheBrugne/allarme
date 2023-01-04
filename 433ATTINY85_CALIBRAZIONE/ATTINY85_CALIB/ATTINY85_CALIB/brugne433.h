#ifdef EXT
    #undef EXT
#endif
#ifdef _MAIN_433_
    #define EXT
#else
    #define EXT extern
#endif



EXT BYTE tx_busy;
EXT BYTE rx_busy;


EXT BYTE bit_ricevuto;
EXT BYTE bit_inviato;
EXT BYTE campioni_8_bit;
EXT BYTE msg_length;  //lunghezza totale msg (1header+ data+1 crc)
EXT BYTE rx_length; //lunghezza payload
EXT BYTE tx_index; //indice dell'array di byte da mandare


int main_loop(void);
void init_button(void);

