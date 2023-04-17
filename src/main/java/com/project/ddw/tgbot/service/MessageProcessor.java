package com.project.ddw.tgbot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
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
            Message msg = update.getMessage();
            long chatId = msg.getChatId();
            user.id = msg.getFrom().getId();
            user.name = msg.getFrom().getFirstName();

            if (msg.hasText()) {
                String receivedMessage = msg.getText();
                botAnswerUtils(receivedMessage, chatId, user.name);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery cQuery = update.getCallbackQuery();
            long chatId = cQuery.getMessage().getChatId();
            user.id = cQuery.getFrom().getId();
            user.name = cQuery.getFrom().getFirstName();
            String receivedMessage = cQuery.getData();

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
        message.setText(BotCommands.GREETING_MSG.format(new String[]{user}));
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
