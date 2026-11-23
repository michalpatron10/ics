package com.ics.backend.claims.internal.mapper;

import com.ics.backend.claims.internal.datamodel.Claim;
import com.ics.backend.claims.internal.dto.ClaimDto;

public final class ClaimMapper {

    private ClaimMapper() {
    }

    public static ClaimDto toDto(Claim claim) {
        if (claim == null) {
            return null;
        }
        return new ClaimDto(
                claim.getId(),
                claim.getClaimNumber(),
                claim.getClaimStatus(),
                claim.getClaimType(),
                claim.getClaimDescription(),
                claim.getClaimDate());
    }

    public static Claim toEntity(ClaimDto claimDto) {
        if (claimDto == null) {
            return null;
        }

        // The ID is assigned on insert, so an ID sent by the client is ignored.
        Claim claim = new Claim();
        claim.setClaimNumber(claimDto.claimNumber());
        claim.setClaimStatus(claimDto.claimStatus());
        claim.setClaimType(claimDto.claimType());
        claim.setClaimDescription(claimDto.claimDescription());
        claim.setClaimDate(claimDto.claimDate());
        return claim;
    }
}
