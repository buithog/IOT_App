package com.example.iotapp;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.util.concurrent.CompletableFuture;


@Configuration
public class MqttBean {
    @Autowired
    private AppService appService;
    private CompletableFuture<Object> respondControlMessage = new CompletableFuture<>();

    public MqttPahoClientFactory mqttPahoClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] {
                "tcp://localhost:1884"
        });
        options.setUserName("thong");
        options.setPassword("thong1462002".toCharArray());
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannelSensor(){
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inboundSensor(){
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "tcp://localhost:1884",mqttPahoClientFactory(),"sensor","respondcontrol"
        );
        adapter.setCompletionTimeout(3000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannelSensor());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannelSensor")
    public MessageHandler handlerSensor(){
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                if (topic.equals("sensor")) {
                    appService.saveDataSensor(message.getPayload().toString());
                }
                if (topic.equals("respondcontrol")) {
                    respondControlMessage.complete(message.getPayload());
                }
                System.out.println(message.getPayload());
            }
        };
    }

    @Bean
    public MessageChannel  mqttOutBoundChannel(){
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutBoundChannel")
    public MessageHandler mqttOutBound(){
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut" , mqttPahoClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("control");
        return messageHandler;
    }
    public CompletableFuture<Object> getRespondControlMessage() {
        return respondControlMessage;
    }
    public void setRespondControlMessage(CompletableFuture<Object> respondControlMessage) {
        this.respondControlMessage = respondControlMessage;
    }
}
