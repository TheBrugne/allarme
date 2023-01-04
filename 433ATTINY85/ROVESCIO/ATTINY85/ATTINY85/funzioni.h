#ifdef EXT
    #undef EXT
#endif
#ifdef _FUNZIONI_
    #define EXT
#else
    #define EXT extern
#endif

void SetRegister()