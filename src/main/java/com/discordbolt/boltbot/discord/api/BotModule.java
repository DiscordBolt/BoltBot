package com.discordbolt.boltbot.discord.api;

import discord4j.core.GatewayDiscordClient;

public interface BotModule {

    void initialize(GatewayDiscordClient client);
}