package com.exemple.demo.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.exemple.demo.model.EventMessage;
import com.exemple.demo.service.SseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubClientConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubClientConfiguration.class);
    private static final String EVENT_HUB_FULLY_QUALIFIED_NAMESPACE = "ridene-eventhubs-namespace.servicebus.windows.net";
    private static final String EVENT_HUB_NAME = "myeventhub";
    private static final String CONSUMER_GROUP = "$Default";
    private static final String STORAGE_ACCOUNT_ENDPOINT = "https://ridenestorageaccount.blob.core.windows.net";
    private static final String STORAGE_CONTAINER_NAME = "eventhubcontainer";

    @Bean
    EventHubClientBuilder eventHubClientBuilder() {
        return new EventHubClientBuilder()
                .credential(EVENT_HUB_FULLY_QUALIFIED_NAMESPACE, EVENT_HUB_NAME,
                        new DefaultAzureCredentialBuilder().build());
    }

    @Bean
    BlobContainerClientBuilder blobContainerClientBuilder() {
        return new BlobContainerClientBuilder()
                .credential(new DefaultAzureCredentialBuilder().build())
                .endpoint(STORAGE_ACCOUNT_ENDPOINT)
                .containerName(STORAGE_CONTAINER_NAME);
    }

    @Bean
    BlobContainerAsyncClient blobContainerAsyncClient(BlobContainerClientBuilder builder) {
        return builder.buildAsyncClient();
    }

    @Bean
    EventProcessorClientBuilder eventProcessorClientBuilder(BlobContainerAsyncClient blobClient) {
        return new EventProcessorClientBuilder()
                .credential(EVENT_HUB_FULLY_QUALIFIED_NAMESPACE, EVENT_HUB_NAME,
                        new DefaultAzureCredentialBuilder().build())
                .consumerGroup(CONSUMER_GROUP)
                .checkpointStore(new BlobCheckpointStore(blobClient));
    }

    @Bean
    EventHubProducerClient eventHubProducerClient(EventHubClientBuilder builder) {
        return builder.buildProducerClient();
    }

    @Bean
    EventProcessorClient eventProcessorClient(EventProcessorClientBuilder builder, SseService sseService) {
        return builder
                .processEvent(eventContext -> {
                    String partition = eventContext.getPartitionContext().getPartitionId();
                    long sequence = eventContext.getEventData().getSequenceNumber();
                    String body = eventContext.getEventData().getBodyAsString();
                    String time = eventContext.getEventData().getEnqueuedTime().toString();

                    EventMessage event = new EventMessage(partition, sequence, body, time);
                    LOGGER.info("Event received: Partition={}, Seq={}, Body={}, Time={}",
                            partition, sequence, body, time);

                    sseService.sendEvent(event);
                })
                .processError(errorContext -> LOGGER.error("Error in partition {}: {}",
                        errorContext.getPartitionContext().getPartitionId(),
                        errorContext.getThrowable()))
                .buildEventProcessorClient();
    }
}