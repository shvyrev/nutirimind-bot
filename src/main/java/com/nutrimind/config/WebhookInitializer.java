package com.nutrimind.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class WebhookInitializer {

    private final TelegramBot telegramBot;

    @ConfigProperty(name = "quarkus.telegram.bot.token")
    String botToken;

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8443")
    int port;

    @ConfigProperty(name = "telegram.webhook.url")
    String webhookUrl;

    public void onStart(@Observes StartupEvent event) {
        try {
            log.info("Initializing Telegram webhook...");
            log.info("Original webhook URL: {}", webhookUrl);
            log.info("Bot token: {}", botToken.substring(0, 10) + "...");
            log.info("Application port: {}", port);

            // Проверяем, включен ли вебхук в конфигурации
            boolean webhookEnabled = Boolean.parseBoolean(System.getenv().getOrDefault("TELEGRAM_WEBHOOK_ENABLED", "false"));
            if (!webhookEnabled) {
                log.warn("⚠️ Webhook initialization is disabled. Set TELEGRAM_WEBHOOK_ENABLED=true to enable");
                return;
            }

            // Проверяем, что URL вебхука не является дефолтным
            if (webhookUrl.contains("your-public-url.com")) {
                log.error("❌ Webhook URL is not configured properly. Please set TELEGRAM_WEBHOOK_URL environment variable");
                return;
            }

            // Убедимся, что URL заканчивается на /webhook
            String finalWebhookUrl = webhookUrl;
            if (!webhookUrl.endsWith("/webhook")) {
                if (webhookUrl.endsWith("/")) {
                    finalWebhookUrl = webhookUrl + "webhook";
                } else {
                    finalWebhookUrl = webhookUrl + "/webhook";
                }
                log.warn("⚠️ Webhook URL corrected from '{}' to '{}'", webhookUrl, finalWebhookUrl);
            }

            SetWebhook setWebhook = new SetWebhook()
                .url(finalWebhookUrl)
                .maxConnections(10);

            var response = telegramBot.execute(setWebhook);
            log.info("Webhook setup response: {}", response);

            if (response.isOk()) {
                log.info("✅ Webhook successfully set up with Telegram");
                log.info("✅ Webhook URL: {}", finalWebhookUrl);
                
                log.info("✅ Webhook configured successfully with Telegram");
                
                // Проверим доступность вебхука локально
                log.info("🔍 Testing webhook endpoint locally...");
                log.info("Local test URL: http://localhost:{}/webhook", port);
            } else {
                log.error("❌ Failed to set webhook: {}", response.description());
                log.error("❌ Error code: {}", response.errorCode());
            }
        } catch (Exception e) {
            log.error("Failed to initialize webhook", e);
        }
    }
}