package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.entities.Helper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class OpenCommand extends AbstractCommand {
    public OpenCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        String description = event.getOption("description").getAsString();
        if (description.length() < 10) {
            event.reply("Your description must be more than 10 characters").setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue();

        String isInfoValid = Helper.getInstance().isServerInfoValid(event.getGuild());
        if (!isInfoValid.equals("")) {
            event.getHook().sendMessage(isInfoValid).queue();

            try {
                event.getGuild().retrieveOwner().submit().get().getUser().openPrivateChannel().submit().get().sendMessage("A user tried to use the /open command, but some channels/roles information isn't correct. Please use /setup command in server \" + event.getGuild().getName()");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return;
        }

        event.getHook().sendMessage("Doing big boy stoff").queue();
    }
}
