package com.discordbolt.boltbot.discord.modules.admin;

import com.discordbolt.boltbot.discord.api.BotModule;
import com.discordbolt.boltbot.discord.api.CommandBean;
import com.discordbolt.boltbot.discord.util.BeanUtil;
import discord4j.core.GatewayDiscordClient;

public class AdminModule implements BotModule {

    @Override
    public void initialize(GatewayDiscordClient client) {
        BeanUtil.getBean(CommandBean.class).registerCommand(new UptimeCommand());
    }
}
