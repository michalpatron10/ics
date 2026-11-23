package com.ics.backend.claims.internal.repository;

import com.ics.backend.claims.internal.datamodel.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
