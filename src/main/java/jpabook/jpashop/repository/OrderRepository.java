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

    /*public List<Orders> findAll(OrderSearch orderSearch){
        return null;
    }*/

}
