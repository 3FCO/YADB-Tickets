package me.efco.yadbtickets.commands.interfaces;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public abstract class AbstractCommand {
    private final String name;
    private final String description;
    private final List<Permission> permissions;
    private final List<OptionData> options;
    private final List<SubcommandData> subcommands;

    public AbstractCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.options = options;
        this.subcommands = subcommands;
    }

    public abstract void onExecution(SlashCommandInteractionEvent event);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public List<SubcommandData> getSubcommands() {
        return subcommands;
    }
}
