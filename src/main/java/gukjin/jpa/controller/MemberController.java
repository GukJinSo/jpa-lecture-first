package gukjin.jpa.controller;

import gukjin.jpa.domain.Address;
import gukjin.jpa.domain.Member;
import gukjin.jpa.dto.MemberForm;
import gukjin.jpa.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class MemberController {

    @Autowired private final MemberService memberService;

    @GetMapping("/members/new")
    private String createForm(Model model){
        System.out.println("멤버탐");
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    private String create(@Valid MemberForm memberForm, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getStreet());
        Member member = new Member();
        member.setAddress(address);
        member.setName(memberForm.getName());
        memberService.join(member);
        return "redirect:/";
    }


    @GetMapping("/members")
    private String list(Model model){
        model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }
}
