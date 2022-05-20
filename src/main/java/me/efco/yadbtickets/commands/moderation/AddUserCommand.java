package me.efco.yadbtickets.commands.moderation;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.entities.Helper;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class AddUserCommand extends AbstractCommand {
    public AddUserCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getTextChannel();
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(guild.getIdLong());
        Member member = event.getMember();
        Member newUser = event.getOption("newuser").getAsMember();

        if (!Helper.getInstance().hasTicketSupportPrivileges(member, guild, serverInfo)) {
            event.reply("You don't have permission to use this command!").setEphemeral(true).queue();
            return;
        }

        channel.getManager()
                .putMemberPermissionOverride(newUser.getIdLong(), List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND), List.of())
                .queue();

        event.reply("``User " + newUser.getEffectiveName() + " has been added to this ticket, and can now participate!``").queue();
    }
}
