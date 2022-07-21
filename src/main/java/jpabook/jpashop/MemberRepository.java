package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    /* 엔티티 매니저 */
    @PersistenceContext // 해당 어노테이션이 있으면 스프링 부트가 엔티티 매니저 주입해줌
    private EntityManager em;

    public Long save(Member member){
        em.persist(member);
        return member.getId();
    }

    /* 조회 */
    public Member find(Long id){
        return em.find(Member.class, id);
    }
}
