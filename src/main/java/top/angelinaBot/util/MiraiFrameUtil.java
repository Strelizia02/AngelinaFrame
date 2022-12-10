package top.angelinaBot.util;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.internal.message.image.OnlineFriendImage;
import net.mamoe.mirai.internal.message.image.OnlineGroupImage;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.angelinaBot.Exception.AngelinaException;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.container.AngelinaEventSource;
import top.angelinaBot.container.QQFrameContainer;
import top.angelinaBot.controller.EventsController;
import top.angelinaBot.controller.FriendChatController;
import top.angelinaBot.controller.GroupChatController;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.PermissionEnum;
import xyz.cssxsh.mirai.device.MiraiDeviceGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Mirai框架的通用启动方法
 */
@Component
@Slf4j
public class MiraiFrameUtil {

    @Autowired
    private GroupChatController groupChatController;

    @Autowired
    private FriendChatController friendChatController;

    @Autowired
    private EventsController eventsController;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * rebuild状态，1为正在rebuild，0为已经rebuild完成
     */
    private volatile int status = 0;

    //qq列表
    @Value("#{'${userConfig.qqList}'.split(' ')}")
    private String[] qqList;

    //密码列表
    @Value("#{'${userConfig.pwList}'.split(' ')}")
    private String[] pwList;

    @Value("#{'${userConfig.typeList}'.split(' ')}")
    private String[]   typeList;

    //bot的呼叫关键字
    @Value("#{'${userConfig.botNames}'.split(' ')}")
    public String[] botNames;

    //维护一个群号/账号服务映射，保证一个群只有一个账号提供服务
    public static final Map<String, String> messageIdMap = new HashMap<>();

    public final Map<String, BotConfiguration.MiraiProtocol> type = new HashMap<>();
    {
        type.put("IPAD", BotConfiguration.MiraiProtocol.IPAD);
        type.put("ANDROID_PHONE", BotConfiguration.MiraiProtocol.ANDROID_PHONE);
        type.put("ANDROID_PAD", BotConfiguration.MiraiProtocol.ANDROID_PAD);
        type.put("ANDROID_WATCH", BotConfiguration.MiraiProtocol.ANDROID_WATCH);
        type.put("MACOS", BotConfiguration.MiraiProtocol.MACOS);
    }

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

            long qq = Long.parseLong(qqList[i]);
            BotConfiguration.MiraiProtocol miraiProtocol = type.get(typeList[i]);
            Bot bot = BotFactory.INSTANCE.newBot(qq, pwList[i], new BotConfiguration() {{
                log.info("尝试登录{}", qq);
                setDeviceInfo(bot -> new MiraiDeviceGenerator().load(bot));
                fileBasedDeviceInfo("runFile/" + qq + "device.json");
                setProtocol(miraiProtocol);

            }});
            try {
                bot.login();
            } catch (Exception e) {
                e.printStackTrace();
                log.error(bot.getId() + " 登录失败");
            }
        }

        reBuildBotGroupMap();

        //监听全部qq账号所接受的消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            if (messageIdMap.get(event.getGroup().getId() + "").equals(event.getBot().getId() + "")) {
                MessageInfo messageInfo = getMessageInfo(event, botNames);
                messageInfo.setFrame(QQFrameContainer.Miari);
                try {
                    groupChatController.receive(messageInfo);
                    if (messageInfo.getText() != null) {
                        AngelinaEventSource.getInstance().handle(messageInfo);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //某个Bot被踢出群或退群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotLeaveEvent.class, event -> {
            messageIdMap.remove(event.getGroupId() + "");
            reBuildBotGroupMap();
        });

        //某个Bot加入了一个新群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotJoinGroupEvent.class, event -> {
            messageIdMap.put(event.getGroupId() + "", event.getBot().getId() + "");
            activityMapper.getEventMessage();
            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setEvent(EventEnum.MemberJoinEvent);
            messageInfo.setLoginQq(event.getBot().getId() + "");
            messageInfo.setGroupId(event.getGroup().getId() + "");
            messageInfo.setFrame(QQFrameContainer.Miari);
            try {
                eventsController.receive(messageInfo);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        //群撤回消息
        GlobalEventChannel.INSTANCE.subscribeAlways(MessageRecallEvent.GroupRecall.class, event -> {
            if (messageIdMap.get(event.getGroup().getId() + "").equals(event.getBot().getId() + "")) {
                activityMapper.getEventMessage();
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setEvent(EventEnum.GroupRecall);
                messageInfo.setLoginQq(event.getBot().getId() + "");
                messageInfo.setGroupId(event.getGroup().getId() + "");
                messageInfo.setQq(event.getAuthorId() + "");
                messageInfo.setFrame(QQFrameContainer.Miari);
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
                if (messageIdMap.get(subject.getId() + "").equals(event.getBot().getId() + "") && event.getTarget() instanceof Bot){
                    activityMapper.getEventMessage();
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setEvent(EventEnum.NudgeEvent);
                    messageInfo.setLoginQq(event.getBot().getId() + "");
                    messageInfo.setGroupId(subject.getId() + "");
                    messageInfo.setQq(event.getFrom().getId() + "");
                    messageInfo.setFrame(QQFrameContainer.Miari);
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
            if (messageIdMap.get(event.getGroup().getId() + "").equals(event.getBot().getId() + "")) {
                activityMapper.getEventMessage();
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setEvent(EventEnum.MemberJoinEvent);
                messageInfo.setLoginQq(event.getBot().getId() + "");
                messageInfo.setGroupId(event.getGroup().getId() + "");
                messageInfo.setQq(event.getMember().getId() + "");
                messageInfo.setName(event.getMember().getNick());
                messageInfo.setFrame(QQFrameContainer.Miari);
                try {
                    eventsController.receive(messageInfo);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        //某人退群
        GlobalEventChannel.INSTANCE.subscribeAlways(MemberLeaveEvent.class, event -> {
            if (messageIdMap.get(event.getGroup().getId() + "").equals(event.getBot().getId() + "")) {
                activityMapper.getEventMessage();
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setEvent(EventEnum.MemberLeaveEvent);
                messageInfo.setLoginQq(event.getBot().getId() + "");
                messageInfo.setGroupId(event.getGroup().getId() + "");
                messageInfo.setQq(event.getMember().getId() + "");
                messageInfo.setName(event.getMember().getNick());
                messageInfo.setFrame(QQFrameContainer.Miari);
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

        //账号掉线
        GlobalEventChannel.INSTANCE.subscribeAlways(BotMuteEvent.class, event -> {
            if (event.component1() > 60 * 60 * 24) {
                event.getGroup().quit();
            }
        });

        //账号上线
        GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class, event -> {
            reBuildBotGroupMap();
        });

        //好友消息
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, event -> {
            MessageInfo messageInfo = getMessageInfo(event, botNames);
            activityMapper.getFriendMessage();
            messageInfo.setFrame(QQFrameContainer.Miari);
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
        if (status == 0) {
            status = 1;
            for (Bot bot : Bot.getInstances()) {
                if (bot.isOnline()) {
                    for (Group group : bot.getGroups()) {
                        if (messageIdMap.get(group.getId() + "") == null) {
                            messageIdMap.put(group.getId() + "", bot.getId() + "");
                        }
                    }
                }
            }
            status = 0;
        } else {
            log.warn("is on rebuilding group-bot");
        }
    }

    /**
     * 根据Mirai的事件构建Message，方便后续调用
     * @param event Mirai事件
     * @param botNames 机器人名称
     */
    public MessageInfo getMessageInfo(GroupMessageEvent event, String[] botNames){
        MessageInfo messageInfo = new MessageInfo();
        // Mirai 的事件内容封装
        messageInfo.setLoginQq(event.getBot().getId() + "");
        messageInfo.setQq(event.getSender().getId() + "");
        messageInfo.setName(event.getSenderName());
        messageInfo.setGroupId(event.getSubject().getId() + "");
        messageInfo.setTime(event.getTime());
        if (AngelinaContainer.administrators.contains(messageInfo.getQq())) {
            messageInfo.setUserAdmin(PermissionEnum.Administrator);
        } else {
            switch (event.getSender().getPermission().getLevel()) {
                case 1:
                    messageInfo.setUserAdmin(PermissionEnum.GroupAdministrator);
                    break;
                case 2:
                    messageInfo.setUserAdmin(PermissionEnum.GroupMaster);
                    break;
                default:
                    messageInfo.setUserAdmin(PermissionEnum.GroupUser);
                    break;
            }
        }

        //获取消息体
        MessageChain chain = event.getMessage();
        messageInfo.setEventString(chain.toString());
        for (Object o: chain){
            if (o instanceof At) {
                //消息艾特内容
                messageInfo.getAtQQList().add(((At) o).getTarget() + "");
                if ((((At) o).getTarget() + "").equals(messageInfo.getLoginQq())) {
                    //如果被艾特则视为被呼叫
                    messageInfo.setCallMe(true);
                }
            } else if (o instanceof PlainText) {
                //消息文字内容
                messageInfo.setText(((PlainText) o).getContent().trim());
                String[] orders = messageInfo.getText().split("\\s+");
                if (orders.length > 0) {
                    messageInfo.setKeyword(orders[0]);
                    messageInfo.setArgs(Arrays.asList(orders));
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            messageInfo.setCallMe(true);
                            messageInfo.setKeyword(messageInfo.getKeyword().replace(name, ""));
                            break;
                        }
                    }
                }
            } else if (o instanceof OnlineGroupImage){ // 编译器有可能因为无法识别Kotlin的class而报红，问题不大能通过编译
                //消息图片内容
                messageInfo.getImgUrlList().add(((OnlineGroupImage) o).getOriginUrl());
                messageInfo.getImgTypeList().add(((OnlineGroupImage) o).getImageType());
            }
        }
        return messageInfo;
    }

    public MessageInfo getMessageInfo(FriendMessageEvent event, String[] botNames) {
        MessageInfo messageInfo = new MessageInfo();

        messageInfo.setLoginQq(event.getBot().getId() + "");
        messageInfo.setQq(event.getSender().getId() + "");
        messageInfo.setName(event.getSenderName());
        messageInfo.setTime(event.getTime());
        //获取消息体
        MessageChain chain = event.getMessage();
        messageInfo.setEventString(chain.toString());
        for (Object o: chain){
            if (o instanceof At) {
                //消息艾特内容
                messageInfo.getAtQQList().add(((At) o).getTarget() + "");
                if ((((At) o).getTarget() + "").equals(messageInfo.getLoginQq())){
                    //如果被艾特则视为被呼叫
                    messageInfo.setCallMe(true);
                }
            } else if (o instanceof PlainText) {
                //消息文字内容
                messageInfo.setText(((PlainText) o).getContent().trim());

                String[] orders = messageInfo.getText().split("\\s+");
                if (orders.length > 0) {
                    messageInfo.setKeyword(orders[0]);
                    messageInfo.setArgs(Arrays.asList(orders));
                    messageInfo.setCallMe(true);
                    for (String name: botNames){
                        if (orders[0].startsWith(name)){
                            messageInfo.setKeyword(messageInfo.getKeyword().replace(name, ""));
                            break;
                        }
                    }
                }
            } else if (o instanceof OnlineFriendImage){ // 编译器有可能因为无法识别Kotlin的class而报红，问题不大能通过编译
                //消息图片内容
                messageInfo.getImgUrlList().add(((OnlineFriendImage) o).getOriginUrl());
                messageInfo.getImgTypeList().add(((OnlineFriendImage) o).getImageType());
            }
        }
        return messageInfo;
    }
}
