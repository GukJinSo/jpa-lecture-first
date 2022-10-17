package gukjin.jpa.service;

import gukjin.jpa.domain.Member;
import gukjin.jpa.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @Rollback(value = false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("국진");

        //when
        Long saveId = memberService.join(member);
        //em.flush();

        //then
        Assertions.assertThat(saveId).isEqualTo(memberRepository.findOne(saveId).getId());


    }
    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member = new Member();
        member.setName("국진");
        Member member2 = new Member();
        member2.setName("국진");

        memberService.join(member);
        memberService.join(member2);
        fail("익셉션이 발생해야 한다");
    }


}