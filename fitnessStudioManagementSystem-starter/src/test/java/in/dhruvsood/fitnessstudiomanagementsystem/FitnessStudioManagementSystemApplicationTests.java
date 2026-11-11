package in.dhruvsood.fitnessstudiomanagementsystem;

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

		for(var member: memberList) {
			System.out.println(member);
    }

}
