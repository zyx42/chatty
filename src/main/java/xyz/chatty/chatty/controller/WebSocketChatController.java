package xyz.chatty.chatty.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.chatty.chatty.model.ChatMessage;
import xyz.chatty.chatty.util.ActiveUserChangeListener;
import xyz.chatty.chatty.util.ActiveUserManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Set;

@Controller
public class WebSocketChatController implements ActiveUserChangeListener {

    private final ActiveUserManager activeUserManager;

    public WebSocketChatController(ActiveUserManager activeUserManager, SimpMessagingTemplate webSocket) {
        this.activeUserManager = activeUserManager;
        this.webSocket = webSocket;
    }

    private final SimpMessagingTemplate webSocket;

    @PostConstruct
    private void init() {
        activeUserManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeUserManager.removeListener(this);
    }

    @GetMapping({"/", "/index"})
    public String getWebSocketWithSockJs() {
        return "chatty";
    }

    @MessageMapping("/chat")
    public void send(SimpMessageHeaderAccessor sha, @Payload ChatMessage chatMessage) throws Exception {
        String sender = sha.getUser().getName();
        ChatMessage message = new ChatMessage(chatMessage.getFrom(), chatMessage.getText(), chatMessage.getRecipient());
        if(!sender.equals(chatMessage.getRecipient())) {
            webSocket.convertAndSendToUser(sender, "/queue/messages", message);
        }

        webSocket.convertAndSendToUser(chatMessage.getRecipient(), "/queue/messages", message);
    }

    @Override
    public void notifyActiveUserChange() {
        Set<String> activeUsers = activeUserManager.getAll();
        webSocket.convertAndSend("/topic/active", activeUsers);
    }
}
