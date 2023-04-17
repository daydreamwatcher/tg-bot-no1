package com.project.ddw.tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import com.project.ddw.tgbot.config.BotConfig;
import com.project.ddw.tgbot.database.User;
import com.project.ddw.tgbot.database.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import static com.project.ddw.tgbot.components.BotCommands.LIST_OF_COMMANDS;

@Slf4j
@Component
public class CounterBot extends TelegramLongPollingBot {
    final BotConfig config;

    private UserRepository userRepository;

    private MessageProcessor messageProcessor;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @PostConstruct
    public void init() {
        messageProcessor.setSender(this);
        messageProcessor.initCommands(
            new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
    }

    public CounterBot(BotConfig config) {
        super(config.getToken());
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        updateDB(messageProcessor.processMessage(update));
    }

    private void updateDB(MessageProcessor.UserInfo userInfo) {
        if (userInfo == null) {
            throw new IllegalArgumentException("userInfor cannot be null");
        }
        if(userRepository.findById(userInfo.getId()).isEmpty()) {
            User user = new User();
            user.setId(userInfo.getId());
            user.setName(userInfo.getName());
            // initially set number of messages to one
            user.setMsg_numb(1);

            userRepository.save(user);
            log.info("Added to DB: " + user);
        } else {
            userRepository.updateMsgNumberByUserId(userInfo.getId());
        }
    }
}
