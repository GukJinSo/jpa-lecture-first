package gukjin.jpa.service;

import gukjin.jpa.domain.Delivery;
import gukjin.jpa.domain.Member;
import gukjin.jpa.domain.Order;
import gukjin.jpa.domain.OrderItem;
import gukjin.jpa.domain.item.Item;
import gukjin.jpa.repository.ItemRepository;
import gukjin.jpa.repository.MemberRepository;
import gukjin.jpa.repository.OrderRepository;
import gukjin.jpa.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;


    /***
     * 주문 생성
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);
        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        orderRepository.save(order); // cascade.ALL로 OrderItem, Delivery도 자동 persist

        return order.getId();
    }

    /***
     * 주문 취소
     */
    @Transactional
    public void orderCancel(Long orderId){
       // 엔티티 조회
       Order order = orderRepository.findOne(orderId);
       order.cancel();
    }
    /***
     * 조회
     */
    public Order findOne(Long orderId) {
        return orderRepository.findOne(orderId);
    }

    /***
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }

}
