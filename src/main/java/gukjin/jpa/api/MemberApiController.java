package gukjin.jpa.api;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.Member;
import gukjin.jpa.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse savedMemberV1(@RequestBody @Valid Member member){ // json으로 온 body data를 바로 Member에 넣겠다
        Long joinId = memberService.join(member);
        return new CreateMemberResponse(joinId);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse savedMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long joinId = memberService.join(member);
        return new CreateMemberResponse(joinId);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.name);
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data @NoArgsConstructor
    static class UpdateMemberRequest{
        @NotEmpty private String name;
    }
    @Data @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data @NoArgsConstructor
    static class CreateMemberRequest{
        @NotEmpty private String name;
    }

    @Data @AllArgsConstructor
    static class CreateMemberResponse{
        private Long id;
    }



}
