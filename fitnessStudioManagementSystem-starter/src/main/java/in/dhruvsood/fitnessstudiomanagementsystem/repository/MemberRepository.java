package in.dhruvsood.fitnessstudiomanagementsystem.repository;

import in.dhruvsood.fitnessstudiomanagementsystem.dto.CMemberInfo;
import in.dhruvsood.fitnessstudiomanagementsystem.dto.IMemberInfo;
import in.dhruvsood.fitnessstudiomanagementsystem.dto.MembershipTierStats;
import in.dhruvsood.fitnessstudiomanagementsystem.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("""
                    SELECT m.id as id, m.name as name, m.email as email
                    FROM Member m
            """)
    List<IMemberInfo> getAllMembers();


    @Query("""
                SELECT 
                    new in.dhruvsood.fitnessstudiomanagementsystem.dto.CMemberInfo(
                    m.id,
                    m.name
                                )
            FROM Member m
            
            """)
    List<CMemberInfo> getAllMembersConcrete();


    @Query("""
                SELECT new in.dhruvsood.fitnessstudiomanagementsystem.dto.MembershipTierStats(
                    m.membershipTier,
                                COUNT(m)
                            )
                FROM Member m
                WHERE m.membershipTier IS NOT NULL 
                GROUP BY m.membershipTier
                ORDER BY COUNT(m) desc 
            
            """)
    List<MembershipTierStats> getMembershipTierStats();
}
