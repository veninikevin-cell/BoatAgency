package com.example.lago.threading;

import com.example.lago.observer.BookingEventManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

// ciclo di vita del thread consumer: parte con l'app (PostConstruct),
// si ferma in modo pulito alla chiusura del contesto (PreDestroy) invece
// di lasciare il thread daemon appeso
@Component
public class BookingThreadManager {

    private final BookingQueue bookingQueue;
    private final BookingEventManager eventManager;
    private BookingConsumer consumer;

    public BookingThreadManager(BookingQueue bookingQueue, BookingEventManager eventManager) {
        this.bookingQueue = bookingQueue;
        this.eventManager = eventManager;
    }

    @PostConstruct
    public void avvia() {
        consumer = new BookingConsumer(bookingQueue, eventManager);
        consumer.start();
    }

    @PreDestroy
    public void ferma() {
        if (consumer != null) {
            consumer.ferma();
        }
    }
}