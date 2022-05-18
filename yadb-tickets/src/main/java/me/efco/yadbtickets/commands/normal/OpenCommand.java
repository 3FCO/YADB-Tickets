package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.security.Permission;
import java.util.List;

public class OpenCommand extends AbstractCommand {
    protected OpenCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {

    }
}
