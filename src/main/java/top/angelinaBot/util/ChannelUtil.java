package top.angelinaBot.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.angelinaBot.bean.SpringContextRunner;
import top.angelinaBot.container.QQFrameContainer;
import top.angelinaBot.controller.GroupChatController;
import top.angelinaBot.model.MessageInfo;

import javax.websocket.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ClientEndpoint
@Component
@Slf4j
public class ChannelUtil {

    @Value("${userConfig.token}")
    public String userConfigToken;

    @Value("${userConfig.appId}")
    public String userConfigAppId;

    @Value("${userConfig.type}")
    public String userConfigType;

    public static String token;
    public static String appId;
    public static String type;

    @Autowired
    protected RestTemplate restTemplate;

    private Session session;

    public static Integer massageIndex;

    public static Integer heartbeatInterval;

    public static String botId;

    public void init() {
        token = userConfigToken;
        appId = userConfigAppId;
        type = userConfigType;
        try {
            massageIndex = 0;
            heartbeatInterval = 50000;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI url = new URI("wss://sandbox.api.sgroup.qq.com/websocket");
            session = container.connectToServer(ChannelUtil.class, url);
            identify();
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("已成功连接到websocket");
        this.session = session;
    }

    /**
     * 接收消息
     * @param text
     */
    @OnMessage
    public void onMessage(String text) {
        log.info("接收到" + text);
        JSONObject obj = new JSONObject(text);
        if (obj.has("s")) {
            massageIndex = obj.getInt("s");
        }

        switch (obj.getInt("op")) {
            case 10://初次链接
                obj.getJSONObject("d").getInt("heartbeat_interval");
                break;
            case 0:
                switch (obj.getString("t")) {
                    case "READY":
                        log.info("鉴权成功");
                        botId = obj.getJSONObject("d").getJSONObject("user").getString("id");
                        break;
                    case "AT_MESSAGE_CREATE":
                        log.info("公域艾特消息");
                        JSONObject messageObj = obj.getJSONObject("d");
                        String id = messageObj.getJSONObject("author").getString("id");
                        String username = messageObj.getJSONObject("author").getString("username");
                        String channelId = messageObj.getString("channel_id");
                        String content = messageObj.getString("content");
                        MessageInfo messageInfo = new MessageInfo();


                        messageInfo.setGroupId(Long.parseLong(channelId));
                        messageInfo.setLoginQq(Long.parseLong(appId));

                        messageInfo.setName(username);
                        messageInfo.setCallMe(true);
                        Pattern pattern = Pattern.compile("\\u003c@!([0-9.]*)\\u003e");
                        Matcher matcher = pattern.matcher(content);
                        String s = matcher.replaceAll("").replace(" /", "");
                        messageInfo.setText(s);
                        try {
                            ((GroupChatController) SpringContextRunner.getBean(GroupChatController.class)).receive(messageInfo, QQFrameContainer.QQChannel);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    case "PUBLIC_MESSAGE_DELETE":
                        log.info("公域撤回");
                        break;
                    case "MESSAGE_CREATE":
                        log.info("私域普通消息");
                        break;
                    case "MESSAGE_DELETE":
                        log.info("私域撤回");
                        break;
                    case "DIRECT_MESSAGE_CREATE":
                        log.info("私聊消息");
                        break;
                    case "DIRECT_MESSAGE_DELETE":
                        log.info("私聊撤回");
                        break;
                    case "GUILD_MEMBER_ADD":
                        log.info("有成员加入");
                        break;
                    case "GUILD_MEMBER_REMOVE":
                        log.info("有成员退出");
                        break;
                }
                break;
        }
    }

    /**
     * 异常处理
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClosing() throws IOException {
        log.info("关闭websocket");
        session.close();
    }

    /**
     * 主动发送消息
     */
    public void send(JSONObject message) {
        log.info("发送ws：" + message);
        this.session.getAsyncRemote().sendText(message.toString());
    }

    public void identify() {
        //鉴权，可以理解为登录
        JSONObject identify = new JSONObject();
        identify.put("op", 2);
        JSONObject tokenObj = new JSONObject();
        tokenObj.put("token", "Bot " + appId + "." + token);
        Integer intents = 1 << 1 | 1 << 12 | 1 << 30;
        if (type.equals("private")) {
            intents = intents | 1 << 9;
        }
        tokenObj.put("intents", intents);
        tokenObj.put("shared", new int[]{0, 1});
        tokenObj.putOpt("properties", null);
        identify.put("d", tokenObj);
        send(identify);

        heartBeats();
    }

    @Async
    public void heartBeats() {
        //心跳维护websocket链接
        JSONObject first = new JSONObject();
        first.putOpt("op", null);
        first.put("d", massageIndex);
        send(first);
        while (true) {
            JSONObject obj = new JSONObject();
            obj.put("op", 1);
            obj.put("d",massageIndex);
            send(obj);
            try {
                Thread.sleep(heartbeatInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
