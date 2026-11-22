package com.ics.claims.internal.repoository;

import com.ics.claims.internal.datamodel.Claim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
}
