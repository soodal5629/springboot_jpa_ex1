package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
/* 엔티티가 아닌, 화면이나 API에 의존적 */
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach( o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi"+
                                " join oi.item i"+
                                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    /* fetch join 안하는 이유 */
    /*
    * 여기서는 엔티티를 조회하는 것이 아니라 원하는 데이터를 모두 조인해서 한번에 필요한 데이터만 조회하는 방식을 사용했습니다.
      이러한 조회 방식을 DTO로 조회하는 방식이라고 일반적으로 이야기합니다.
      이렇게 DTO로 조회하게 되면 엔티티가 아닙니다. 따라서 지연로딩, Fetch join 등을 사용할 수 없습니다.
      이렇게 DTO로 조회하려면 SQL의 JOIN 문을 사용해서 처음부터 원하는 데이터를 모두 선택해서 조회해야 합니다.
    * */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.username, o.orderDate, o.status, d.address)" +
                                " from Orders o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class) // fetch join 아님
                .getResultList();
    }
}
