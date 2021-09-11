package com.thanhquangvo.az204eventhub.services;

import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.storage.blob.BlobContainerAsyncClient;

import java.io.IOException;

public interface EventHubReceiverService {

    BlobContainerAsyncClient getBlobContainerClient();

    EventProcessorClientBuilder getEventHubClient();

    void receiveEvents() throws IOException;
}
