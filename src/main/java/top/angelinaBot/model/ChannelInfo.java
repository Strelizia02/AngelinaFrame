package top.angelinaBot.model;

import top.angelinaBot.util.ChannelUtil;

public class ChannelInfo {
    //WebSocket的调用是通过tomcat唤起，无法读取Spring内部的bean，因此这部分内容需要通过static形式保存
    public String token;

    public String appId;

    public String type;

    public Integer massageIndex;

    public Integer heartbeatInterval;

    public String botId;

    public ChannelInfo(ChannelUtil channelUtil) {
        this.token = channelUtil.token;
        this.appId = channelUtil.appId;
        this.type = channelUtil.type;
        this.heartbeatInterval = channelUtil.heartbeatInterval;
        this.botId = channelUtil.botId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMassageIndex() {
        return massageIndex;
    }

    public void setMassageIndex(Integer massageIndex) {
        this.massageIndex = massageIndex;
    }

    public Integer getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(Integer heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }
}
