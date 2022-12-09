package top.angelinaBot.service;

import net.mamoe.mirai.Bot;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.PermissionEnum;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.MiraiFrameUtil;

@Service
public class AliveService {
    @AngelinaGroup(keyWords = {"切换"}, description = "切换活跃账号", permission = PermissionEnum.GroupAdministrator)
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
}
