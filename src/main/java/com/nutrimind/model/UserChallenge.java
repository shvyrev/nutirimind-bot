package com.nutrimind.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_challenges")
public class UserChallenge extends PanacheEntity {

    @ManyToOne
    public Challenge challenge;

    @ManyToOne
    public User user;

    @Column(name = "start_date")
    public LocalDateTime startDate;

    @Column(name = "end_date")
    public LocalDateTime endDate;

    @Column(name = "status")
    public String status;

    // Reactive finder methods
    public static Uni<java.util.List<UserChallenge>> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static Uni<java.util.List<UserChallenge>> findByChallengeId(Long challengeId) {
        return find("challenge.id", challengeId).list();
    }

    public static Uni<java.util.List<UserChallenge>> findByStatus(String status) {
        return find("status", status).list();
    }

    // Reactive save method
    public Uni<UserChallenge> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (UserChallenge) entity);
    }
}