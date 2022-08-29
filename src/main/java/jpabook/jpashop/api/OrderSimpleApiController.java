package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
* xToOne 관계 성능 최적화(Orders는 Member와 다대일, Delivery와는 일대일 관계)
* Order
* Order -> Member
* Order -> Delivery
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Orders> ordersV1(){
        List<Orders> all = orderRepository.findAllByString(new OrderSearch());
        for(Orders order:all){
            order.getMember().getUsername(); // DB 조회하기 때문에 Lazy 강제 초기화
            order.getDelivery().getAddress(); // DB 조회하기 때문에 Lazy 강제 초기화
            // order.getMember(), order.getDelibery()는 사실 진짜가 아니고 프록시임
            // getUseranme, getAddress() 호출하면서 db에서 정보 조회하고 초기화함
        }
        return all;
    }


    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){ // 원래는 Result로 감싸서 return 시켜야 함
        List<Orders> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){ // fetch join 사용 - 성능 최적화
        List<Orders> orders = orderRepository.findAllWithMemberDelivery(); // 진짜 Member 객체랑 Delivery 객체가 같이 조회되므로 지연 로딩 자체가 일어나지 않음
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // 배송지 정보

        public SimpleOrderDto(Orders order) {
            orderId = order.getId();
            name = order.getMember().getUsername(); // LAZY 초기화 됨
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화 됨
        }
    }
}
