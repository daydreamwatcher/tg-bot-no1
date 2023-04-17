package com.project.ddw.tgbot.components;

import java.util.List;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

public interface BotCommands {
    String START_CMD = "/start";
    String HELP_CMD = "/help";

    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand(START_CMD, "start bot"),
            new BotCommand(HELP_CMD, "bot info")
    );

    String HELP_TEXT = "The bot counts messages number in the chat. " +
            "The following commands are available:\n\n" +
            "/start - start the bot\n" +
            "/help - help menu";
}
