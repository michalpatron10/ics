package com.ics.backend.claims.internal.dto;

import com.ics.backend.claims.internal.enums.ClaimStatus;
import com.ics.backend.claims.internal.enums.ClaimType;
import java.time.LocalDateTime;

public record ClaimDto(
        Long id,
        String claimNumber,
        ClaimStatus claimStatus,
        ClaimType claimType,
        String claimDescription,
        LocalDateTime claimDate) {
}
