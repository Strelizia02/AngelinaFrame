package top.angelinaBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.model.*;

import java.util.List;
import java.util.Set;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @AngelinaGroup(keyWords = {"关闭"}, description = "关闭某个功能", permission = PermissionEnum.GroupAdministrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo closeFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1) {
            String key = messageInfo.getArgs().get(1);
            if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            if (AngelinaContainer.groupFuncNameMap.containsKey(key)) {
                String name = AngelinaContainer.groupFuncNameMap.get(key);
                adminMapper.closeFunction(messageInfo.getGroupId(), name);
                replayInfo.setReplayMessage("关闭功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.closeFunction(messageInfo.getGroupId(), key);
                        replayInfo.setReplayMessage("关闭功能 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，关闭失败");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"开启", "打开"}, description = "打开某个功能", permission = PermissionEnum.GroupAdministrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo openFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1) {
            String key = messageInfo.getArgs().get(1);
            if (key.equals("开启") || key.equals("打开") || key.equals("关闭")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            if (AngelinaContainer.groupFuncNameMap.containsKey(key)) {
                String name = AngelinaContainer.groupFuncNameMap.get(key);
                adminMapper.openFunction(messageInfo.getGroupId(), name);
                replayInfo.setReplayMessage("打开功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.openFunction(messageInfo.getGroupId(), key);
                        replayInfo.setReplayMessage("打开功能 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，打开失败");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"已关闭", "功能开关"}, description = "查看当前已关闭的功能", funcClass = FunctionType.FunctionAdmin)
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
    
    @AngelinaGroup(keyWords = {"停用", "停止"}, description = "全局关闭某个功能", permission = PermissionEnum.Administrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo stopFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1) {
            String key = messageInfo.getArgs().get(1);
            if (key.equals("停用") || key.equals("停止") || key.equals("启用") || key.equals("启动")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            
            if (AngelinaContainer.groupFuncNameMap.containsKey(key)) {
                String name = AngelinaContainer.groupFuncNameMap.get(key);
                adminMapper.stopFunction(name);
                replayInfo.setReplayMessage("停止功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.stopFunction(key);
                        replayInfo.setReplayMessage("停止功能 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，停止失败");
        return replayInfo;
    }
    
    @AngelinaGroup(keyWords = {"启用", "启动"}, description = "全局启用某个功能", permission = PermissionEnum.Administrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo startFunc(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1) {
            String key = messageInfo.getArgs().get(1);
            if (key.equals("停用") || key.equals("停止") || key.equals("启用") || key.equals("启动")) {
                replayInfo.setReplayMessage("您不能对基本操作进行更改");
                return replayInfo;
            }
            
            if (AngelinaContainer.groupFuncNameMap.containsKey(key)) {
                String name = AngelinaContainer.groupFuncNameMap.get(key);
                adminMapper.startFunction(name);
                replayInfo.setReplayMessage("启用功能 " + key + "成功");
                return replayInfo;
            } else {
                Set<EventEnum> eventEnums = AngelinaContainer.eventMap.keySet();
                for (EventEnum e: eventEnums) {
                    if (key.equals(e.getEventName())) {
                        adminMapper.startFunction(key);
                        replayInfo.setReplayMessage("启用功能 " + key + "成功");
                        return replayInfo;
                    }
                }
            }
        }
        replayInfo.setReplayMessage("未找到该功能，启用失败");
        return replayInfo;
    }
    
    @AngelinaGroup(keyWords = {"已停用", "已停止"}, description = "查看哪些功能已全局关闭", funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo stopFuncList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        List<String> stopFunction = adminMapper.getStopFunction();
        if (stopFunction.size() == 0) {
            replayInfo.setReplayMessage("当前功能均已启用");
            return replayInfo;
        }
        StringBuilder sb = new StringBuilder();
        for (String s: stopFunction) {
            sb.append(s).append("   ").append("停用").append("\n");
        }
        replayInfo.setReplayMessage(sb.toString());
        return replayInfo;
    }
}
