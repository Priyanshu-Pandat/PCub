package com.pcub.WebSocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private  final   DriverLocationHandler driverLocationHandler;

    public WebSocketConfig(DriverLocationHandler driverLocationHandler) {
        this.driverLocationHandler = driverLocationHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(driverLocationHandler, "/ws/driver")
                .setAllowedOrigins("*"); // for dev only; restrict in prod
    }
}

