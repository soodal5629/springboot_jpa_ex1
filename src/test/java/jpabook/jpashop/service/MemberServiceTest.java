package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class) // JUnit을 스프링과 엮어서 실행한다는 뜻
@SpringBootTest // 스프링부트를 띄운상태로 테스트를 하려면 해당 어노테이션이 필요 (이게 없으면 Autowired 다 실패함) - 스프링 컨테이너 안에서 테스트하므로
@Transactional // 이게 있어야 테스투 후, 롤백이 됨
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void join() throws Exception{
        //given
        Member member = new Member();
        member.setUsername("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setUsername("kim");

        Member member2 = new Member();
        member2.setUsername("kim");

        //when
        memberService.join(member1);
        try {
            memberService.join(member2); // IllegalStateException 예외가 터져야 한다!!
        }catch (IllegalStateException e){
            return; // IllegalStateException 예외발생하면 테스트 통과 --> return 시켜줌
        }

        //then
        fail("예외가 발생해야 한다.");

    }
}