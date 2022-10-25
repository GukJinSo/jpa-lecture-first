package gukjin.jpa.repository.order.query;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderFlatDto {

    private Long orderId;
    private String name;
    private OrderStatus orderStatus;
    private Address address;
    private LocalDateTime orderDate;

    private String itemName;
    private int count;
    private int orderPrice;

    public OrderFlatDto(Long orderId, String name, OrderStatus orderStatus, Address address, LocalDateTime orderDate, String itemName, int count, int orderPrice) {
        this.orderId = orderId;
        this.name = name;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderDate = orderDate;
        this.itemName = itemName;
        this.count = count;
        this.orderPrice = orderPrice;
    }
}
