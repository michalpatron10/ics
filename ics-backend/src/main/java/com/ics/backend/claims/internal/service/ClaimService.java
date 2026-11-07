package com.ics.backend.claims.internal.service;

import com.ics.backend.claims.internal.datamodel.Claim;
import com.ics.backend.claims.internal.dto.ClaimDto;
import com.ics.backend.claims.internal.mapper.ClaimMapper;
import com.ics.backend.claims.internal.repoository.ClaimRepository;
import org.springframework.stereotype.Service;


@Service
public class ClaimService implements ClaimServiceInterface {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public ClaimDto getClaimById(Long id) {
        return ClaimMapper.toDto(claimRepository.findById(id).get());
    }

    @Override
    public Long createClaim(ClaimDto claimDto) {
        Claim savedClaim = claimRepository.save(ClaimMapper.toEntity(claimDto));
        return savedClaim.getId();
    }
}
