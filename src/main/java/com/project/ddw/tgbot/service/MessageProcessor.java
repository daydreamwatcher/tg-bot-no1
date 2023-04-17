package com.project.ddw.tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.project.ddw.tgbot.components.BotCommands;
import com.project.ddw.tgbot.components.Buttons;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.project.ddw.tgbot.components.BotCommands.HELP_TEXT;

@Slf4j
@Component
public class MessageProcessor {
    private AbsSender sender;

    public void setSender(AbsSender sender) {
        this.sender = sender;
    }

    public void initCommands(SetMyCommands commands) {
        try {
            sender.execute(commands);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public UserInfo processMessage(@NotNull Update update) {
        UserInfo user = new UserInfo();
        if(update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            user.id = update.getMessage().getFrom().getId();
            user.name = update.getMessage().getFrom().getFirstName();

            if (update.getMessage().hasText()) {
                String receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, user.name);
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            user.id = update.getCallbackQuery().getFrom().getId();
            user.name = update.getCallbackQuery().getFrom().getFirstName();
            String receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, user.name);
        }
        return user;
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage) {
            case BotCommands.START_CMD:
                startBot(chatId, userName);
                break;
            case BotCommands.HELP_CMD:
                sendHelpText(chatId, HELP_TEXT);
                break;
            default: break;
        }
    }

    private void startBot(long chatId, String user) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hello, " + user + "! I'm a Counter bot.");
        message.setReplyMarkup(Buttons.inlineMarkup());
    
        try {
            sender.execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void sendHelpText(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            sender.execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private long id;
        private String name;
    }
}
