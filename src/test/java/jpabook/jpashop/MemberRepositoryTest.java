package jpabook.jpashop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    // EntityManager를 통한 데이터 변경은 모두 트랜잭션 안에서 발생하므로 해당 어노테이션 필요
    // but 해당 어노테이션이 test에 있으면 실행 후, 롤백시키므로 데이터에 영향X
    @Transactional
    @Test
    void save() {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // 같은 영속성 컨텍스트이므로 같은 엔티티로 식별(1차 캐시)

    }

    @Test
    void find() {
    }
}