package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {
    private final MemberService memberService;

    /* 회원 등록 api */
    @PostMapping("/api/v1/members") // 엔티티 자체를 파라미터로 절대 받지 마세요!! --> api 스펙이 바뀔 수 있음
    // 또한 엔티티는 외부에 노출하면 안됨
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /*
    * 별도의 리퀘스트 DTO를 만듦 (엔티티와 프레젠테이션 계층 분리 가능)
    * 또한 엔티티와 API 스펙을 명확하게 분리 가능
    * 엔티티가 변경되어도 API 스펙은 변경되지 않는다는 장점이 존재
    * API는 리퀘스트, 리스판스 모두 DTO를 사용하자!
    * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setUsername(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
