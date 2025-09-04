package com.nutrimind.model;

import java.time.LocalDateTime;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import io.smallrye.mutiny.Uni;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(name = "telegram_user_id", unique = true, nullable = false)
    public String telegramUserId;

    @Column(name = "username")
    public String username;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "email")
    public String email;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    // Reactive finder methods
    public static Uni<User> findByTelegramUserId(String telegramUserId) {
        return find("telegramUserId", telegramUserId).firstResult();
    }

    public static Uni<User> findByEmail(String email) {
        return find("email", email).firstResult();
    }

    // Reactive save method
    public Uni<User> persistAndFlush() {
        return persistAndFlush().onItem().transform(entity -> (User) entity);
    }
}