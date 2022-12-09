package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.MiraiFrameUtil;
import top.angelinaBot.util.SendMessageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static top.angelinaBot.container.QQFrameContainer.Miari;

/**
 * Mirai发送消息封装方法
 */
@Component(Miari)
@Slf4j
public class MiraiMessageUtilImpl implements SendMessageUtil {

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 发送群消息方法
     *
     * @param replayInfo 发送消息的结构封装
     */
    @Override
    public void sendGroupMsg(ReplayInfo replayInfo) {
        if (replayInfo == null) {
            return;
        }
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<Object> replayImgList = replayInfo.getReplayImg();
        String kick = replayInfo.getKick();
        Integer muted = replayInfo.getMuted();
        Boolean nudged = replayInfo.getNudged();
        File mp3 = replayInfo.getMp3();

        ExternalResource mp3Resource = null;
        if (mp3 != null) {
            mp3Resource = ExternalResource.create(mp3);
        }

        List<ExternalResource> imgResource = getExternalResource(replayImgList);


        //获取群
        for (String groupId : replayInfo.getGroupId()) {
            if (MiraiFrameUtil.messageIdMap.get(groupId) != null) {
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                try {
                    //获取登录bot
                    Bot bot = Bot.getInstance(Long.parseLong(MiraiFrameUtil.messageIdMap.get(groupId)));
                    Group group = bot.getGroupOrFail(Long.parseLong(groupId));

                    if (replayMessage != null) {
                        //发送文字
                        messageChainBuilder.append(new PlainText(replayMessage));
                    }

                    if (replayImgList.size() > 0) {
                        //发送图片
                        for (ExternalResource replayImg : imgResource) {
                            messageChainBuilder.append(group.uploadImage(replayImg));
                        }
                    }

                    if (mp3Resource != null) {
                        //发送语音
                        messageChainBuilder.append(group.uploadAudio(mp3Resource));
                    }

                    if (kick != null) {
                        //踢出群
                        group.getOrFail(Long.parseLong(replayInfo.getQq())).kick("");
                    }

                    if (muted != null) {
                        //禁言muted秒
                        group.getOrFail(Long.parseLong(replayInfo.getQq())).mute(muted);
                    }

                    if (nudged) {
                        //戳一戳
                        group.getOrFail(Long.parseLong(replayInfo.getQq())).nudge();
                    }
                    group.sendMessage(messageChainBuilder.build());
                    log.info("发送消息" + replayInfo);
                    Thread.sleep(new Random().nextInt(5) * 500 + 500);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("发送消息失败");
                }
            }
        }

        for (ExternalResource replayImg : imgResource) {
            try {
                replayImg.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<Object> replayImgList = replayInfo.getReplayImg();
        Boolean nudged = replayInfo.getNudged();

        List<ExternalResource> imgResource = getExternalResource(replayImgList);

        //获取登录bot
        Bot bot = Bot.getInstance(Long.parseLong(replayInfo.getLoginQQ()));
        User user = bot.getFriendOrFail(Long.parseLong(replayInfo.getQq()));
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();

        if (replayMessage != null) {
            //文字
            messageChainBuilder.append(new PlainText(replayMessage));
        }

        if (replayImgList.size() > 0) {
            //发送图片
            for (ExternalResource replayImg : imgResource) {
                messageChainBuilder.append(user.uploadImage(replayImg));
            }
        }

        user.sendMessage(messageChainBuilder.build());
        if (nudged) {
            //戳一戳
            user.nudge();
        }

        for (ExternalResource replayImg : imgResource) {
            try {
                replayImg.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("发送私聊消息" + replayInfo.getReplayMessage());
    }

    private List<ExternalResource> getExternalResource(List<Object> replayImgList) {
        List<ExternalResource> imgResource = new ArrayList<>();

        if (replayImgList.size() > 0) {
            for (Object o: replayImgList) {
                if (o instanceof String) {
                    try {
                        URL u = new URL((String) o);
                        HttpURLConnection httpUrl = (HttpURLConnection) u.openConnection();
                        httpUrl.connect();
                        try (InputStream is = httpUrl.getInputStream()){
                            ExternalResource externalResource = ExternalResource.create(is);
                            imgResource.add(externalResource);
                        }
                    } catch (IOException e) {
                        log.error("读取图片URL失败");
                    }
                } else if (o instanceof BufferedImage) {
                    try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
                        ImageIO.write((BufferedImage) o, "jpg", os);
                        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                        ExternalResource externalResource = ExternalResource.create(inputStream);
                        imgResource.add(externalResource);
                        inputStream.close();
                    } catch (IOException e) {
                        log.error("BufferImage读取IO流失败");
                    }
                } else if (o instanceof File) {
                    try (InputStream inputStream = Files.newInputStream(((File) o).toPath())){
                        ExternalResource externalResource = ExternalResource.create(inputStream);
                        imgResource.add(externalResource);
                    } catch (IOException e) {
                        log.error("File读取IO流失败");
                    }
                }
            }
        }

        return imgResource;
    }
}
