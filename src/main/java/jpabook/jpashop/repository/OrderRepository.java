package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Orders order){
        em.persist(order);
    }

    public Orders findOne(Long id) {
        return em.find(Orders.class, id);
    }

    /* 검색 - 동적 쿼리 필요 */
    public List<Orders> findAll(OrderSearch orderSearch){
        return em.createQuery("select o from Orders o join o.member m"
                +" where o.status = :status "
                + "and m.username like :name", Orders.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }

    /* JPQL로 처리 */
    public List<Orders> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Orders o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.username like :name";
        }
        TypedQuery<Orders> query = em.createQuery(jpql, Orders.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    /* QueryDSL 사용 */
    public List<Orders> findAllQueryDSL(OrderSearch orderSearch){
        QOrders order = QOrders.orders;
        QMember member = QMember.member;

        JPAQueryFactory query = new JPAQueryFactory(em);
        return query.select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch, member))
                .limit(1000)
                .fetch();   // jpql로 바뀌어서 실행됨
    }

    /* 동적 쿼리 */
    private BooleanExpression nameLike(OrderSearch orderSearch, QMember member){
        if(!StringUtils.hasText(orderSearch.getMemberName())){
            return null;
        }
        return member.username.like(orderSearch.getMemberName());
    }
    /* 동적 쿼리 */
    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }
        return QOrders.orders.status.eq(statusCond);
    }
    public List<Orders> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Orders o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Orders.class
        ).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.username, o.orderDate, o.status, d.address)"
                + "from Orders o"
                + " join o.member m"
                + " join o.delivery d", OrderSimpleQueryDto.class
                ).getResultList();
    }


    public List<Orders> findAllWithItem() {
        // distinct 를 넣어주면
        // 1. sql에 distinct를 넣어서 쿼리문을 날려주고
        // 2. 중복을 걸러서 Collection 에 담아줌
        return em.createQuery("select distinct o from Orders o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Orders.class)
                .getResultList();
    }

    /* toOne 관계 페치조인 + 페이징 처리 */
    public List<Orders> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Orders o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Orders.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
