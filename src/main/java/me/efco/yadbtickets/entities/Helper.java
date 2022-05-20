package me.efco.yadbtickets.entities;

import me.efco.yadbtickets.data.DBConnection;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;

public class Helper {
    private static Helper INSTANCE = new Helper();

    private Helper() {}

    public boolean isServerInfoValid(Guild guild) {
        ServerInfo serverInfo = DBConnection.getInstance().getServerInfo(guild.getIdLong());

        if (serverInfo == null) {
            return false;
        }

        Channel channel = guild.getTextChannelById(serverInfo.getChannelId());
        Category category = guild.getCategoryById(serverInfo.getCategoryId());
        Role role = guild.getRoleById(serverInfo.getSupportId());
        Role roleModerator = guild.getRoleById(serverInfo.getModeratorId());

        if (channel == null || category == null || role == null || roleModerator == null) {
            return false;
        }

        return true;
    }

    public ServerInfo setupTicket(Guild guild) {
        Role supportRole = guild.createRole()
                .setName("Ticket Support")
                .setColor(Color.lightGray)
                .submit().join();

        Role moderatorRole = guild.createRole()
                .setName("Ticket Moderator")
                .setColor(Color.gray)
                .submit().join();

        Category category = guild.createCategory("tickets")
                .addRolePermissionOverride(guild.getPublicRole().getIdLong(), java.util.List.of(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND,Permission.MANAGE_CHANNEL))
                .addRolePermissionOverride(supportRole.getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND), java.util.List.of())
                .addRolePermissionOverride(moderatorRole.getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND,Permission.MANAGE_CHANNEL), java.util.List.of())
                .submit().join();

        TextChannel channel = guild.createTextChannel("master-ticket")
                .setParent(category)
                .syncPermissionOverrides()
                .submit().join();

        ServerInfo serverInfo = new ServerInfo(guild.getIdLong(), channel.getIdLong(), supportRole.getIdLong(), category.getIdLong(), moderatorRole.getIdLong());
        DBConnection.getInstance().insertServerInfo(serverInfo);

        return serverInfo;
    }

    public void createIfAbsent(ServerInfo serverInfo, Guild guild) {

    }

    public static Helper getInstance() {
        return INSTANCE;
    }
}
