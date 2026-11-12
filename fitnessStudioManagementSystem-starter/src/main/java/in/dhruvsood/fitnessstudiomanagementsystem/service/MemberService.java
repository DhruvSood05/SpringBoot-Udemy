package in.dhruvsood.fitnessstudiomanagementsystem.service;

import in.dhruvsood.fitnessstudiomanagementsystem.entity.Member;
import in.dhruvsood.fitnessstudiomanagementsystem.entity.MembershipPlan;
import in.dhruvsood.fitnessstudiomanagementsystem.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void testMemberRepository() {
    }

    @Transactional
    public void testMemberRepositoryTransactional() {
        Member m1 = memberRepository.findById(1L).orElseThrow();
        Member m2 = memberRepository.findById(1L).orElseThrow();


        System.out.println(m1 + "  " + m2);
        System.out.println(m1 == m2);

        m1.setName("Dhruv");

    }

    @Transactional
    public MembershipPlan assignMembershipPlanToMember(MembershipPlan membershipPlan,
                                                       Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow();

        member.setMembershipPlan(membershipPlan);

        return membershipPlan;
    }

    @Transactional
    public void deleteMember(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow();

    memberRepository.delete(member);
    }



}
