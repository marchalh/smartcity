#include <WiFi101.h>

#include <dht.h>

dht DHT;

//Temp and humidity
#define DHT11_PIN 5

//Photoresistor
#define PHOTO_PIN A0

// Web REST API params
IPAddress local(192,168,137,255);
char* localip = "192.168.137.255"; 
IPAddress server(192,168,137,1);
char* host = "192.168.137.1";
char* urlPost = "/meteo/data";
String serv = "192.168.137.1:9000";  
int port = 9000; //(port 443 is default for HTTPS, 80 for HTTP)

boolean notOk;
 

WiFiClient client;
char ssid[] = "Connectify-hugh";      //  your network SSID (name)
char pass[] = "password";
int status = WL_IDLE_STATUS;

void setup() {

  Serial.begin(9600);
  // check for the presence of the shield:
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    while(true);  // don't continue
  }
  Serial.println("Attempting to connect to WPA network...");
  Serial.print("SSID: ");
  Serial.println(ssid);

  status = WiFi.begin(ssid, pass);
  delay(5000);
  if ( status != WL_CONNECTED) { 
    Serial.println("Couldn't get a wifi connection. Trying again in 10s.");
    // don't do anything else:
    delay(5000);
    setup();
  } 
  else {
    Serial.println("Connected to wifi");
    Serial.println("\nStarting connection...");
    // if you get a connection, report back via serial:
    if (client.connect(server, port)) {
      Serial.println("connected");
    }
  }
  pinMode(0, OUTPUT);
}


void loop() {
  Serial.println("Reading data");
  int chk = DHT.read11(DHT11_PIN);
  Serial.print("Temperature = ");
  int t = DHT.temperature;
  Serial.println(t);
  Serial.print("Humidity = ");
  int h = DHT.humidity;
  Serial.println(h);
  int p = analogRead(PHOTO_PIN);
  Serial.print("Photo resistor : ");
  Serial.println(p);
  notOk = false;
  if(t == -999){
      digitalWrite(0, HIGH);
      Serial.println("Le capteur d'humidité et de température est débranché");
      notOk=true;
  }
  if(p<100){
    digitalWrite(0,HIGH);
    Serial.println("La photo résistance a un problème");
    notOk = true;
  }
  if(notOk){
    //SEND HTTP GET TO SIGNAL LOSS OF DATA
            // Make a HTTP request:
//    status = WiFi.begin(ssid, pass);
//  if ( status != WL_CONNECTED) { 
//    Serial.println("Couldn't get a wifi connection. Trying again in 10s.");
//    // don't do anything else:
//    delay(10000);
//    setup();
//  } 
    boolean ok = client.connect(server, port);
    delay(2000);
    Serial.println(ok? "Connected" : "Failed connect");
    client.println("POST /meteo/lost HTTP/1.0");
    client.println("Host: 192.168.137.1");
    client.println("User-Agent: Arduino/1.0");
    client.println("Content-Type: text/plain");
    client.println("Content-Length: 0");
    client.println();
    client.println();
    while(t == -999 || p < 100){
      delay(2*1000);
      int dt = DHT.read11(DHT11_PIN);
      t = DHT.temperature;
      p = analogRead(PHOTO_PIN);
      Serial.println("Mauvaises données de température, d'humidité ou de luminosité");
    }
  }
  else{
    digitalWrite(0, LOW);
    
    char content[26] = "";
    char temp[2];
    char hum[2];
    char pho[4];
    sprintf(temp, "%d", (int)t);
    sprintf(hum, "%d", (int)h);
    sprintf(pho, "%d", (int)p);

    strcpy(content, localip);//15
    strcat(content, "/");//16
    strcat(content, temp);//18
    strcat(content, "/");//19
    strcat(content, hum);//21
    strcat(content, "/");//22
    strcat(content, pho);//25 - 26

    postRequest(host, urlPost, content);
  }
  delay(30*1000); // delay 1 min
}


void postRequest(const char* host, const char* resource, const char* content) {
  Serial.print("POST ");
  Serial.println(resource);
  Serial.print("content ");
  Serial.println(content);


//  status = WiFi.begin(ssid, pass);
//  if ( status != WL_CONNECTED) { 
//    Serial.println("Couldn't get a wifi connection. Trying again in 10s.");
//    // don't do anything else:
//    delay(1000);
//    setup();
//  } 
  boolean ok = client.connect(server, port);
  Serial.println(ok? "Connected" : "Failed connect");
  client.println("POST /meteo/data HTTP/1.0");
  client.println("Host: 192.168.137.1");
  client.println("User-Agent: Arduino/1.0");
  client.println("Content-Type: text/plain");
  client.print("Content-Length: ");
  client.println(strlen(content));
  client.println();
  client.println(content);
}


