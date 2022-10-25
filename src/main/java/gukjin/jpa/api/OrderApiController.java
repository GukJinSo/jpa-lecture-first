package gukjin.jpa.api;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.Order;
import gukjin.jpa.domain.OrderItem;
import gukjin.jpa.domain.OrderStatus;
import gukjin.jpa.domain.item.Item;
import gukjin.jpa.repository.OrderRepository;
import gukjin.jpa.repository.OrderSearch;
import gukjin.jpa.repository.order.query.OrderQueryDto;
import gukjin.jpa.repository.order.query.OrderQueryRepository;
import gukjin.jpa.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    private final OrderQueryRepository orderQueryRepository;


    /**
     * 강제 초기화를 통한 엔티티 직접 반환 쿼리
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기환
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); //Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        return orderRepository.findAllByString(new OrderSearch())
                .stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return orderRepository.findAllWithItem(new OrderSearch())
                .stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        return orderRepository.findAllWithMemberDelivery(offset, limit)
                .stream().map(order -> new OrderDto(order)).collect(Collectors.toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }


    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findMap();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        return orderQueryRepository.findFlat();
    }

    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{
        private Long orderItemId;
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            orderItemId = orderItem.getId();
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}