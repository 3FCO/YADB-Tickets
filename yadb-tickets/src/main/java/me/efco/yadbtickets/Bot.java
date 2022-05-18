package me.efco.yadbtickets;

import me.efco.yadbtickets.data.ConfigLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Bot {
    private static JDA api;

    public static void main(String[] args) throws LoginException {
        api = JDABuilder.createDefault(ConfigLoader.getInstance().loadConfigByName("api_token"))
                .setActivity(Activity.playing("/help"))
                .build();

        //This is so we can make sweet sweet slash commands
        api.setRequiredScopes("identify", "bot", "webhook.incoming", "applications.commands");

        System.out.println("YADB Tickets has successfully started. Invite via " + api.getInviteUrl());
    }
}
