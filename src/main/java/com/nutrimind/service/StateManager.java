package com.nutrimind.service;

import com.nutrimind.model.User;
import com.nutrimind.model.enums.UserState;
import com.nutrimind.repository.UserRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.Logger;
@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StateManager {

    private final UserRepository userRepository;

    public Uni<User> transitionToState(User user, UserState newState) {
        user.state = newState;
        user.lastActiveAt = java.time.LocalDateTime.now();
        return userRepository.persist(user);
    }

    public Uni<User> handleOnboardingStep(User user, String input) {
        // Базовая реализация - просто переводим в активное состояние
        return transitionToState(user, UserState.ACTIVE)
            .chain(this::completeOnboarding);
    }

    private Uni<User> completeOnboarding(User user) {
        // Завершение онбординга
        return Uni.createFrom().item(user);
    }
}