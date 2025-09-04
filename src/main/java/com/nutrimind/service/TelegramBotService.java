package com.nutrimind.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
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

    record ChatMessage(Long chatId, String text) {
        public static ChatMessage of(Update update) {
            return new ChatMessage(update.message().chat().id(), update.message().text());
        }
    }

    public Uni<Void> handleUpdate(Update update) {
        return ofNullable(update)
                .filter(not(v -> v.message() == null))
                .map(ChatMessage::of)
                .map(this::handleMessageNew)
                .orElseGet(() -> Uni.createFrom().voidItem());
    }

    private Uni<Void> handleMessageNew(ChatMessage chatMessage) {
        return switch (chatMessage.text()) {
            case "/start" -> sendMessage(chatMessage.chatId(), "Добро пожаловать в NutriMind! Начнем онбординг.");
            case "/help" -> sendHelpMessage(chatMessage.chatId());
            default -> sendMessage(chatMessage.chatId(), "Пожалуйста, используйте /start для начала работы.");
        };
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