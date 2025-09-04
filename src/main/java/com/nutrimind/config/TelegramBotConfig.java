package com.nutrimind.config;

import com.pengrad.telegrambot.TelegramBot;
import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TelegramBotConfig {

    @ConfigProperty(name = "quarkus.telegram.bot.token")
    String botToken;

    @Produces
    @DefaultBean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}