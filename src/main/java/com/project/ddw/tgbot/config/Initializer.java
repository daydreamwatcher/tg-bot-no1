package com.project.ddw.tgbot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.project.ddw.tgbot.service.CounterBot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Scope("singleton")
@Component
public class Initializer {
    @Autowired CounterBot bot;

    private BotSession session;

    @EventListener({ContextRefreshedEvent.class})
    public synchronized void init() {
        try {
            if (session != null) {
                session.stop();
            }
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            session = telegramBotsApi.registerBot((LongPollingBot) bot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @EventListener({ContextClosedEvent.class, ContextStoppedEvent.class})
    public synchronized void stop() {
        if (session != null) {
            session.stop();
        }
    }
}
