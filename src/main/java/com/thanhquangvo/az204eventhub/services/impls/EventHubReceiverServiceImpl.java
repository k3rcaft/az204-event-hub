package com.thanhquangvo.az204eventhub.services.impls;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.messaging.eventhubs.models.PartitionContext;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.thanhquangvo.az204eventhub.services.EventHubReceiverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Consumer;

@Service
public class EventHubReceiverServiceImpl implements EventHubReceiverService {

    public static final Consumer<EventContext> PARTITION_PROCESSOR = eventContext -> {
        PartitionContext partitionContext = eventContext.getPartitionContext();
        EventData eventData = eventContext.getEventData();
        System.out.println("handle event");
        System.out.printf("Processing event from partition %s with sequence number %d with body: %s%n",
                partitionContext.getPartitionId(), eventData.getSequenceNumber(), eventData.getBodyAsString());

        // Every 10 events received, it will update the checkpoint stored in Azure Blob Storage.
        if (eventData.getSequenceNumber() % 10 == 0) {
            eventContext.updateCheckpoint();
        }
    };
    public static final Consumer<ErrorContext> ERROR_HANDLER = errorContext -> {
        System.out.printf("Error occurred in partition processor for partition %s, %s.%n",
                errorContext.getPartitionContext().getPartitionId(),
                errorContext.getThrowable());
    };
    @Value("${EH_CONNECTION_STRING}")
    String connectionString;
    @Value("${SA_CONNECTION_STRING}")
    String storageConnectionString;
    String eventHubName = "cinema";
    String storageContainerName = "eh-checkpoint";

    @Override
    public BlobContainerAsyncClient getBlobContainerClient() {
        return new BlobContainerClientBuilder()
                .connectionString(storageConnectionString)
                .containerName(storageContainerName)
                .buildAsyncClient();
    }

    @Override
    public EventProcessorClientBuilder getEventHubClient() {
        return new EventProcessorClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(EventHubClientBuilder.DEFAULT_CONSUMER_GROUP_NAME)
                .processEvent(PARTITION_PROCESSOR)
                .processError(ERROR_HANDLER)
                .checkpointStore(new BlobCheckpointStore(getBlobContainerClient()));

    }

    @Override
    public void receiveEvents() throws IOException {
        EventProcessorClient eventProcessorClient = getEventHubClient().buildEventProcessorClient();

        System.out.println("Starting event processor");
        eventProcessorClient.start();

//        System.out.println("Press enter to stop.");
//        System.in.read();
//
//        System.out.println("Stopping event processor");
//        eventProcessorClient.stop();
//        System.out.println("Event processor stopped.");
//
//        System.out.println("Exiting process");
    }
}
