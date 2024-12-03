package com.example.backend.service.POS;

import com.example.backend.dto.pos.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PosOrderService {
    @Qualifier("webClient8083")
    private final WebClient webClient;

    @Value("${pos.api.url.orders}")
    private String posOrdersUrl;

    public List<OrderResponseDTO> fetchOrdersFromPos() {
        return webClient.get()
                .uri(posOrdersUrl + "/all")
                .retrieve()
                .bodyToFlux(OrderResponseDTO.class)
                .collectList()
                .block();
    }
}
