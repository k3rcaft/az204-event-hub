package com.thanhquangvo.az204eventhub;

import com.thanhquangvo.az204eventhub.services.EventHubReceiverService;
import com.thanhquangvo.az204eventhub.services.EventHubSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Az204EventHubApplication implements CommandLineRunner {

    @Autowired
    EventHubSenderService eventHubSenderService;

    @Autowired
    EventHubReceiverService eventHubReceiverService;

    public static void main(String[] args) {
        SpringApplication.run(Az204EventHubApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        eventHubReceiverService.receiveEvents();
        eventHubSenderService.publishEvents();
    }
}
