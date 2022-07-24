package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepository {

    @PersistenceContext // jpa가 제공하는 표준 어노테이션 --> 스프링부트는 Autowired 생성자 인젝션 방식도 가능
    private EntityManager em; // 스프링이 Entity Manager 인젝션해줌

    public void save(Member member){
        em.persist(member); // jpa가 영속성 컨텍스트에 저장, 추후 commit 시점에 db에 저장
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); // 멤버 단건 조회
    }

    /* 모든 멤버 조회 */
    public List<Member> findAll(){
        /* JPQL - 파라미터 첫번째값 = jpql, 두번째값 = 반환 타입 */
        /* JPQL은 from의 대상이 테이블이 아닌, 엔티티 */
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from  Member m where m.username= :username",
                Member.class)
                .setParameter("username", name)
                .getResultList();
    }
}
