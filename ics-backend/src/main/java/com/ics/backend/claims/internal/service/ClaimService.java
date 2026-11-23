package com.ics.backend.claims.internal.service;

import com.ics.backend.claims.internal.datamodel.Claim;
import com.ics.backend.claims.internal.dto.ClaimDto;
import com.ics.backend.claims.internal.mapper.ClaimMapper;
import com.ics.backend.claims.internal.repository.ClaimRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public ClaimDto getClaimById(Long id) {
        Claim claim = claimRepository.findById(id).orElseThrow(() -> new ClaimNotFoundException(id));
        return ClaimMapper.toDto(claim);
    }

    public Long createClaim(ClaimDto claimDto) {
        Claim savedClaim = claimRepository.save(ClaimMapper.toEntity(claimDto));
        return savedClaim.getId();
    }
}
