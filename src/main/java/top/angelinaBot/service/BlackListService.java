package top.angelinaBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.dao.BlackListMapper;
import top.angelinaBot.model.*;

import java.util.Set;

@Service
public class BlackListService {

    @Autowired
    private BlackListMapper blackListMapper;

    @AngelinaGroup(keyWords = {"拉黑", "加入黑名单"}, description = "拉黑某个QQ", permission = PermissionEnum.Administrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo addBlackList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1) {
            String qq = messageInfo.getArgs().get(1);
            blackListMapper.insertBlackList(qq);
        }
        replayInfo.setReplayMessage("拉黑成功");
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"黑名单", "已拉黑"}, description = "查看拉黑了哪些QQ", permission = PermissionEnum.Administrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo getBlackList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        StringBuilder sb = new StringBuilder();
        for (String qq: blackListMapper.selectBlackList()) {
            sb.append(qq).append("\n");
        }
        replayInfo.setReplayMessage(sb.toString());
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"移出黑名单", "取消拉黑"}, description = "取消拉黑某个QQ", permission = PermissionEnum.Administrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo removeBlackList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1) {
            String qq = messageInfo.getArgs().get(1);
            blackListMapper.deleteBlackList(qq);
        }
        replayInfo.setReplayMessage("拉黑成功");
        return replayInfo;
    }
}
