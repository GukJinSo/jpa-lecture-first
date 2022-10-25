package gukjin.jpa.repository.order.query;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderItemQueryDto {
    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }

}
