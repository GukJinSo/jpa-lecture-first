package gukjin.jpa.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gukjin.jpa.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue @Column(name = "order_item_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;
    private int count;

    public void cancel() {
        getItem().addStock(count);
    }

    /***
     * 주문 아이템 생성 메서드
     */
    public static OrderItem createOrderItem (Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setCount(count);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setItem(item);

        item.removeStock(count);
        return orderItem;
    }

    /***
     * 가격 조회
     * count * orderPrice
     */
    public int getTotalPrice() {
        return count * orderPrice;
    }
}
