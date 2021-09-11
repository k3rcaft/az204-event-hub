package com.thanhquangvo.az204eventhub.services.impls;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.thanhquangvo.az204eventhub.services.EventHubSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EventHubSenderServiceImpl implements EventHubSenderService {

    @Value("${EH_CONNECTION_STRING}")
    String connectionString;
    String eventHubName = "cinema";

    @Override
    public EventHubProducerClient getEventHubClient() {
        System.out.println(connectionString);
        return new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }

    @Override
    public void publishEvents() {
        var ehClient = getEventHubClient();

        List<EventData> events = Arrays.asList(
                new EventData("The Matrix Resurrections"),
                new EventData("Godzilla vs. Kong"),
                new EventData("Black Adam"));

        var eventBatch = ehClient.createBatch();

        for (EventData event : events) {
            if (!eventBatch.tryAdd(event)) {
                ehClient.send(eventBatch);
                eventBatch = ehClient.createBatch();

                if (!eventBatch.tryAdd(event)) {
                    throw new IllegalArgumentException("Event is too large for en empty batch. Max size: " + eventBatch.getMaxSizeInBytes());
                }
            }
        }

        if (eventBatch.getCount() > 0)
            ehClient.send(eventBatch);

        ehClient.close();
    }
}
