package com.nutrimind.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_goals")
public class UserGoal extends PanacheEntity {

    @ManyToOne
    public User user;

    @Column(name = "goal_type", nullable = false)
    public String goalType;

    @Column(name = "target_value")
    public Double targetValue;

    @Column(name = "current_value")
    public Double currentValue;

    @Column(name = "target_date")
    public LocalDate targetDate;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    // Reactive finder methods
    public static Uni<java.util.List<UserGoal>> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static Uni<java.util.List<UserGoal>> findByGoalType(String goalType) {
        return find("goalType", goalType).list();
    }

    // Reactive save method
    public Uni<UserGoal> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (UserGoal) entity);
    }
}