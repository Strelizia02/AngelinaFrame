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
import org.springframework.stereotype.Component;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

/**
 * Mirai发送消息封装方法
 */
@Component("mirai")
@Slf4j
public class MiraiMessageUtilImpl implements SendMessageUtil {

    /**
     * 发送群消息方法
     * @param replayInfo 发送消息的结构封装
     */
    @Override
    public void sendGroupMsg(ReplayInfo replayInfo) {
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<BufferedImage> replayImgList = replayInfo.getReplayImg();
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

            try {
                for (BufferedImage replayImg: replayImgList) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(replayImg, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    ExternalResource externalResource = ExternalResource.create(is);
                    Image i = group.uploadImage(externalResource);
                    messageChainBuilder.append(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            group.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            try {
                for (BufferedImage replayImg: replayImgList) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(replayImg, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    ExternalResource externalResource = ExternalResource.create(is);
                    Image i = group.uploadImage(externalResource);
                    messageChainBuilder.append(i);
                }
                MessageChain chain = messageChainBuilder.build();
                group.sendMessage(chain);
            } catch (IOException e) {
                log.error("构建图片失败");
            }
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
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<BufferedImage> replayImgList = replayInfo.getReplayImg();
        String kick = replayInfo.getKick();
        Integer muted = replayInfo.getMuted();
        Boolean nudged = replayInfo.getNudged();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        User user = bot.getFriendOrFail(replayInfo.getQq());
        if (replayMessage != null && replayImgList.size() > 0) {
            //发送图片 + 文字
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(replayMessage));

            try {
                for (BufferedImage replayImg: replayImgList) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(replayImg, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    ExternalResource externalResource = ExternalResource.create(is);
                    Image i = user.uploadImage(externalResource);
                    messageChainBuilder.append(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            MessageChain chain = messageChainBuilder.build();
            user.sendMessage(chain);
        } else if (replayMessage != null) {
            //发送文字
            user.sendMessage(new PlainText(replayMessage));
        } else if (replayImgList.size() > 0) {
            //发送图片
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            try {
                for (BufferedImage replayImg: replayImgList) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(replayImg, "jpg", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    ExternalResource externalResource = ExternalResource.create(is);
                    Image i = user.uploadImage(externalResource);
                    messageChainBuilder.append(i);
                }
                MessageChain chain = messageChainBuilder.build();
                user.sendMessage(chain);
            } catch (IOException e) {
                log.error("构建图片失败");
            }
        }

        if (nudged) {
            //戳一戳
            user.nudge();
        }

        log.info("发送私聊消息" + replayInfo.getReplayMessage());
    }
}
