package com.example.lago.threading;

import com.example.lago.observer.BookingEventManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Unit test per il gestore del ciclo di vita del thread consumer
@ExtendWith(MockitoExtension.class)
public class BookingThreadManagerTest {

    @Mock
    private BookingEventManager eventManager;

    @Test
    void avvia_creaEavviaIlThreadConsumer() {
        BookingQueue queue = new BookingQueue();
        BookingThreadManager manager = new BookingThreadManager(queue, eventManager);

        manager.avvia();
        manager.ferma();
    }

    @Test
    void ferma_senzaAvvioPrecedente_nonSollevaEccezioni() {
        BookingQueue queue = new BookingQueue();
        BookingThreadManager manager = new BookingThreadManager(queue, eventManager);

        manager.ferma();
    }
}