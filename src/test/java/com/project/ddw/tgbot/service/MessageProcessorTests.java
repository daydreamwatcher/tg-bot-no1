package com.project.ddw.tgbot.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.project.ddw.tgbot.components.BotCommands;

@ExtendWith(MockitoExtension.class)
public class MessageProcessorTests {

    private static final long USER_ID = 777;
    private static final String USER = "user777";
    private static final long CHAT_ID = 333;

    @Mock
    private AbsSender sender;

    private MessageProcessor messageProcessor;

    @BeforeEach
    void init() {
        messageProcessor = new MessageProcessor();
        messageProcessor.setSender(sender);
    }

    @Test
    void testInitCommands() throws TelegramApiException {
        SetMyCommands cmds = new SetMyCommands();

        messageProcessor.initCommands(cmds);

        verify(sender).execute(cmds);
    }

    @Test
    void testProcessMessage_start() throws TelegramApiException {
        Update update = new Update();
        Message msg = new Message();
        User from = new User();
        from.setId(USER_ID);
        from.setFirstName(USER);
        msg.setChat(new Chat(CHAT_ID, "private"));
        msg.setFrom(from);
        msg.setText(BotCommands.START_CMD);
        update.setMessage(msg);

        messageProcessor.processMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());

        assertThat(captor.getValue().getText(), is(equalTo(BotCommands.GREETING_MSG.format(new String[]{USER}))));
    }

    @Test
    void testProcessMessage_help() throws TelegramApiException {
        Update update = new Update();
        Message msg = new Message();
        User from = new User();
        from.setId(USER_ID);
        from.setFirstName(USER);
        msg.setChat(new Chat(CHAT_ID, "private"));
        msg.setFrom(from);
        msg.setText(BotCommands.HELP_CMD);
        update.setMessage(msg);

        messageProcessor.processMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(sender).execute(captor.capture());

        assertThat(captor.getValue().getText(), is(equalTo(BotCommands.HELP_TEXT)));
    }

    @Test
    void testProcessMessage_nonCommand() throws TelegramApiException {
        Update update = new Update();
        Message msg = new Message();
        User from = new User();
        from.setId(USER_ID);
        from.setFirstName(USER);
        msg.setChat(new Chat(CHAT_ID, "private"));
        msg.setFrom(from);
        msg.setText("some text");
        update.setMessage(msg);

        messageProcessor.processMessage(update);

        verifyNoInteractions(sender);
    }
}
