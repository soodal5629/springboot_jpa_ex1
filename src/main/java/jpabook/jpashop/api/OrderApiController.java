package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // Entity 직접 노출
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

    // DTO return
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Orders> orders = orderRepository.findAllByString(new OrderSearch());
        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
    }

    /* 페이징 처리 불가 but 쿼리 한방에 끝남 */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Orders> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
    }

    /* 페이징 처리 가능 - application.yml 파일에 default_batch_fetch_size: 100 설정 추가함 */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value="offset", defaultValue = "0") int offset,
                                        @RequestParam(value="limit", defaultValue = "100") int limit){
        // toOne 관계는 페치조인해도 페이징 처리 가능!
        List<Orders> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        return orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    /* 중복(뻥튀기) 없이 정규화된 데이터를 페이징 처리하여 가져올 수 있음 */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    // join을 하여 뻥튀기해서 데이터를 한번에 가져오기 때문에 sql문 한번만 실행됨
    // 아래 return 문에서 뻥튀기 데이터를 지지고 볶아서 api 스펙에 맞게 중복제거
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        // OrderFlatDto --> OrderQueryDto로 변환
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(),
                                o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(),
                                o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // 값타입은 DTO 안에 있어도 괜춘

        /* DTO를 반환할 땐 DTO 안에 OrderItem과 같은 Entity 가 있으면 안됨!!! */
        /* 엔티티에 대한 의존을 완전히 끊어내야 하기 떄문! */
        /* 따라서 OrderItem 조차도 DTO 로 다 변환해서 return 시켜줘야 함 */
        private List<OrderItemDto> orderItems;

        public OrderDto(Orders order) {
            orderId = order.getId();
            name = order.getMember().getUsername();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto{
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
