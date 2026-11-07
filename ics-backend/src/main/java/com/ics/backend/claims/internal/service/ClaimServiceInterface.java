package com.ics.backend.claims.internal.service;

import com.ics.backend.claims.internal.dto.ClaimDto;

public interface ClaimServiceInterface {

    ClaimDto getClaimById(Long id);

    Long createClaim(ClaimDto claim);

}
