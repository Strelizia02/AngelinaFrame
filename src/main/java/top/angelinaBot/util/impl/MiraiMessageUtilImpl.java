package top.angelinaBot.util.impl;

import it.sauronsoftware.jave.*;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;

import java.io.File;
import java.util.List;
import java.util.UUID;

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
        try {
            //解析replayInfo
            String replayMessage = replayInfo.getReplayMessage();
            List<ExternalResource> replayImgList = replayInfo.getReplayImg();
            String kick = replayInfo.getKick();
            Integer muted = replayInfo.getMuted();
            Boolean nudged = replayInfo.getNudged();
            File mp3 = replayInfo.getMp3();

            //获取登录bot
            Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
            //获取群
            Group group = bot.getGroupOrFail(replayInfo.getGroupId());
            if (replayMessage != null && replayImgList.size() > 0) {
                //发送图片 + 文字
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                        .append(new PlainText(replayMessage));

                for (ExternalResource replayImg : replayImgList) {
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
                for (ExternalResource replayImg : replayImgList) {
                    messageChainBuilder.append(group.uploadImage(replayImg));
                }
                MessageChain chain = messageChainBuilder.build();
                group.sendMessage(chain);
            }

            if (mp3 != null) {
//                File amr = new File("runFile/silkCache/" + UUID.randomUUID());//输出
//                AudioAttributes audio = new AudioAttributes();
//                audio.setCodec("libamr_nb");//编码器
//
//                audio.setBitRate(12200);//比特率
//                audio.setChannels(1);//声道；1单声道，2立体声
//                audio.setSamplingRate(8000);//采样率（重要！！！）
//
//                EncodingAttributes attrs = new EncodingAttributes();
//                attrs.setFormat("amr");//格式
//                attrs.setAudioAttributes(audio);//音频设置
//                Encoder encoder = new Encoder();
//                try {
//                    encoder.encode(mp3, amr, attrs);
//                } catch (EncoderException e) {
//                    e.printStackTrace();
//                }
                ExternalResource externalResource = ExternalResource.create(mp3);

                MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                messageChainBuilder.append(group.uploadAudio(externalResource));
                group.sendMessage(messageChainBuilder.build());
            }
            if (kick != null) {
                //踢出群
                group.getOrFail(replayInfo.getQq()).kick("");
            }

            if (muted != null) {
                //禁言muted秒
                group.getOrFail(replayInfo.getQq()).mute(muted);
            }

            if (nudged) {
                //戳一戳
                group.getOrFail(replayInfo.getQq()).nudge();
            }

            log.info("发送消息" + replayInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
        }
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
        File mp3 = replayInfo.getMp3();

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
}
