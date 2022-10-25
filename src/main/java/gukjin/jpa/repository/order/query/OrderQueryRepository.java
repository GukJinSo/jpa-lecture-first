package gukjin.jpa.repository.order.query;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos(){
        List<OrderQueryDto> orders = findOrders(); // query 1번
        orders.stream().forEach(o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // query n번 ( n+1 발생 )
            o.setOrderItems(orderItems);
        });
        return orders;
    }

    public List<OrderQueryDto> findFlat(){
        String jpql = "select new gukjin.jpa.repository.order.query.OrderFlatDto(o.id, m.name, o.status, d.address, o.orderDate, i.name, oi.count, oi.orderPrice)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i";
        List<OrderFlatDto> flats = em.createQuery(jpql, OrderFlatDto.class).getResultList();
        return flats.stream()
                .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), Collectors.toList())
                )).entrySet().stream()
                .map(e -> {
                    System.out.println(e.getValue());
                    return new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue());
                })
                .collect(Collectors.toList());
    }

    public List<OrderQueryDto> findMap(){

        // Order 가져오기
        List<OrderQueryDto> orders = findOrders();

        // orders의 ID로 리스트 생성
        List<Long> orderIds = orders.stream().map(order -> order.getOrderId()).collect(Collectors.toList());

        // in절을 통한 한 방 쿼리로 orderItems 가져옴
        List<OrderItemQueryDto> orderItems = em.createQuery("select new gukjin.jpa.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // orderItem의 orderId를 그룹으로 묶어 리스트 생성하고 키 값으로 orderId를 사용하는 맵 생성
        Map<Long, List<OrderItemQueryDto>> groupedByOrderId = orderItems.stream().collect(Collectors.groupingBy(orderItem -> orderItem.getOrderId()));

        // 연관관계 설정
        orders.stream().forEach(order -> order.setOrderItems(groupedByOrderId.get(order.getOrderId())));

        return orders;
    }

    public List<OrderItemQueryDto> findOrderItems(Long orderId){
        return em.createQuery("select new gukjin.jpa.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi"+
                        " join oi.item i"+
                        " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }


    public List<OrderQueryDto> findOrders() {
        return em.createQuery("select new gukjin.jpa.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class).getResultList();
    }
}
