package top.angelinaBot.util;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.DeviceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.angelinaBot.Exception.AngelinaException;
import top.angelinaBot.controller.EventsController;
import top.angelinaBot.controller.FriendChatController;
import top.angelinaBot.controller.GroupChatController;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Mirai框架的通用启动方法
 */
@Component
public class MiraiFrameUtil {

    @Autowired
    private GroupChatController groupChatController;

    @Autowired
    private FriendChatController friendChatController;

    @Autowired
    private EventsController eventsController;

    //qq列表
    @Value("#{'${userConfig.qqList}'.split(' ')}")
    private String[] qqList;

    //密码列表
    @Value("#{'${userConfig.pwList}'.split(' ')}")
    private String[] pwList;

    //bot的呼叫关键字
    @Value("#{'${userConfig.botNames}'.split(' ')}")
    public String[] botNames;

    //维护一个群号/账号服务映射，保证一个群只有一个账号提供服务
    private final Map<Long, Long> messageIdMap = new HashMap<>();

    /**
     * Mirai框架的启动方法，一次性启动Mirai并开启消息监听
     */
    public void startMirai(){
        //判断一下qq数量和密码数量是否匹配
        if (qqList.length == 0 || qqList.length != pwList.length){
            throw new AngelinaException("当前配置文件中账号密码配置有误，请审视配置文件");
        }

        for (int i = 0; i < qqList.length; i++) {
            //循环登录所有配置的qq账号，如果有需要滑块验证的，需要单独解决
            Bot bot = BotFactory.INSTANCE.newBot(Long.parseLong(qqList[i]), pwList[i], new BotConfiguration() {{
                setProtocol(MiraiProtocol.IPAD);
                setDeviceInfo(bot -> new DeviceInfo("Huawei.856832.001".getBytes(StandardCharsets.UTF_8),
                        "nova75g".getBytes(StandardCharsets.UTF_8),
                        "JEF-AN20".getBytes(StandardCharsets.UTF_8),
                        "Huawei Kirin 985".getBytes(StandardCharsets.UTF_8),
                        "Huawei".getBytes(StandardCharsets.UTF_8),
                        "Nova 7".getBytes(StandardCharsets.UTF_8),
                        "HarmonyOS 2.0".getBytes(StandardCharsets.UTF_8),
                        "Huawei/Nova/nova:7/MIRAI.200122.001/2736748:user/release-keys".getBytes(StandardCharsets.UTF_8),
                        "1BBBCCA8-0B4A-2EFC-BE95-E732C84DA5F0".getBytes(StandardCharsets.UTF_8),
                        "HarmonyOS version 2.0.0.221(C00E208R6P8)".getBytes(StandardCharsets.UTF_8),
                        "unknown".getBytes(StandardCharsets.UTF_8),
                        new DeviceInfo.Version(),
                        "T-Mobile".getBytes(StandardCharsets.UTF_8),
                        "HarmonyOS".getBytes(StandardCharsets.UTF_8),
                        "02:00:00:00:00:00".getBytes(StandardCharsets.UTF_8),
                        "02:00:00:00:00:00".getBytes(StandardCharsets.UTF_8),
                        "Strelitzia".getBytes(StandardCharsets.UTF_8),
                        "6e096dd53aa9062c".getBytes(StandardCharsets.UTF_8),
                        "342086728277870",
                        "wifi".getBytes(StandardCharsets.UTF_8)
                ));
            }});
            bot.login();
        }

        reBuildBotGroupMap();

        //监听全部qq账号所接受的消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            if (messageIdMap.get(event.getGroup().getId()) == event.getBot().getId()) {
                MessageInfo messageInfo = new MessageInfo(event, botNames);
                try {
                    groupChatController.receive(messageInfo);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //某个Bot被踢出群或退群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotLeaveEvent.class, event -> {
            reBuildBotGroupMap();
        });

        //某个Bot加入了一个新群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, event -> {
            messageIdMap.put(event.getGroupId(), event.getBot().getId());
        });

        //群撤回消息
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageRecallEvent.GroupRecall.class, event -> {
            if (messageIdMap.get(event.getGroup().getId()) == event.getBot().getId()) {
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setEvent(EventEnum.GroupRecall);
                messageInfo.setLoginQq(event.getBot().getId());
                messageInfo.setGroupId(event.getGroup().getId());
                messageInfo.setQq(event.getAuthorId());
                try {
                    eventsController.receive(messageInfo);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //戳一戳
        GlobalEventChannel.INSTANCE.subscribeAlways(NudgeEvent.class, event -> {
            Contact subject = event.getSubject();
            if (subject instanceof Group) {
                if (messageIdMap.get(subject.getId()) == event.getBot().getId() && event.getTarget() instanceof Bot){
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setEvent(EventEnum.NudgeEvent);
                    messageInfo.setLoginQq(event.getBot().getId());
                    messageInfo.setGroupId(subject.getId());
                    messageInfo.setQq(event.getFrom().getId());
                    try {
                        eventsController.receive(messageInfo);
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //某人进群
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinEvent.class, event -> {
            if (messageIdMap.get(event.getGroup().getId()) == event.getBot().getId()) {
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setEvent(EventEnum.MemberJoinEvent);
                messageInfo.setLoginQq(event.getBot().getId());
                messageInfo.setGroupId(event.getGroup().getId());
                messageInfo.setQq(event.getMember().getId());
                try {
                    eventsController.receive(messageInfo);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //某人退群
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.class, event -> {
            if (messageIdMap.get(event.getGroup().getId()) == event.getBot().getId()) {
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setEvent(EventEnum.MemberLeaveEvent);
                messageInfo.setLoginQq(event.getBot().getId());
                messageInfo.setGroupId(event.getGroup().getId());
                messageInfo.setQq(event.getMember().getId());
                try {
                    eventsController.receive(messageInfo);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //账号掉线
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOfflineEvent.class, event -> {
            reBuildBotGroupMap();
        });

        //账号上线
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            reBuildBotGroupMap();
        });

        //好友消息
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            MessageInfo messageInfo = new MessageInfo(event, botNames);
            try {
                friendChatController.receive(messageInfo);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 重建群号-账号映射，保证一个群只有一个账号提供服务
     */
    private void reBuildBotGroupMap() {
        //一个群号只能有一个bot提供服务
        for (Bot bot: Bot.getInstances()) {
            if (bot.isOnline()) {
                for (Group group : bot.getGroups()) {
                    messageIdMap.put(group.getId(), bot.getId());
                }
            }
        }
    }
}
