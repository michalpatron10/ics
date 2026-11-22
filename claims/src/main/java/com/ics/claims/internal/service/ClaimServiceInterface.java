package com.ics.claims.internal.service;

import com.ics.claims.internal.dto.ClaimDto;

public interface ClaimServiceInterface {

    ClaimDto getClaimById(Long id);

    Long createClaim(ClaimDto claim);

}
