package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
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

}
