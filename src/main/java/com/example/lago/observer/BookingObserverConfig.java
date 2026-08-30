package com.example.lago.observer;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

// registra gli observer su BookingEventManager all'avvio: se aggiungi un
// nuovo observer va aggiunto qui, altrimenti non riceve eventi

@Component
public class BookingObserverConfig {

    private final BookingEventManager eventManager;
    private final EmailNotificationObserver emailObserver;
    private final WebSocketNotificationObserver wsObserver;

    public BookingObserverConfig(BookingEventManager eventManager,
                                  EmailNotificationObserver emailObserver,
                                  WebSocketNotificationObserver wsObserver) {
        this.eventManager = eventManager;
        this.emailObserver = emailObserver;
        this.wsObserver = wsObserver;
    }

    @PostConstruct
    public void registraObservers() {
        eventManager.registra(emailObserver);
        eventManager.registra(wsObserver);
        System.out.println("[Observer] EmailObserver e WebSocketObserver registrati");
    }}