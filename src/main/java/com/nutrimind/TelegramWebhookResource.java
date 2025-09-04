package com.nutrimind;

import com.nutrimind.service.TelegramBotService;
import com.pengrad.telegrambot.utility.BotUtils;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Slf4j
@Path("/webhook")
@RequiredArgsConstructor
public class TelegramWebhookResource {

    private final TelegramBotService botService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Void> handleUpdate(String rawJson) {
        return ofNullable(rawJson)
                .filter(not(s -> s.trim().isEmpty()))
                .map(BotUtils::parseUpdate)
                .map(botService::handleUpdate)
                .orElseGet(() -> Uni.createFrom().voidItem());
    }
}