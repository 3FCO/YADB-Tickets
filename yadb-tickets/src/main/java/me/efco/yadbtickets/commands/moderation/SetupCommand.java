package me.efco.yadbtickets.commands.moderation;

import me.efco.yadbtickets.commands.interfaces.AbstractCommand;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.entities.Helper;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

public class SetupCommand extends AbstractCommand {
    public SetupCommand(String name, String description, List<Permission> permissions, List<OptionData> options, List<SubcommandData> subcommands) {
        super(name, description, permissions, options, subcommands);
    }

    @Override
    public void onExecution(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(guild.getIdLong());
        MessageBuilder messageBuilder = new MessageBuilder();

        OptionMapping optionSupport = event.getOption("support");
        OptionMapping optionModerator = event.getOption("moderator");
        OptionMapping optionChannel = event.getOption("channel");
        OptionMapping optionCategory = event.getOption("category");

        event.deferReply().queue();

        if (optionCategory == null && optionModerator == null && optionSupport == null && optionChannel == null) {
            ServerInfo newServerInfo = Helper.getInstance().setupTicket(guild);

            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .addField("Category", "" + newServerInfo.getCategoryId(), false)
                    .addField("Channel", guild.getTextChannelById(newServerInfo.getChannelId()).getAsMention(), false)
                    .addField("Support role", guild.getRoleById(newServerInfo.getSupportId()).getAsMention(), false)
                    .addField("Moderator role", guild.getRoleById(newServerInfo.getModeratorId()).getAsMention(), false);

            event.getHook().sendMessage(new MessageBuilder()
                    .append("All necessary channels and roles have been generated. Here is the info")
                    .setEmbeds(embedBuilder.build())
                    .build()).queue();
            return;
        }

        if (optionCategory != null) {
            Category category = guild.getCategoryById(optionCategory.getAsLong());
            if (category != null) {
                serverInfo.setCategoryId(category.getIdLong());
                messageBuilder.append("Ticket category has been updated to " + category.getAsMention() + "\n");
            }
        }
        if (optionChannel != null) {
            Channel channel = optionChannel.getAsTextChannel();
            serverInfo.setChannelId(channel.getIdLong());
            messageBuilder.append("Ticket channel has been updated to " + channel.getAsMention() + "\n");
        }
        if (optionSupport != null) {
            Role support = optionSupport.getAsRole();
            serverInfo.setSupportId(support.getIdLong());
            messageBuilder.append("Ticket support role has been updated to " + support.getAsMention() + "\n");
        }
        if (optionModerator != null) {
            Role moderator = optionModerator.getAsRole();
            serverInfo.setModeratorId(moderator.getIdLong());
            messageBuilder.append("Ticket support role has been updated to " + moderator.getAsMention() + "\n");
        }

        event.getHook().sendMessage("The following has been updated\n" + messageBuilder.build()).queue();
        DBConnection.getInstance().updateServerInfo(serverInfo);
    }
}
