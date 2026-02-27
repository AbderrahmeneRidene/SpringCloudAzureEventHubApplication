package com.exemple.demo;

import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.EventProcessorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class EventHubClientApplication
implements CommandLineRunner 
{

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubClientApplication.class);

    private final EventHubProducerClient producerClient;
    private final EventProcessorClient processorClient;

    public EventHubClientApplication(EventHubProducerClient producerClient,
                                    EventProcessorClient processorClient) {
        this.producerClient = producerClient;
        this.processorClient = processorClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(EventHubClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        processorClient.start();
        TimeUnit.SECONDS.sleep(10);
/*
        producerClient.send(Collections.singletonList(
                new com.azure.messaging.eventhubs.EventData("Hello World")));
        LOGGER.info("Message sent to Event Hubs.");
        producerClient.close();*/
    }
}