package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문 --> 데이터 변경이 일어남
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품을 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 새성
        Orders order = Orders.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order); // 얘 하나만 저장해도 OrderItem, Delivery도 저장됨(Cascade해줘서)
        return order.getId();
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(Long orderId){
        Orders order = orderRepository.findOne(orderId);

        // 데이터가 변하면 jpa가 알아서 update 쿼리를 날려줌(dirty checking..?)
        // ↑ jpa의 엄청난 강점
        order.cancel();
    }

    /*// 주문 검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }*/
}
