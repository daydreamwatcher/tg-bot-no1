package com.project.ddw.tgbot.components;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info")
    );

    String HELP_TEXT = "The bot counts messages number in the chat. " +
            "The following commands are available:\n\n" +
            "/start - start the bot\n" +
            "/help - help menu";
}
