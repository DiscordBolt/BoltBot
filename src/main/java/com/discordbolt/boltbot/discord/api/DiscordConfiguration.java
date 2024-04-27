package com.discordbolt.boltbot.discord.api;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration("DiscordConfiguration")
@Profile("prod")
public class DiscordConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordConfiguration.class);

    private final String token;

    private GatewayDiscordClient client;

    public DiscordConfiguration(@Value("${discord.token}") String token) {
        LOGGER.info("Starting configuration of Discord Client");
       this.token = token;
    }

    @Bean
    public GatewayDiscordClient getClient() {
        return client;
    }

    protected void login() {
        LOGGER.info("Logging into Discord...");
        client = DiscordClient.create(token)
                .gateway()
                .setEnabledIntents(IntentSet.all())
                .withEventDispatcher(d -> d.on(ReadyEvent.class)
                        .doOnNext(readyEvent -> LOGGER.info("Ready: {}", readyEvent.getShardInfo())))
                .setInitialPresence(s -> ClientPresence.online(ClientActivity.playing("line 1").withState("line 2")))
                .login().block();
    }
}
