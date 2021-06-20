#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>
#include <Wire.h>
#include "MAX30100_PulseOximeter.h"

const char *ssid = "MOVISTAR_casa";          //Nombre de la Red Wifi
const char *password = "*";                  //Password de la Red Wifi
const char* dbUser = "pr_bbrace";                //User para conectarse a la Base de Datos.
const char* dbPass = "BbRacE.44";                //Pasword del usuario
const char* dbName = "prbbrace";                //Base de datos de la tabla a insertar    
const int idSensorPuls=8; //pulsometro bebé Eva padre 33333333Y
const int idSensorOx=7; //oxímetro bebé Eva padre 33333333Y

#define tiempoEspera     60000
PulseOximeter pox;
uint32_t ultimaMedicion = 0;

void setup()
{
    initWifi();

    Serial.begin(115200);
    if (!pox.begin()) {
        Serial.println("FAILED");
        for(;;);
    } else {
        Serial.println("SUCCESS");
    }

}

void loop()
{

   if ( ( WiFi.status() == WL_CONNECTED ) ) {
    pox.update();

    if (millis() - ultimaMedicion > tiempoEspera) {
        
        float valorPuls= pox.getHeartRate();
        float valorOx= pox.getSpO2();

        //Si el valor medido es mayor que 50 insertamos. Así evitamos valores no deseados en la BBDD
        //Si el valor medido es mayor que 75 insertamos. Así evitamos valores no deseados en la BBDD

        if(valorPuls>50 && valorOx>75){
            ultimaMedicion = millis();  
            insertValueBBDD( valorPuls,idSensorPuls);
            insertValueBBDD( valorOx,idSensorOx);

          }
      }
    }
 else {
        Serial.println( "[HTTP} Unable to connect" );
    }
    delay( 10 );
    
}


/********************************************************************************/
//   Funcion de Inicializar Wifi.
/********************************************************************************/
void initWifi(){
#ifdef noDEBUG
    Serial.setDebugOutput( true );
    Serial.println("\n\n");
#endif

    WiFi.mode( WIFI_STA );
    WiFi.begin( ssid, password );

    // Wait for connection
#ifdef DEBUG
        Serial.print( "Conectando:");
#endif
    while ( WiFi.status() != WL_CONNECTED ){
#ifdef DEBUG
        Serial.print( ".");
#endif
        delay ( 250 );
    }
#ifdef DEBUG
    Serial.println();
    Serial.println( "[SETUP] Wifi Conectada...");
#endif
}
/********************************************************************************/
//   Funcion de insertar en la BBDD.
/********************************************************************************/
int insertValueBBDD (double valor, int idSensor) {
  
        String insert = String ( "datos(valor,idSensor) VALUES( " ) + valor+ ", " + idSensor + ")";
#ifdef DEBUG
    Serial.print ( "insert: " ); Serial.println ( insert );
#endif
    int code = sendSQLinsert ( insert );
#ifdef DEBUG
    Serial.print ( "InserDDBB: " ); Serial.println ( code );
#endif
    return code;
}

/********************************************************************************/

/********************************************************************************/
/********************************************************************************/
int sendSQLinsert ( String insert ) {
    String hostDDBB = "2.139.176.212";  // IP de MariaDB en la UEM
    const unsigned int TAM_BUFF = 250;
    char uri_[TAM_BUFF];
    const char *formatGET = "/insert.php?db=%s&user=%s&pass=%s&insert=%s";

    int httpCode;
    WiFiClient client;
    HTTPClient http;

    insert.trim();
    insert.replace ( " ", "%20" );
    insert.replace ( "'", "%27" );
    snprintf ( uri_, TAM_BUFF, formatGET, dbName, dbUser, dbPass, insert.c_str() );
#ifdef DEBUG
    Serial.print("URI: ");
    Serial.println(uri_);
#endif

    if ( http.begin ( client, hostDDBB, 8888, uri_, false ) )
    {
        httpCode = http.GET();  // Realizar petición
        delay ( 100 );
        http.end();
        return httpCode;
    }
    return -1;
}
/********************************************************************************/
/********************************************************************************/
