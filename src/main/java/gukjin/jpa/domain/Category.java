package gukjin.jpa.domain;

import gukjin.jpa.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name="category_item",
        joinColumns = @JoinColumn(name = "category_id"), // 중간 테이블의 fk
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    public void addChildCategory(Category category){
        child.add(category);
        category.setParent(this);
    }

}
