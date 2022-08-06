package top.angelinaBot.service;

import net.mamoe.mirai.Bot;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.MiraiFrameUtil;

@Service
public class AliveService {
    @AngelinaGroup( keyWords = {"切换"}, description = "切换活跃账号")
    public ReplayInfo switchAliveQQ(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);

        if (messageInfo.getArgs().size() > 1) {
            long qq = Long.parseLong(messageInfo.getArgs().get(1));
            Bot bot = Bot.getInstanceOrNull(qq);
            if (bot != null && bot.isOnline() && bot.getGroups().contains(messageInfo.getGroupId().longValue())) {
                MiraiFrameUtil.messageIdMap.put(messageInfo.getGroupId(), qq);
                replayInfo.setReplayMessage("已将本群bot切换为" + bot.getNick());
            } else {
                replayInfo.setReplayMessage("所选账号不在本群中或已被封号");
            }
        } else {
            replayInfo.setReplayMessage("请输入需要切换的qq号");
        }
        return replayInfo;
    }
}
