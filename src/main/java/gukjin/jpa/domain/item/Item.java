package gukjin.jpa.domain.item;

import gukjin.jpa.domain.Category;
import gukjin.jpa.domain.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {

    @Id @GeneratedValue @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList();

    // 비즈니스 로직 (도메인 주도 개발)

    /**
     * 재고 수량 증가
     */
    public void addStock(int quantity){
        stockQuantity += quantity;
    }

    /**
     * 재고 수량 감소, 체크
     */
    public void minusStock(int quantity){
        int restStock = stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
