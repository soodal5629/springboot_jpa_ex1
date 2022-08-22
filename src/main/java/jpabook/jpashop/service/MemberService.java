package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // find와 같은 기능은 readOnly = true 넣으면 성능 최적화 가능
@RequiredArgsConstructor // final 가지고 있는 필드만 가지고 생성자를 만들어줌
public class MemberService {
    private final MemberRepository memberRepository; // 필드는 final 사용 권장

    /**
     * 회원 가입
     */
    // 모든 데이터 변경은 트랜잭션 안에서 일어나야 함
    @Transactional // readOnly => true 되면 데이터 변경 일어나지 않음
    // @Transactional의 default는 readOnly = false
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /*
    * 중복회원 검증
    * */
    private void validateDuplicateMember(Member member) {
        // exception
        List<Member> findMembers = memberRepository.findByName(member.getUsername());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    /**
     * 회원 단건 조회
     */
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    /* 회원정보 수정 -> 변경감지 사용 */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id); // 영속상태
        member.setUsername(name);
    }
}
