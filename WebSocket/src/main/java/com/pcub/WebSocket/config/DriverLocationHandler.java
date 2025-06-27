package com.pcub.WebSocket.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcub.WebSocket.dtos.DriverLocationUpdateDTO;
import com.pcub.WebSocket.service.DriverLocationService;
import com.pcub.WebSocket.service.RideSessionService;

import com.pcub.WebSocket.utils.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Arrays;

@Component
@Log4j2
public class DriverLocationHandler extends TextWebSocketHandler {

    private final DriverLocationService driverLocationService;
    private final RideSessionService rideSessionService;
    private final JwtUtil jwtUtil;

    public DriverLocationHandler(DriverLocationService driverLocationService,
                                 RideSessionService rideSessionService,
                                 JwtUtil jwtUtil) {
        this.driverLocationService = driverLocationService;
        this.rideSessionService = rideSessionService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        String query = uri.getQuery();
        String token = Arrays.stream(query.split("&"))
                .filter(p -> p.startsWith("token="))
                .map(p -> p.split("=")[1])
                .findFirst().orElse(null);

        if (token == null || !jwtUtil.validateToken(token)) {
            session.close();
            return;
        }

        String driverId = jwtUtil.getDriverIdFromToken(token);
        session.getAttributes().put("driverId", driverId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String driverIdStr = (String) session.getAttributes().get("driverId");
        if (driverIdStr == null) {
            log.warn("Missing driverId in WebSocket session");
            return;
        }

        Long driverId = Long.valueOf(driverIdStr);

        JsonNode node = new ObjectMapper().readTree(message.getPayload());
        double lat = node.get("lat").asDouble();
        double lon = node.get("lon").asDouble();
        DriverLocationUpdateDTO dto = new DriverLocationUpdateDTO(lat, lon);

        DriverLocationUpdateDTO updatedLocation = driverLocationService.updateDriverLocation(driverId, dto);

        // Push to ride topic if active ride found
        String rideId = rideSessionService.getRideIdForDriver(driverId);
        if (rideId != null) {
            log.info("Driver {} updated for ride {}", driverId, rideId);
        }
    }
}
