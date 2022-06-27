package top.angelinaBot.service;

import net.mamoe.mirai.contact.MemberPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.EnableMapper;
import top.angelinaBot.model.EventEnum;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private EnableMapper enableMapper;

    @AngelinaGroup(keyWords = {"关闭"}, description = "关闭洁哥的某个功能")
    public ReplayInfo closeFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
            return replayInfo;
        }
        if (messageInfo.getArgs().size() > 1) {
            String key = messageInfo.getArgs().get(1);
            if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            if (AngelinaContainer.groupMap.containsKey(key)) {
                adminMapper.closeFunction(messageInfo.getGroupId(), AngelinaContainer.groupMap.get(key).getName());
                replayInfo.setReplayMessage("关闭功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.closeFunction(messageInfo.getGroupId(), AngelinaContainer.eventMap.get(e).getName());
                        replayInfo.setReplayMessage("关闭功能 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，关闭失败");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"开启", "打开"}, description = "打开洁哥的某个功能")
    public ReplayInfo openFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (!messageInfo.getUserAdmin().equals(MemberPermission.MEMBER)) {
            replayInfo.setReplayMessage("仅本群群主及管理员有权限对功能进行操作");
            return replayInfo;
        }
        if (messageInfo.getArgs().size() > 1) {
            String key = messageInfo.getArgs().get(1);
            if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            if (AngelinaContainer.groupMap.containsKey(key)) {
                adminMapper.openFunction(messageInfo.getGroupId(), AngelinaContainer.groupMap.get(key).getName());
                replayInfo.setReplayMessage("打开功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.openFunction(messageInfo.getGroupId(), AngelinaContainer.eventMap.get(e).getName());
                        replayInfo.setReplayMessage("打开功能 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，打开失败");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"已关闭", "功能开关"}, description = "查看洁哥当前已关闭的功能")
    public ReplayInfo funcList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        List<String> closeFunction = adminMapper.getCloseFunction(messageInfo.getGroupId());
        if (closeFunction.size() == 0) {
            replayInfo.setReplayMessage("当前本群功能均已开启");
            return replayInfo;
        }
        StringBuilder sb = new StringBuilder();
        for (String s: closeFunction) {
            sb.append(s).append("   ").append("关闭").append("\n");
        }
        replayInfo.setReplayMessage(sb.toString());
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"群组关闭"},description = "关闭某个群组所有接收消息")
    public ReplayInfo closeGroup(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (!messageInfo.getUserAdmin().equals(MemberPermission.ADMINISTRATOR)) {
            replayInfo.setReplayMessage("仅超级管理员有权限对功能进行操作");
            return replayInfo;
        } else {
            if (messageInfo.getArgs().size() > 1) {
                String key = (String)messageInfo.getArgs().get(1);
                char num[] = key.toCharArray();
                for (int i = 0; i < num.length; i++) {
                    if (Character.isDigit(num[i])) {
                        long numkey = Integer.parseInt(key);
                        this.enableMapper.closeGroup(numkey, 1);
                        replayInfo.setReplayMessage("关闭群组 " + numkey + "成功");
                    }else{
                        replayInfo.setReplayMessage("您输入的群组ID有误，请重新输入");
                    }
                }
            }else{
                replayInfo.setReplayMessage("请输入群组ID以关闭");
            }
            return replayInfo;
        }
    }

    @AngelinaGroup(keyWords = {"群组开启"},description = "开启某个群组所有接收消息")
    public ReplayInfo openGroup(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (!messageInfo.getUserAdmin().equals(MemberPermission.ADMINISTRATOR)) {
            replayInfo.setReplayMessage("仅超级管理员有权限对功能进行操作");
            return replayInfo;
        } else {
            if (messageInfo.getArgs().size() > 1) {
                String key = (String)messageInfo.getArgs().get(1);
                char num[] = key.toCharArray();
                for (int i = 0; i < num.length; i++) {
                    if (Character.isDigit(num[i])) {
                        long numkey = Integer.parseInt(key);
                        this.enableMapper.closeGroup(numkey, 0);
                        replayInfo.setReplayMessage("开启群组 " + numkey + "成功");
                    }else{
                        replayInfo.setReplayMessage("您输入的群组ID有误，请重新输入");
                    }
                }
            }else{
                replayInfo.setReplayMessage("请输入群组ID以开启");
            }
            return replayInfo;
        }
    }
}
