package com.nutrimind.model;

import java.time.LocalDate;

import com.nutrimind.model.enums.ChallengeStatus;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "challenge_progress")
public class ChallengeProgress extends PanacheEntity {

    @ManyToOne
    public User user;

    @ManyToOne
    public Challenge challenge;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(name = "end_date")
    public LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public ChallengeStatus status;

    @Column(name = "current_progress")
    public Integer currentProgress;

    @Column(name = "target_progress")
    public Integer targetProgress;

    // Reactive finder methods
    public static Uni<java.util.List<ChallengeProgress>> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static Uni<java.util.List<ChallengeProgress>> findByChallengeId(Long challengeId) {
        return find("challenge.id", challengeId).list();
    }

    public static Uni<java.util.List<ChallengeProgress>> findByStatus(ChallengeStatus status) {
        return find("status", status).list();
    }

    // Reactive save method
    public Uni<ChallengeProgress> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (ChallengeProgress) entity);
    }
}