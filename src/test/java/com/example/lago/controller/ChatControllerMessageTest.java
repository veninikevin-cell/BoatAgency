package com.example.lago.controller;

import com.example.lago.model.ChatMessage;
import com.example.lago.model.User;
import com.example.lago.repository.ChatMessageRepository;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import com.example.lago.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Unit test per la gestione dei messaggi WebSocket di ChatController 
@ExtendWith(MockitoExtension.class)
public class ChatControllerMessageTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingService bookingService;

    @InjectMocks
    private ChatController chatController;

    // TEST invio messaggio
    @Test
    void sendMessage_datiValidi_salvaEInoltra() {
        User sender = new User("m", "n", "m@test.it", "pass", User.Role.USER);
        User receiver = new User("n", "m", "n@test.it", "pass", User.Role.PROVIDER);

        Principal principal = () -> "m@test.it";

        Map<String, String> payload = Map.of(
            "receiverEmail", "n@test.it",
            "content", "Buongiorno, il tour è confermato?",
            "bookingId", "1"
        );

        when(userService.findByEmail("m@test.it")).thenReturn(Optional.of(sender));
        when(userService.findByEmail("n@test.it")).thenReturn(Optional.of(receiver));

        chatController.sendMessage(payload, principal);

        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
        verify(messagingTemplate, times(1)).convertAndSendToUser(
            eq("n@test.it"), eq("/queue/messages"), any(Map.class));
    }

    @Test
    void sendMessage_mittenteNonTrovato() {
        Principal principal = () -> "assente@test.it";

        Map<String, String> payload = Map.of(
            "receiverEmail", "a@test.it",
            "content", "Test",
            "bookingId", "1"
        );

        when(userService.findByEmail("assente@test.it")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> chatController.sendMessage(payload, principal));
    }

    @Test
    void sendMessage_destinatarioNonTrovato() {
        User sender = new User("q", "qq", "qq@test.it", "pass", User.Role.USER);
        Principal principal = () -> "qq@test.it";

        Map<String, String> payload = Map.of(
            "receiverEmail", "as@test.it",
            "content", "Test",
            "bookingId", "1"
        );

        when(userService.findByEmail("qq@test.it")).thenReturn(Optional.of(sender));
        when(userService.findByEmail("as@test.it")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> chatController.sendMessage(payload, principal));
    }
}