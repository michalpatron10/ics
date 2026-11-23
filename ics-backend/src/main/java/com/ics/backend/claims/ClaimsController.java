package com.ics.backend.claims;

import com.ics.backend.claims.internal.dto.ClaimDto;
import com.ics.backend.claims.internal.service.ClaimService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/claims")
public class ClaimsController {

    private final ClaimService claimService;

    public ClaimsController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @GetMapping("/{id}")
    public ClaimDto getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id);
    }

    @PostMapping
    public Long createClaim(@RequestBody ClaimDto claim) {
        return claimService.createClaim(claim);
    }
}
