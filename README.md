## IOT Web Application

This is my IOT web application built with HTML, CSS and JavaScript for Front-end side. For the server side, I use Spring Boost and MySQL.

## Installation

Download this repo, extract and open it in Intellij. Open the terminal and type:


After that, it will install all the packages you need to run this application.

## Database Configuration

You need create database called "iot" and 2 tables: "action_history" (id, device_name, status, created_at) and "data_sensor" (id, temperature, humidity, light, created_at).

After that, you must connect your server with database, go into "server.js" file, change your database configuration:



## MQTT Configuration

You also need to config your mqtt client, change your option in mqtt connect function:

```JavaScript
const client = mqtt.connect("mqtt://<your mqtt host>:<your mqtt port number>/", {
  username: "<your mqtt username>",
  password: "<your mqtt password>",
});
```

## Run Front-end side

Open folder in Visual Studio Code and open index.html file. Right click and open with Live Server. After running that, you will get this interface:
![image](https://github.com/KiritoSAO148/IoT/assets/118887671/143b8a5b-58eb-4d1b-81f8-799799d695e1)
![image](https://github.com/KiritoSAO148/IoT/assets/118887671/9ad13973-3bcb-4ad9-9271-e0e7d19d473b)
![image](https://github.com/KiritoSAO148/IoT/assets/118887671/b04b72e9-3d74-47f3-a243-feb06138894b)
![image](https://github.com/KiritoSAO148/IoT/assets/118887671/7d8c2893-dfc7-483b-8d5c-8678758f722c)



## Run Back-end side
Go into "server.js" file and open the terminal, type:
```bash
npm start
```
Aftrt that, you will get:
![image](https://github.com/KiritoSAO148/IoT/assets/118887671/2ca70831-125b-473f-990f-2d03993ee364)


## Install Arduino

[Download Arduino here!](https://www.arduino.cc/en/software/).
[Installation guide](https://www.thegioididong.com/game-app/cach-tai-va-cai-dat-arduino-ide-nhanh-de-dang-1321845)
[Turn LED with ESP32](https://www.instructables.com/Blinking-an-LED-With-ESP32/)
[DHT 11 sensor with ESP32](https://randomnerdtutorials.com/esp32-dht11-dht22-temperature-humidity-sensor-arduino-ide/)
[Photoregister with ESP32](https://www.youtube.com/watch?v=0t-e2Dmz5TI)

```cpp
const char *ssid = "<YOUR_WIFI_SSID>";
const char *password = "<YOUR WIFI PASSWORD>";
const char *mqtt_broker = "<YOUR HOST>";
const char *topic = "devices/control";
const char *topicStatus = "devices/status";
const char *topicSensor = "huy/sensor";
const char *mqtt_username = "<YOUR MQTT USERNAME>";
const char *mqtt_password = "<YOUR MQTT PASSWORD>";
const int mqtt_port = <YOUR MQTT PORT NUMBER>;
```

Open file "sketch_feb15a.ino" and change your network id, network password, mqtt host broker, mqtt username, mqtt password and mqtt port number.

## API Documentations

[API Docs](https://documenter.getpostman.com/view/24287979/2sA35BbjDb)