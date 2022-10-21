package gukjin.jpa.api;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.Member;
import gukjin.jpa.service.MemberService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2(){
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());
        return new Result(collect.size(), collect);
    }

    @Data @AllArgsConstructor @Setter @Getter
    static class Result<T>{
        private int count;
        private T data;
    }

    @Data @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

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
