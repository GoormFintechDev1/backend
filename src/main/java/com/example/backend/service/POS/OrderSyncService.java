package com.example.backend.service.POS;

import com.example.backend.dto.pos.OrderResponseDTO;
import com.example.backend.model.POS.Pos;
import com.example.backend.model.POS.PosSales;
import com.example.backend.model.enumSet.OrderStatus;
import com.example.backend.model.enumSet.PaymentStatus;
import com.example.backend.model.enumSet.PaymentTypeEnum;
import com.example.backend.repository.PosSalesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSyncService {

    private final PosOrderService posOrderService;
    private final PosSalesRepository posSalesRepository;

    private final Random random = new Random();

    // @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 */1 * * * *")
    public void syncOrders() {
        try {
            List<OrderResponseDTO> orders = posOrderService.fetchOrdersFromPos();
            boolean isUpdated = false;

            for (OrderResponseDTO order : orders) {
                boolean exists = posSalesRepository.existsByOrderTimeAndProductName(order.getOrderDate(), order.getProductName());
                if (!exists) {
                    Pos pos = new Pos();
                    pos.setPosId(order.getPosId()); // DTO에서 posId 가져오기

                    PaymentTypeEnum paymentType = random.nextBoolean() ? PaymentTypeEnum.CASH : PaymentTypeEnum.CARD;

                    PosSales posSales = PosSales.builder()
                            .posId(pos)
                            .orderTime(order.getOrderDate())
                            .totalPrice(BigDecimal.valueOf(order.getTotalPrice()))
                            .vatAmount(BigDecimal.valueOf(order.getTotalPrice()).multiply(BigDecimal.valueOf(0.1)))
                            .productName(order.getProductName())
                            .quantity(order.getQuantity())
                            .orderStatus(OrderStatus.valueOf(order.getOrderStatus()))
                            .paymentType(paymentType)
                            .paymentStatus(PaymentStatus.valueOf(order.getPaymentStatus()))
                            .build();
                    posSalesRepository.save(posSales);
                    isUpdated = true;
                }
            }

            if (isUpdated) {
                log.info("POS 주문 데이터 업데이트 완료!");
            }
        } catch (Exception e) {
            log.error("Error during syncOrders: {}", e.getMessage(), e);
        }
    }

}
