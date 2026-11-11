package in.dhruvsood.fitnessstudiomanagementsystem.dto;


import in.dhruvsood.fitnessstudiomanagementsystem.entity.type.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MembershipTierStats {

    private final MembershipTier membershipTier;

    private final Long count;

}
