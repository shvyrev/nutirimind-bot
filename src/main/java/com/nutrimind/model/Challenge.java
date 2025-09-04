package com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.List;

import com.nutrimind.model.enums.ChallengeType;
import com.nutrimind.model.enums.DifficultyLevel;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Challenge extends PanacheEntity {

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "description")
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    public ChallengeType type;

    @Column(name = "duration_days")
    public Integer durationDays;

    @Column(name = "points_reward")
    public Integer pointsReward;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    public DifficultyLevel difficulty;

    @Column(name = "completion_criteria")
    public String completionCriteria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "challenge_tips", joinColumns = @JoinColumn(name = "challenge_id"))
    @Column(name = "tip")
    public List<String> tips;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    // Reactive finder methods
    public static Uni<List<Challenge>> findByType(ChallengeType type) {
        return find("type", type).list();
    }

    public static Uni<Challenge> findByTitle(String title) {
        return find("title", title).firstResult();
    }

    public static Uni<List<Challenge>> findByDifficulty(DifficultyLevel difficulty) {
        return find("difficulty", difficulty).list();
    }

    // Reactive save method
    public Uni<Challenge> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (Challenge) entity);
    }
}