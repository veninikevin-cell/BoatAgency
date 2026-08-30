package com.example.lago.threading;

import com.example.lago.observer.BookingEventManager;

// thread consumer che preleva eventi dalla coda condivisa e delega la notifica a BookingEventManager.
// l'invio email (lento) non blocca la richiesta HTTP che ha creato la prenotazione

public class BookingConsumer extends Thread {

    private final BookingQueue bookingQueue;
    private final BookingEventManager eventManager;

    // per fermare il thread in modo pulito
    private volatile boolean running = true;

    public BookingConsumer(BookingQueue bookingQueue, BookingEventManager eventManager) {
        this.bookingQueue = bookingQueue;
        this.eventManager = eventManager;
        this.setDaemon(true);
        this.setName("booking-consumer");
    }

    @Override
    public void run() {
        while (running) {
            try {
                // si blocca qui finché non arriva un evento in coda
                BookingEvent evento = bookingQueue.consuma();
                //long inizio = System.currentTimeMillis(); //per grafico mt
                eventManager.notifica(evento.getBooking(), evento.getEvento());
                //System.out.println("!!!!![MISURA] notifiche: " + (System.currentTimeMillis() - inizio) + " ms");//per grafico mt
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                // un errore sulla singola notifica non deve fermare il consumer
                System.out.println("[BookingConsumer] errore durante la notifica: " + e.getMessage());
            }
        }
    }

    public void ferma() {
        running = false;
        this.interrupt();
    }
}