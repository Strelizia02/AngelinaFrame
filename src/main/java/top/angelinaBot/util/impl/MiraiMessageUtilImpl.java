package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Mirai发送消息封装方法
 */
@Component("mirai")
@Slf4j
public class MiraiMessageUtilImpl implements SendMessageUtil {

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 发送群消息方法
     * @param replayInfo 发送消息的结构封装
     */
    @Override
    public void sendGroupMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<Image> replayImgList = replayInfo.getReplayImg();
        String kick = replayInfo.getKick();
        Integer muted = replayInfo.getMuted();
        Boolean nudged = replayInfo.getNudged();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取群
        Group group = bot.getGroupOrFail(replayInfo.getGroupId());
        if (replayMessage != null && replayImgList.size() > 0) {
            //发送图片 + 文字
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(replayMessage));

            for (Image replayImg: replayImgList) {
                messageChainBuilder.append(replayImg);
            }
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            group.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (Image replayImg: replayImgList) {
                messageChainBuilder.append(replayImg);
            }
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        }
        if (kick != null) {
            //踢出群
            group.getOrFail(replayInfo.getQq()).kick("");
        }

        if (muted != null) {
            //禁言muted分钟
            group.getOrFail(replayInfo.getQq()).mute(muted);
        }

        if (nudged) {
            //戳一戳
            group.getOrFail(replayInfo.getQq()).nudge();
        }

        log.info("发送消息" + replayInfo.getReplayMessage());
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<Image> replayImgList = replayInfo.getReplayImg();
        Boolean nudged = replayInfo.getNudged();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        User user = bot.getFriendOrFail(replayInfo.getQq());
        if (replayMessage != null && replayImgList.size() > 0) {
            //发送图片 + 文字
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(replayMessage));

            for (Image replayImg: replayImgList) {
                messageChainBuilder.append(replayImg);
            }
            MessageChain chain = messageChainBuilder.build();
            user.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            user.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (Image replayImg: replayImgList) {
                messageChainBuilder.append(replayImg);
            }
            MessageChain chain = messageChainBuilder.build();
            user.sendMessage(chain);
        }

        if (nudged) {
            //戳一戳
            user.nudge();
        }

        log.info("发送私聊消息" + replayInfo.getReplayMessage());
    }
}
