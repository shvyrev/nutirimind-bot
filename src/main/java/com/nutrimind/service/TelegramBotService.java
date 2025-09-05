package com.nutrimind.service;

import com.nutrimind.model.User;
import com.nutrimind.service.handlers.ChatMessageHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TelegramBotService {
    private final TelegramBot bot;

    private final OnboardingService onboardingService;

    public Uni<Void> handleUpdate(Update update) {
        return ofNullable(update)
                .filter(not(v -> v.message() == null))
                .map(v -> User.byUpdate(v)
                        .map(u -> ChatMessageHandler.ChatMessage.of(v, u))
                        .chain(this::handleMessage))
                .orElseGet(() -> Uni.createFrom().voidItem());
    }

    private Uni<Void> handleMessage(ChatMessageHandler.ChatMessage message) {
        return switch (message.user().getState()) {
            case ACTIVE, PAUSED, CHURNED -> handleDefaultCommands(message);
            case ONBOARDING, AWAITING_ANSWER -> handleOnboardingMessage(message);
        };
    }

    private Uni<Void> handleDefaultCommands(ChatMessageHandler.ChatMessage message) {
        return switch (message.text()) {
            case "/start" -> startOnboarding(message.user());
            case "/help" -> sendHelpMessage(message.chatId());
            default -> sendMessage(message.chatId(), "Пожалуйста, используйте /start для начала работы.");
        };
    }

    private Uni<Void> handleOnboardingMessage(ChatMessageHandler.ChatMessage message) {
        return onboardingService.processAnswer(message.user(), message.text())
                .onFailure().recoverWithUni(failure -> {
                    return sendMessage(message.user().getTelegramId(), "Не могу понять ответ. Пожалуйста, попробуйте еще раз.")
                            .chain(() -> Uni.createFrom().failure(failure));
                })
                .chain(() -> Uni.createFrom().voidItem());
    }

    private Uni<Void> startOnboarding(User user) {
        return onboardingService.startOnboarding(user)
                .chain(updatedUser -> sendMessage(updatedUser.getTelegramId(), "Начинаем онбординг! Ответьте на несколько вопросов для создания вашего плана питания."));
    }

    public Uni<Void> sendMessage(Long chatId, String text) {
        log.info("$ sendMessage() called with: chatId = [{}], text = [{}]", chatId, text);

        return Uni.createFrom().voidItem()
                .invoke(() -> {
                    SendMessage request = new SendMessage(chatId, text);
                    bot.execute(request);
                });
    }

    private Uni<Void> sendHelpMessage(Long chatId) {
        log.info("$ sendHelpMessage() called with: chatId = [{}]", chatId);

        String helpText = """
                🤖 *NutriMind Bot Help*
                
                Доступные команды:
                /start - Начать онбординг
                /help - Показать справку
                
                Во время онбординга отвечайте на вопросы для создания персонализированного плана питания.
                """;
        return sendMessage(chatId, helpText);
    }
}