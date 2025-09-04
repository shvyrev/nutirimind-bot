package com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "challenges")
public class Challenge extends PanacheEntity {

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "challenge_type")
    public String challengeType;

    @Column(name = "target_value")
    public Integer targetValue;

    @Column(name = "reward_points")
    public Integer rewardPoints;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    // Reactive finder methods
    public static Uni<List<Challenge>> findByType(String challengeType) {
        return find("challengeType", challengeType).list();
    }

    public static Uni<Challenge> findByTitle(String title) {
        return find("title", title).firstResult();
    }

    // Reactive save method
    public Uni<Challenge> persistAndFlush() {
        return super.persistAndFlush();
    }
}