package in.dhruvsood.fitnessstudiomanagementsystem;

import in.dhruvsood.fitnessstudiomanagementsystem.dto.CMemberInfo;
import in.dhruvsood.fitnessstudiomanagementsystem.dto.IMemberInfo;
import in.dhruvsood.fitnessstudiomanagementsystem.dto.MembershipTierStats;
import in.dhruvsood.fitnessstudiomanagementsystem.entity.Member;
import in.dhruvsood.fitnessstudiomanagementsystem.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FitnessStudioManagementSystemApplicationTests {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void contextLoads() {
        List<Member> memberList = memberRepository.findAll();

        for (var member : memberList) {
            System.out.println(member);
        }
    }

    @Test
    void testMemberProjectionInterface() {
        List<IMemberInfo> memberList = memberRepository.getAllMembers();

        for (var member : memberList) {
            System.out.print(member.getId() + " ");
            System.out.print(member.getName() + " ");
            System.out.println(member.getEmail() + "     ");

        }
    }

    @Test
    void testMemberProjectionConcrete() {
        List<CMemberInfo> memberList = memberRepository.getAllMembersConcrete();

        for (var member : memberList) {
            System.out.print(member.getId() + " ");
            System.out.println(member.getName() + " ");
        }
    }


    @Test
    void testMemberAggregation() {
        List<MembershipTierStats> memberList = memberRepository.getMembershipTierStats();

        for (var member : memberList) {
            System.out.print(member.getMembershipTier() + " ");
            System.out.println(member.getCount() + " ");
        }
    }
}
