package com.discordbolt.boltbot.discord.api;

import com.discordbolt.boltbot.discord.api.commands.CommandManager;
import com.discordbolt.boltbot.discord.api.commands.CustomCommand;
import discord4j.core.GatewayDiscordClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("CommandBean")
@Profile("prod")
public class CommandBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBean.class);

    private final GatewayDiscordClient client;
    private CommandManager commandManager;

    @Autowired
    public CommandBean(GatewayDiscordClient client) {
        this.client = client;
        initCommands();
    }

    private void initCommands() {
        // Initialize command manager (scans and registers commands with @BotCommand)
        commandManager = new CommandManager(client, BoltService.PACKAGE_PREFIX);
        // Restore saved per-guild command prefixes
        /*BeanUtil.getBean(GuildRepository.class)
                .findAll()
                .filter(data -> data.getCommandPrefix() != null)
                .subscribe(data -> commandManager.setCommandPrefix(data.getId(), data.getCommandPrefix()));
        */
        commandManager.onCommandExecution(context -> {
            // TODO store stats about each command execution.
            // Neo4J https://projects.spring.io/spring-data-neo4j/
            //
            // https://medium.com/@joeclever/using-multiple-datasources-with-spring-boot-and-spring-data-6430b00c02e7
            // http://www.baeldung.com/spring-data-jpa-multiple-databases
        });
    }

    public void registerCommand(CustomCommand command) {
        LOGGER.info("Registering '{}'", command.toString());
        commandManager.registerCommand(command);
    }

    public void unregisterCommand(CustomCommand command) {
        LOGGER.info("Unregistering '{}'", command);
        commandManager.unregisterCommand(command);
    }

    public void setCommandPrefix(long guildId, String newPrefix) {
        commandManager.setCommandPrefix(guildId, newPrefix);
    }
}
