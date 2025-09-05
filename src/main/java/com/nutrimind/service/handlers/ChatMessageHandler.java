package com.nutrimind.service.handlers;

import com.nutrimind.model.User;
import com.pengrad.telegrambot.model.Update;

public interface ChatMessageHandler  {

    record ChatMessage(Long chatId, String text, User user) {
        public static ChatMessage of(Update update, User user) {
            return new ChatMessage(update.message().chat().id(), update.message().text(), user);
        }
    }
}
