package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.entities.Helper;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.util.List;

public class TransferCommand extends AbstractCommand {
    public TransferCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(guild.getIdLong());
        Member member = event.getMember();
        int ticketId = Integer.parseInt(event.getTextChannel().getName().split("-")[1]);
        long currentSupporter = DBConnection.getInstance().getTicketSupporter(ticketId);
        Member newSupport = event.getOption("newsupport").getAsMember();

        if (!member.isOwner() && !member.getRoles().contains(guild.getRoleById(serverInfo.getModeratorId())) && member.getIdLong() != currentSupporter) {
            event.reply("You dont have permissions in this ticket").setEphemeral(true).queue();
            return;
        }
        if (!Helper.getInstance().hasTicketSupportPrivileges(member, guild, serverInfo)) {
            event.reply("The chosen member doesn't have permission to administrate support ticket").setEphemeral(true).queue();
            return;
        }

        channel.getManager()
                .putMemberPermissionOverride(currentSupporter, List.of(), List.of(Permission.MESSAGE_SEND,Permission.VIEW_CHANNEL))
                .putMemberPermissionOverride(newSupport.getIdLong(), List.of(Permission.MESSAGE_SEND,Permission.VIEW_CHANNEL), List.of())
                .queue();

        event.reply(new MessageBuilder().setEmbeds(new EmbedBuilder()
                    .setColor(Color.BLUE)
                    .setTitle("This ticket has been transferred to support member " + newSupport.getEffectiveName()).build()).build())
                .queue();

        DBConnection.getInstance().updateTicketSupporter(newSupport.getIdLong());
    }
}
