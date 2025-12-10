package com.eric.shopmall.websocket;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class WebSocketStatusHandler {
    // SessionId -> UserId 的映射
    public static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // 這裡需要 Spring Security 在 WS 握手時正確設定 headerAccessor.getUser()
        String userId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : null;
        String sessionId = headerAccessor.getSessionId();

        if (userId != null) {
            onlineUsers.put(sessionId, userId);
            System.out.println("User connected: " + userId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String userId = onlineUsers.remove(sessionId);

        if (userId != null) {
            System.out.println("User disconnected: " + userId);
        }
    }
}