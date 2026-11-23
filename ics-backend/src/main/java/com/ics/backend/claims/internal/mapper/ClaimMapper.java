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

        ClaimDto claimDto = new ClaimDto();
        claimDto.setId(claim.getId());
        claimDto.setClaimNumber(claim.getClaimNumber());
        claimDto.setClaimStatus(claim.getClaimStatus());
        claimDto.setClaimType(claim.getClaimType());
        claimDto.setClaimDescription(claim.getClaimDescription());
        claimDto.setClaimDate(claim.getClaimDate());
        return claimDto;
    }

    public static Claim toEntity(ClaimDto claimDto) {
        if (claimDto == null) {
            return null;
        }

        Claim claim = new Claim();
        //claim.setId(claimDto.getId());
        claim.setClaimNumber(claimDto.getClaimNumber());
        claim.setClaimStatus(claimDto.getClaimStatus());
        claim.setClaimType(claimDto.getClaimType());
        claim.setClaimDescription(claimDto.getClaimDescription());
        claim.setClaimDate(claimDto.getClaimDate());
        return claim;
    }
}