package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // Foreign key - 연관관계 주인
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) /* 일대다는 기본이 LAZY라서 따로 설정 안해도 됨 */
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문상태 (ORDER, CANCEL);

    /* 연관관계 편의 메소드 */
    public void addMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void addDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    /* 생성 메소드 */
    // 주문 생성 메소드
    public static Orders createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Orders orders = new Orders();
        orders.setMember(member);
        orders.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            orders.addOrderItem(orderItem);
        }
        orders.setStatus(OrderStatus.ORDER);
        orders.setOrderDate(LocalDateTime.now());
        return orders;
    }

    /* ======== 비즈니스 로직 ======== */

    /*
    * 주문취소
   * */
    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){ // 배송이 완료되면
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL); // 위 validation에 안걸리면 취소 상태로 변경해줌
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    /* ===== 조회로직 ===== */

    /*
    * 전체 주문 가격
    * */
    public int totalPrice(){
        /*int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        } 이 코드가 아래 return 값이랑 똑같음 */

        return orderItems.stream().
                mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
