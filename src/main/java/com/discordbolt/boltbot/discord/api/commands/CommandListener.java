package com.discordbolt.boltbot.discord.api.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import java.util.Optional;
import reactor.core.publisher.Mono;

class CommandListener {

    private final CommandManager manager;

    CommandListener(CommandManager manager, GatewayDiscordClient client) {
        this.manager = manager;

        client.getEventDispatcher()
                .on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(author -> !author.isBot()).orElse(false))
                .filter(message -> message.getContent().isEmpty())
                .filterWhen(message -> message.getGuild().map(guild -> message.getContent().length() > manager.getCommandPrefix(guild).length()))
                .subscribe(this::onCommand);
    }

    private void onCommand(Message message) {
        Mono.just(message)
                .filterWhen(msg -> msg.getGuild().map(manager::getCommandPrefix).map(prefix -> msg.getContent().startsWith(prefix)))
                .flatMap(msg -> msg.getGuild()
                        .map(manager::getCommandPrefix)
                        .map(prefix -> msg.getContent().substring(prefix.length()))
                        .map(rawCommand -> manager.getCommands()
                                .stream()
                                .filter(command -> command.getCommands().size() <= rawCommand.split(" ").length)
                                .filter(command -> matches(command, rawCommand))
                                .reduce((first, second) -> second)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribe(c -> c.preexec(message));
    }

    private boolean matches(CustomCommand customCommand, String userCommand) {
        String userBaseCommand = userCommand.substring(0, userCommand.indexOf(" ") > 0 ? userCommand.indexOf(" ") : userCommand.length());

        for (int i = 0; i < customCommand.getCommands().size(); i++) {
            if (i == 0) {  // Checking the base command
                if (!(customCommand.getCommands().getFirst().equalsIgnoreCase(userBaseCommand) || (!customCommand.getAliases().isEmpty() && customCommand.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(userBaseCommand))))) {
                    return false;
                }
            } else {  // Check the sub commands
                if (!customCommand.getCommands().get(i).equalsIgnoreCase(userCommand.split(" ")[i])) {
                    return false;
                }
            }
        }
        return true;
    }
}
