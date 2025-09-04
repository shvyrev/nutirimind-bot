package com.nutrimind.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_achievements")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserAchievement extends PanacheEntity {

    @ManyToOne
    public User user;

    @ManyToOne
    public Achievement achievement;

    @Column(name = "earned_at")
    public LocalDateTime earnedAt;

    // Reactive finder methods
    public static Uni<java.util.List<UserAchievement>> findByUserId(Long userId) {
        return find("user.id", userId).list();
    }

    public static Uni<java.util.List<UserAchievement>> findByAchievementId(Long achievementId) {
        return find("achievement.id", achievementId).list();
    }

    // Reactive save method
    public Uni<UserAchievement> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (UserAchievement) entity);
    }
}