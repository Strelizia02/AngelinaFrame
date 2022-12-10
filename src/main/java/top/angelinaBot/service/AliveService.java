package top.angelinaBot.service;

import net.mamoe.mirai.Bot;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.FunctionType;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.PermissionEnum;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.MiraiFrameUtil;

import java.util.List;

@Service
public class AliveService {
    @AngelinaGroup(keyWords = {"切换"}, description = "切换活跃账号", permission = PermissionEnum.GroupAdministrator, funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo switchAliveQQ(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        String qq;

        if (messageInfo.getArgs().size() > 1) {
            qq = messageInfo.getArgs().get(1);
        } else {
            if (messageInfo.getAtQQList().size() == 1) {
                qq = messageInfo.getAtQQList().get(0);
            } else {
                replayInfo.setReplayMessage("请输入或艾特需要切换的qq号");
                return replayInfo;
            }
        }

        Bot bot = Bot.getInstanceOrNull(Long.parseLong(qq));
        if (bot != null && bot.isOnline() && bot.getGroups().contains(Long.parseLong(messageInfo.getGroupId()))) {
            MiraiFrameUtil.messageIdMap.put(messageInfo.getGroupId(), qq);
            replayInfo.setReplayMessage("已将本群bot切换为" + bot.getNick());
        } else {
            replayInfo.setReplayMessage("所选账号不在本群中或已被封号");
        }
        return replayInfo;
    }

    @AngelinaGroup(keyWords = {"在线Bot", "在线", "Bot列表", "在线bot", "bot列表"}, description = "查看当前所有Bot信息", funcClass = FunctionType.FunctionAdmin)
    public ReplayInfo getAliveQQ(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);

        List<Bot> bots = Bot.getInstances();
        int i = 0;
        for (Bot bot: bots) {
            replayInfo.setReplayMessage("[" + i + "]" + bot.getNick() + " " + bot.getId() + " " + (bot.isOnline() ? "在线\n" : "离线\n"));
        }
        return replayInfo;
    }
}
