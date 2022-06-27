package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.Stranger;
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
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();
        List<ExternalResource> replayAudioList = replayInfo.getReplayAudio();
        String kick = replayInfo.getKick();
        Long AT = replayInfo.getAT();
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

            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(group.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            group.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(group.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        }

        if (replayAudioList.size() > 0) {
            //发送语音
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (ExternalResource replayAudio: replayAudioList) {
                messageChainBuilder.append(group.uploadImage(replayAudio));
            }
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        }

        if (kick != null) {
            //踢出群
            group.getOrFail(replayInfo.getQq()).kick("");
        }

        //if (AT != null) {
            //@某个人
        //    group.getOrFail(replayInfo.getQq()).At(AT);
        //}

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
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();
        Boolean nudged = replayInfo.getNudged();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        User user = bot.getFriendOrFail(replayInfo.getQq());
        if (replayMessage != null && replayImgList.size() > 0) {
            //发送图片 + 文字
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(replayMessage));

            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(user.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            user.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            user.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(user.uploadImage(replayImg));
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

    @Override
    public void sendStrangerMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();
        Boolean nudged = replayInfo.getNudged();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        Stranger stranger = bot.getStranger(replayInfo.getQq());
        if (replayMessage != null && replayImgList.size() > 0) {
            //发送图片 + 文字
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(replayMessage));

            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(stranger.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            stranger.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            stranger.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(stranger.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            stranger.sendMessage(chain);
        }
        log.info("发送陌生人私聊消息" + replayInfo.getReplayMessage());
    }

    @Override
    public void sendGroupTempMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();
        Boolean nudged = replayInfo.getNudged();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        Group group = bot.getGroupOrFail(replayInfo.getGroupId());
        Member member = group.getOrFail(replayInfo.getQq());
        if (replayMessage != null && replayImgList.size() > 0) {
            //发送图片 + 文字
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(replayMessage));

            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(member.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            member.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            member.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            for (ExternalResource replayImg: replayImgList) {
                messageChainBuilder.append(member.uploadImage(replayImg));
            }
            MessageChain chain = messageChainBuilder.build();
            member.sendMessage(chain);
        }
        log.info("发送临时会话私聊消息" + replayInfo.getReplayMessage());
    }
}
