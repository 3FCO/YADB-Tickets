package me.efco.yadbtickets.events;

import me.efco.yadbtickets.Bot;
import me.efco.yadbtickets.data.DBConnection;
import me.efco.yadbtickets.entities.ServerInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class ButtonIntEvent extends ListenerAdapter {
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        int buttonId = Integer.parseInt(event.getInteraction().getButton().getId());
        String[] buttonInfo = DBConnection.getInstance().getButtonInteractions(buttonId);

        switch (buttonInfo[0].toLowerCase()) {
            case "autosetup" -> {
                event.getMessage().delete().queue();
                autoSetup(buttonInfo);
            }
            case "delete" -> {
                event.getMessage().delete().queue();
            }
        }
    }

    public void autoSetup(String[] buttonInfo) {
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(Long.parseLong(buttonInfo[1]));
        Guild guild = Bot.getApi().getGuildById(Long.parseLong(buttonInfo[1]));

        Role support = guild.getRoleById(serverInfo.getSupportId());
        if (support == null) {
            support = guild.createRole()
                    .setName("Ticket Support")
                    .setColor(Color.lightGray)
                    .submit().join();

            serverInfo.setSupportId(support.getIdLong());
        }
        Role moderator = guild.getRoleById(serverInfo.getModeratorId());
        if (moderator == null) {
            moderator = guild.createRole()
                    .setName("Ticket Moderator")
                    .setColor(Color.gray)
                    .submit().join();

            serverInfo.setModeratorId(moderator.getIdLong());
        }
        Category category = guild.getCategoryById(serverInfo.getCategoryId());
        if (category == null) {
            category = guild.createCategory("tickets")
                    .addRolePermissionOverride(guild.getPublicRole().getIdLong(), java.util.List.of(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND,Permission.MANAGE_CHANNEL))
                    .addRolePermissionOverride(support.getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND), java.util.List.of())
                    .addRolePermissionOverride(moderator.getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND,Permission.MANAGE_CHANNEL), java.util.List.of())
                    .submit().join();

            serverInfo.setCategoryId(category.getIdLong());
        }
        Channel channel = guild.getTextChannelById(serverInfo.getCategoryId());
        if (channel == null) {
            channel = guild.createTextChannel("master-ticket")
                    .setParent(category)
                    .syncPermissionOverrides()
                    .submit().join();

            serverInfo.setChannelId(channel.getIdLong());
        }

        DBConnection.getInstance().updateServerInfo(serverInfo);
    }
}
