package me.efco.yadbtickets.commands.normal;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.entities.Helper;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.managers.Manager;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;

import java.util.List;

public class ClaimCommand extends AbstractCommand {
    public ClaimCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(guild.getIdLong());
        Member member = event.getMember();

        if (!channel.getName().split("-")[0].equalsIgnoreCase("ticket")) {
            event.reply("Maybe try and claim an actual support ticket?").setEphemeral(true).queue();
            return;
        }
        if (!Helper.getInstance().hasTicketSupportPrivileges(member, guild, serverInfo)) {
            event.reply("You're not a support member").setEphemeral(true).queue();
            return;
        }

        channel.getManager()
                .putRolePermissionOverride(serverInfo.getSupportId(), java.util.List.of(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND,Permission.MANAGE_CHANNEL))
                .putMemberPermissionOverride(member.getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND), java.util.List.of())
                .queue();

        event.reply("This ticket has been claimed by " + member.getEffectiveName()).queue();

        DBConnection.getInstance().updateTicketSupporter(member.getIdLong());
    }
}
