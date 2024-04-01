#include <WiFi.h>
#include <PubSubClient.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <String.h>
#include <ArduinoJson.h>
#define DHTPIN            15         // Pin which is connected to the DHT sensor.
#define DHTTYPE           DHT11   
DHT dht(DHTPIN, DHTTYPE);
const unsigned long interval = 15000; // Đặt thời gian giữa các lần publish (miliseconds)
unsigned long previousMillis = 0;      // Biến lưu thời điểm lần cuối cùng publish
const int photoresistorPin = 34;
const int led1Pin = 2;
const int led2Pin = 4;

const char* ssid = "FPT Free Wifi";
const char* password = "yeumixoan";
const char* mqtt_server = "192.168.7.185";
const char* mqtt_username = "thong";
const char* mqtt_password = "thong1462002";

float temperature;
float humidity;
int lux;
int statusfan = 0;
int statuslight = 0;

WiFiClient espClient;
PubSubClient client(espClient);

void setup() {
  dht.begin();
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1884);  
  client.setCallback(callback); // Đăng ký hàm callback
  pinMode(led1Pin,OUTPUT);
  pinMode(led2Pin,OUTPUT);
}

void setup_wifi() {
  delay(10);
  Serial.println();
  Serial.print("Connecting to ");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  statuslight = ((char)payload[1]) - 48;
  statusfan = ((char)payload[2]) - 48;
  digitalWrite(led1Pin, statusfan == 1 ? HIGH : LOW);
  digitalWrite(led2Pin, statuslight == 1 ? HIGH : LOW);
  client.publish("respondcontrol","ok");
}


void reconnect() {
  while (!client.connected()) {
    Serial.println("Attempting MQTT connection...");
    if (client.connect("ESP32Client", mqtt_username, mqtt_password)) {
      Serial.println("connected");
      client.subscribe("control");
    } else {
      Serial.println("failed, rc=");
      Serial.println(client.state());
      Serial.println(" try again in 5 seconds");
      delay(500);
    }
  }
}
void loop() {
  unsigned long currentMillis = millis(); // Lấy thời điểm hiện tại
  if (currentMillis - previousMillis >= interval) { // Kiểm tra nếu đã đến thời điểm publish tiếp theo
    previousMillis = currentMillis; // Cập nhật thời điểm lần cuối cùng publish
    if (!client.connected()) {
      reconnect(); // Kết nối lại nếu cần thiết
    }
    client.loop();
    
    humidity = dht.readHumidity();
    temperature = dht.readTemperature();
    lux = analogRead(photoresistorPin);
    
    DynamicJsonDocument dataSensor(1024);

    if (isnan(temperature) || isnan(humidity) || isnan(lux)) {
      Serial.println(F("Failed to read from DHT sensor!"));
    }else{
        dataSensor["temperture"] = temperature;
        dataSensor["humidity"] = humidity;
        dataSensor["lux"] = lux;
    }
    String dataSensorStr;
    serializeJson(dataSensor,dataSensorStr);
    client.publish("sensor", dataSensorStr.c_str());
  }
}