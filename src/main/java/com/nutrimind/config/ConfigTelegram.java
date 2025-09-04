package com.nutrimind.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.telegram.bot")
public interface ConfigTelegram {
    String token();
    String name();
}
