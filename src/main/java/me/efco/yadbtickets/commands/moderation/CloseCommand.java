package me.efco.yadbtickets.commands.moderation;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.data.TicketLogger;
import me.efco.yadbtickets.entities.Helper;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CloseCommand extends AbstractCommand {
    public CloseCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(guild.getIdLong());
        Member member = event.getMember();

        if (!channel.getName().split("-")[0].equalsIgnoreCase("ticket")) {
            event.reply("Maybe try and close an actual support ticket?").setEphemeral(true).queue();
            return;
        }

        if (!Helper.getInstance().hasTicketSupportPrivileges(member, guild, serverInfo)) {
            event.reply("You don't have permission to execute this command").setEphemeral(true).queue();
            return;
        }

        channel.delete().queueAfter(2, TimeUnit.MINUTES);
        event.reply("``This ticket has been marked as solved. Closing ``<t:" + Instant.now().plus(2, ChronoUnit.MINUTES).getEpochSecond() + ":R>").queue();

        StringBuilder stringBuilder = new StringBuilder();
        List<Message> messages = new ArrayList<>();
        try {
            messages = MessageHistory.getHistoryFromBeginning(channel).submit().get().getRetrievedHistory();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        for (Message m : messages) {
            stringBuilder.append("[" + m.getTimeCreated() + "] " + m.getMember().getEffectiveName() + ": " + m.getContentDisplay() + "\n");
        }

        TicketLogger.getInstance().saveLogLocal(event.getTextChannel().getName(), stringBuilder.toString());

        DBConnection.getInstance().setTicketSolved(Integer.parseInt(channel.getName().split("-")[1]), false);
    }
}
