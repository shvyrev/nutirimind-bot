package com.nutrimind.service;

import com.nutrimind.model.OnboardingStep;
import com.nutrimind.model.User;
import com.nutrimind.model.enums.UserState;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.Logger;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class OnboardingService {
    private static final List<OnboardingStep> ONBOARDING_STEPS = List.of(
        new OnboardingStep(1, "age_height_weight", "Расскажи, пожалуйста, твой возраст, рост и вес? (формат: возраст рост вес, например: 30 175 70)"),
        new OnboardingStep(2, "health_goal", "Какая у тебя цель: похудеть, набрать мышечную массу или просто питаться сбалансированно?"),
        new OnboardingStep(3, "activity_level", "Сколько раз в неделю у тебя получается заниматься спортом или ходить пешком?"),
        new OnboardingStep(4, "communication_style", "Выбери стиль общения: хочу строгого наставника или хочу лёгкого приятеля?")
    );

    private final TelegramBotService telegramBotService;

    private final StateManager stateManager;

    public Uni<User> startOnboarding(User user) {
        return stateManager.transitionToState(user, UserState.ONBOARDING)
            .chain(updatedUser -> askQuestion(updatedUser, ONBOARDING_STEPS.get(0)));
    }

    public Uni<User> processAnswer(User user, String answer) {
        // Базовая реализация - переходим к следующему вопросу
        return askNextQuestion(user);
    }

    private Uni<User> askNextQuestion(User user) {
        // Временно - задаем следующий вопрос по порядку
        int nextStepIndex = 1;
        if (nextStepIndex < ONBOARDING_STEPS.size()) {
            return askQuestion(user, ONBOARDING_STEPS.get(nextStepIndex));
        } else {
            return completeOnboarding(user);
        }
    }

    private Uni<User> askQuestion(User user, OnboardingStep step) {
        return telegramBotService.sendMessage(user.telegramId, step.question())
            .chain(() -> stateManager.transitionToState(user, UserState.AWAITING_ANSWER));
    }

    private Uni<User> completeOnboarding(User user) {
        return stateManager.transitionToState(user, UserState.ACTIVE)
            .chain(updatedUser -> {
                String completionMessage = """
                    🎉 Отлично! Онбординг завершен!
                    
                    Теперь я готов помочь тебе с питанием.
                    Используй команды для управления своим планом питания.
                    """;
                return telegramBotService.sendMessage(updatedUser.telegramId, completionMessage)
                    .chain(() -> Uni.createFrom().item(updatedUser));
            });
    }

    public boolean isOnboardingComplete(User user) {
        return user.state == UserState.ACTIVE;
    }
}