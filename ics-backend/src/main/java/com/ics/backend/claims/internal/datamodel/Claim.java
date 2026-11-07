package com.ics.backend.claims.internal.datamodel;

import com.ics.backend.claims.internal.enums.ClaimStatus;
import com.ics.backend.claims.internal.enums.ClaimType;
// TODO no group import
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM")
public class Claim {

    @Id()
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "claim_sequence")
    @SequenceGenerator(name = "claim_sequence", sequenceName = "claim_sequence", allocationSize = 1)
    private Long id;

    @Column(name = "CLAIM_NUMBER")
    private String claimNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "CLAIM_STATUS")
    private ClaimStatus claimStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "CLAIM_TYPE")
    private ClaimType claimType;

    @Column(name = "CLAIM_DESCRIPTION")
    private String claimDescription;

    @Column(name = "CLAIM_DATE")
    private LocalDateTime claimDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    public ClaimStatus getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(ClaimStatus claimStatus) {
        this.claimStatus = claimStatus;
    }

    public ClaimType getClaimType() {
        return claimType;
    }

    public void setClaimType(ClaimType claimType) {
        this.claimType = claimType;
    }

    public String getClaimDescription() {
        return claimDescription;
    }

    public void setClaimDescription(String claimDescription) {
        this.claimDescription = claimDescription;
    }

    public LocalDateTime getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDateTime claimDate) {
        this.claimDate = claimDate;
    }
}
