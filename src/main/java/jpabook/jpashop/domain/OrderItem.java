package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn("item_id")
    private Item item;

    @ManyToOne
    @JoinColumn("order_id")
    private Orders order;

    private int orderPrice; // 주문가격
    private int count; // 주문 수량
}
