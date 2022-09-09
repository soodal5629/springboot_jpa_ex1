package jpabook.jpashop.api;

import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Orders> ordersV1(){
        List<Orders> all = orderRepository.findAllByString(new OrderSearch());
        for (Orders orders : all) {
            orders.getMember().getUsername();
            orders.getDelivery().getAddress();
            List<OrderItem> orderItems = orders.getOrderItems();
            // OrderItem 뿐만 아니라 Item도 LAZY 이므로 강제 초기화
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }
}
