package in.dhruvsood.fitnessstudiomanagementsystem.repository;

import in.dhruvsood.fitnessstudiomanagementsystem.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
