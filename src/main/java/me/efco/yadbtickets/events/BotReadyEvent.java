package me.efco.yadbtickets.events;

import me.efco.yadbtickets.Bot;
import me.efco.yadbtickets.commands.SlashCommandManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotReadyEvent extends ListenerAdapter {
    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Ready");
        for (Guild guild : Bot.getApi().getGuilds()) {
            SlashCommandManager.getInstance().updateAllCommands(guild);
        }
    }
}
