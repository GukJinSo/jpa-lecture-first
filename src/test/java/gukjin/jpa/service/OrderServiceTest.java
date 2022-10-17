package gukjin.jpa.service;

import gukjin.jpa.domain.*;
import gukjin.jpa.domain.exception.NotEnoughStockException;
import gukjin.jpa.domain.item.Book;
import gukjin.jpa.domain.item.Item;
import gukjin.jpa.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(readOnly = true)
public class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private EntityManager em;

    // 상품 주문 성공
    // 상품을 주문할 때 재고 수량을 초과하면 안 된다
    // 주문 취소가 성공해야 한다
    @Test
    @Transactional
    public void 주문성공() throws Exception{
        //given
        Member member = createMember("소국진");

        Book book = createBook(10, "JPA 실전", 10000);

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), 3);

        //then
        Order findOrder = orderService.findOne(orderId);
        assertEquals("주문의 아이디는 같아야 한다", orderId, findOrder.getId());
        assertEquals("주문 아이템은 1종이다", 1, findOrder.getOrderItems().size());
        assertEquals("주문 수량은 orderCount이어야 한다", 3, findOrder.getOrderItems().get(0).getCount());
        assertEquals("주문 가격은 price * orderCount이다", 3 * book.getPrice(), findOrder.getTotalPrice());
        assertEquals("주문 상태는 ORDER이어야 한다", OrderStatus.ORDER, findOrder.getStatus());
        assertEquals("재고가 orderCount만큼 줄어야 한다", 10 - 3, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    @Transactional
    public void 재고이상주문실패() throws Exception{

        //given
        Member member = createMember("소국진");
        Item item = createBook(10, "JPA 실전", 10000);

        //when
        int orderCount = 12;

        //then
        orderService.order(member.getId(), item.getId(), orderCount);

        fail("재고 이상의 주문은 실패해야 한다");
    }

    @Test
    @Transactional
    public void 주문취소() throws Exception{
        //given
        Member member = createMember("소국진");
        Item item = createBook(10, "JPA 실전", 10000);
        Long orderId = orderService.order(member.getId(), item.getId(), 4);

        orderService.orderCancel(orderId);

        Order findOrder = orderService.findOne(orderId);
        assertEquals("주문 취소 시 상태는 CANCEL", OrderStatus.CANCEL, findOrder.getStatus());
        assertEquals("취소된 갯수만큼 재고를 원복한다", 10, item.getStockQuantity());
    }

    private Book createBook(int bookQuantity, String name, int price) {
        Book book = new Book();
        book.setStockQuantity(bookQuantity);
        book.setName(name);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("Daego", "Yulha street", "1042-3"));
        em.persist(member);
        return member;
    }
}