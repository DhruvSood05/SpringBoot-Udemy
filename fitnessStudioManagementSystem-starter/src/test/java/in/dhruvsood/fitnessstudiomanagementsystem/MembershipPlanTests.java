package in.dhruvsood.fitnessstudiomanagementsystem;


import in.dhruvsood.fitnessstudiomanagementsystem.entity.MembershipPlan;
import in.dhruvsood.fitnessstudiomanagementsystem.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
public class MembershipPlanTests {

    @Autowired
    private MemberService memberService;

    @Test
    void testAssignMembershipPlanToMember() {
        MembershipPlan membershipPlan = MembershipPlan.builder()
                .provider("Cult Fitness")
                .planNumber("FLC_26")
                .validUntil(LocalDate.of(2030, 12, 11))
                .build();

        MembershipPlan updatemembershipPlan = memberService.assignMembershipPlanToMember(
                membershipPlan,
                1L
        );

        System.out.println(updatemembershipPlan);

        memberService.deleteMember(1L);

    }
}
