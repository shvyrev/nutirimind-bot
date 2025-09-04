package com.nutrimind.repository;

import com.nutrimind.model.User;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Uni<User> findByTelegramId(Long telegramId) {
        return find("telegramId", telegramId).firstResult();
    }

    public Uni<User> persistUser(User user) {
        return persistAndFlush(user);
    }
}