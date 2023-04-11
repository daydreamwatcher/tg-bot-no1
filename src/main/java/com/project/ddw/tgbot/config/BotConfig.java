package com.project.ddw.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@Data
@PropertySource("bot.properties")
public class BotConfig {
    @Value("${bot.name}") String botName;
    @Value("${bot.token}") String token;
    // @Value("${bot.chatId}") String chatId;
}