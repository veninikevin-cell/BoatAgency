package com.example.lago.threading;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

// coda condivisa producer-consumer (wait/notify) per gli eventi di
// prenotazione in attesa di notifica. il thread della richiesta HTTP fa da
// producer, un thread consumer dedicato preleva e invia le notifiche
@Component
public class BookingQueue {

    private final Queue<BookingEvent> coda = new LinkedList<>();

    private static final int MAX = 100;

    public synchronized void produci(BookingEvent evento) throws InterruptedException {
        while (coda.size() >= MAX) {
            wait();
        }
        coda.add(evento);
        notifyAll();
    }

    public synchronized BookingEvent consuma() throws InterruptedException {
        while (coda.isEmpty()) {
            wait();
        }
        BookingEvent evento = coda.poll();
        notifyAll();
        return evento;
    }

    public synchronized int size() {
        return coda.size();
    }
}