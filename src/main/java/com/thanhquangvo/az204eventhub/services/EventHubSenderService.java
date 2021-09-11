package com.thanhquangvo.az204eventhub.services;

import com.azure.messaging.eventhubs.EventHubProducerClient;

public interface EventHubSenderService {

    EventHubProducerClient getEventHubClient();

    void publishEvents();
}
