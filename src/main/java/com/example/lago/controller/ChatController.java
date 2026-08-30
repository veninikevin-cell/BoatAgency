package com.example.lago.controller;

import com.example.lago.exception.UserNotFoundException;
import com.example.lago.model.ChatMessage;
import com.example.lago.model.User;
import com.example.lago.repository.ChatMessageRepository;
import com.example.lago.service.BookingService;
import com.example.lago.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.Map;

// chat in tempo reale legata a una prenotazione: rendering pagina (HTTP)
// e invio messaggi (WebSocket/STOMP), vedi WebSocketConfig per il broker
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepo;
    private final UserService userService;
    private final BookingService bookingService;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageRepository chatMessageRepo,
                          UserService userService,
                          BookingService bookingService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepo = chatMessageRepo;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/chat/{bookingId}")
    public String chatPage(@PathVariable Long bookingId,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model) {
        User currentUser = userService.getCurrentUser(userDetails);

        List<ChatMessage> messaggi = chatMessageRepo.findByBookingIdOrderBySentAtAsc(bookingId);

        // il destinatario dipende da chi è l'utente corrente: se ha prenotato lui,
        // il destinatario è il fornitore del tour, altrimenti è chi ha prenotato
        String receiverEmail = bookingService.findById(bookingId)
            .map(b -> b.getUser().getEmail().equals(currentUser.getEmail())
                ? b.getTour().getAgency().getOwner().getEmail()
                : b.getUser().getEmail())
            .orElse("");

        model.addAttribute("messaggi", messaggi);
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("receiverEmail", receiverEmail);

        return "chat/chat";
    }

    // riceve il messaggio via /app/chat.send, lo persiste e lo inoltra
    // sulla coda privata del destinatario (/user/{email}/queue/messages)
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, String> payload, Principal principal) {
        User sender = userService.findByEmail(principal.getName())
            .orElseThrow(() -> new UserNotFoundException(principal.getName()));

        String receiverEmail = payload.get("receiverEmail");
        String content = payload.get("content");
        Long bookingId = Long.parseLong(payload.get("bookingId"));

        User receiver = userService.findByEmail(receiverEmail)
            .orElseThrow(() -> new UserNotFoundException(receiverEmail));

        ChatMessage msg = new ChatMessage(sender, receiver, content, bookingId);
        chatMessageRepo.save(msg);

        messagingTemplate.convertAndSendToUser(
            receiverEmail,
            "/queue/messages",
            Map.of(
                "content", content,
                "senderEmail", sender.getEmail(),
                "senderName", sender.getFirstName(),
                "bookingId", bookingId
            ));
    }
}