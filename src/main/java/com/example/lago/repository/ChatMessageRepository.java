package com.example.lago.repository;

import com.example.lago.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
// Accesso ai messaggi di chat, recuperabili in ordine cronologico per prenotazione. 

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByBookingIdOrderBySentAtAsc(Long bookingId);
}