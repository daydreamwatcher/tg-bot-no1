package com.project.ddw.tgbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.project.ddw.tgbot.config.BotConfig;
import com.project.ddw.tgbot.database.User;
import com.project.ddw.tgbot.database.UserRepository;
import com.project.ddw.tgbot.service.MessageProcessor.UserInfo;

@ExtendWith(MockitoExtension.class)
class CounterBotTests {

	private static final String BOT_NAME = "TestName";
	private static final String BOT_TOKEN = "token";
	private static final long USER_ID = 777;
	private static final String USER_NAME = "User777";

	@Mock
	private UserRepository userRepository;

	@Mock
	private MessageProcessor messageProcessor;

	private CounterBot counterBot;

	@BeforeEach
	void initCounterBot() {
		BotConfig botConfig = new BotConfig();
		botConfig.setBotName(BOT_NAME);
		botConfig.setToken(BOT_TOKEN);
		counterBot = new CounterBot(botConfig);
		counterBot.setUserRepository(userRepository);
		counterBot.setMessageProcessor(messageProcessor);
	}

	@Test
	void testInit() {
		counterBot.init();

		verify(messageProcessor).setSender(counterBot);

		assertEquals(counterBot.getBotUsername(), BOT_NAME);
		assertEquals(counterBot.getBotToken(), BOT_TOKEN);
	}

	@Test
	void testOnUpdateReceived_noExistingInDB() {
		Update update = new Update();
		UserInfo info = new UserInfo(USER_ID, USER_NAME);


		when(messageProcessor.processMessage(update)).thenReturn(info);
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		counterBot.onUpdateReceived(update);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());

		assertEquals(captor.getValue().getMsg_numb(), 1);
		assertEquals(captor.getValue().getId(), USER_ID);
		assertEquals(captor.getValue().getName(), USER_NAME);
	}

	@Test
	void testOnUpdateReceived_existingInDB() {
		Update update = new Update();
		UserInfo info = new UserInfo(USER_ID, USER_NAME);

		User user = new User();
		user.setId(USER_ID);

		when(messageProcessor.processMessage(update)).thenReturn(info);
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		counterBot.onUpdateReceived(update);

		verify(userRepository).updateMsgNumberByUserId(USER_ID);
	}

	@Test
	void testOnUpdateReceived_nullUserInfo() {
		Update update = new Update();

		when(messageProcessor.processMessage(update)).thenReturn(null);

		assertThrows(IllegalArgumentException.class, () -> counterBot.onUpdateReceived(update));
	}
}
