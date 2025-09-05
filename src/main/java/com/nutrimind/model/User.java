package com.nutrimind.model;

import java.time.LocalDateTime;
import java.util.List;

import com.nutrimind.model.enums.UserState;
import com.nutrimind.model.enums.CommunicationStyle;

import com.pengrad.telegrambot.model.Update;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User extends PanacheEntity {

    @Column(name = "telegram_id", unique = true, nullable = false)
    public Long telegramId;

    @Column(name = "username")
    public String username;

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    public UserState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_style")
    public CommunicationStyle communicationStyle;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "last_active_at")
    public LocalDateTime lastActiveAt;

    @OneToOne(mappedBy = "user")
    public UserProfile profile;

    @OneToMany(mappedBy = "user")
    public List<ChallengeProgress> challengeProgresses;

    @OneToMany(mappedBy = "user")
    public List<FoodEntry> foodEntries;

    @OneToOne(mappedBy = "user")
    public NutritionPlan nutritionPlan;

    // Reactive finder methods
    public static Uni<User> findByTelegramId(Long telegramId) {
        return find("telegramId", telegramId).firstResult();
    }

    public static Uni<User> findByUsername(String username) {
        return find("username", username).firstResult();
    }

    @WithTransaction
    public static Uni<User> byTelegramId(Long telegramId) {
        return findByTelegramId(telegramId)
                .onItem().ifNull().switchTo(() -> new User()
                        .setTelegramId(telegramId)
                        .setState(UserState.ONBOARDING)
                        .persist());
    }

    public static Uni<User> byTelegramUpdate(Update v) {
        Long chatId = v.message().chat().id();
        return byTelegramId(chatId);
    }

    // Reactive save method
    public Uni<User> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (User) entity);
    }
}