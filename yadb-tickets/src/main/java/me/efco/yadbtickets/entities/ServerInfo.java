package me.efco.yadbtickets.entities;

public class ServerInfo {
    private long serverId;
    private long channelId;
    private long supportId;
    private long categoryId;
    private long moderatorId;

    public ServerInfo(long serverId, long channelId, long supportId, long categoryId, long moderatorId) {
        this.serverId = serverId;
        this.channelId = channelId;
        this.supportId = supportId;
        this.categoryId = categoryId;
        this.moderatorId = moderatorId;
    }

    public long getServerId() {
        return serverId;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getSupportId() {
        return supportId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getModeratorId() {
        return moderatorId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public void setSupportId(long supportId) {
        this.supportId = supportId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setModeratorId(long moderatorId) {
        this.moderatorId = moderatorId;
    }
}
