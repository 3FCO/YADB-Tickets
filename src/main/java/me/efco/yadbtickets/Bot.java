package me.efco.yadbtickets;

import me.efco.yadbtickets.commands.SlashCommandManager;
import me.efco.yadbtickets.data.ConfigLoader;
import me.efco.yadbtickets.events.BotReadyEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.List;

public class Bot {
    private static JDA api;

    public static void main(String[] args) throws LoginException {
        api = JDABuilder.createDefault(ConfigLoader.getInstance().loadConfigByName("api_token"))
                .setActivity(Activity.playing("Under construction"))
                .addEventListeners(new BotReadyEvent())
                .addEventListeners(SlashCommandManager.getInstance())
                .build();

        //This is so we can make sweet sweet slash commands
        api.setRequiredScopes("identify", "bot", "webhook.incoming", "applications.commands");

        System.out.println("YADB Tickets has successfully started. Invite via " + api.getInviteUrl(List.of(
                Permission.ADMINISTRATOR
        )));
    }

    public static JDA getApi() {
        return api;
    }
}
