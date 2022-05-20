package me.efco.yadbtickets.commands;

import me.efco.yadbtickets.Bot;
import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.commands.moderation.SetupCommand;
import me.efco.yadbtickets.commands.normal.HelpCommand;
import me.efco.yadbtickets.commands.normal.OpenCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlashCommandManager extends ListenerAdapter {
    private static final SlashCommandManager INSTANCE = new SlashCommandManager();
    private final Map<String, AbstractCommand> commands;

    private SlashCommandManager() {
        commands = new HashMap<>();

        commands.put("help", new HelpCommand(
                "help", "Get a list of all available commands",
                new ArrayList<>(),
                List.of(
                        new OptionData(OptionType.STRING, "command", "Get help for a specific commands", false)
                ),
                new ArrayList<>()
        ));
        commands.put("open", new OpenCommand(
                "open", "Open a new ticket",
                new ArrayList<>(),
                List.of(
                        new OptionData(OptionType.STRING, "description", "Ticket description", true)
                ),
                new ArrayList<>()
        ));
        commands.put("setup", new SetupCommand(
                "setup", "Setup important information for the ticket bot to use",
                List.of(Permission.ADMINISTRATOR),
                List.of(
                        new OptionData(OptionType.ROLE, "support", "Select a role to become the ticket support role", false),
                        new OptionData(OptionType.ROLE, "moderator", "Select a role to become the ticket moderator role", false),
                        new OptionData(OptionType.CHANNEL, "channel", "Select a channel to become the ticket master channel", false),
                        new OptionData(OptionType.INTEGER, "category", "Select a category ID to become the ticket support category", false)
                ),
                new ArrayList<>()
        ));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!commands.containsKey(event.getName())) return;

        Bot.getApi().getCallbackPool().submit(() -> commands.get(event.getName()).onExecution(event));
    }

    public void updateAllCommands(Guild guild) {
        commands.forEach((key, value) -> {
            guild.upsertCommand(value.getName(), value.getDescription())
                    .addOptions(value.getOptions())
                    .addSubcommands(value.getSubcommands())
                    .queue();
        });
    }

    public static SlashCommandManager getInstance() {
        return INSTANCE;
    }

    public Map<String, AbstractCommand> getCommands() {
        return commands;
    }
}