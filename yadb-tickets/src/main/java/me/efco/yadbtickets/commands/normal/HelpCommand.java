package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.SlashCommandManager;
import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.List;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Commands for tickets").setColor(Color.RED);
        SlashCommandManager.getInstance().getCommands().forEach((s, abstractCommand) -> {
            embedBuilder.addField(abstractCommand.getName(), abstractCommand.getDescription(), false);
        });
        event.reply(new MessageBuilder().setEmbeds(embedBuilder.build()).build()).queue();
    }
}
