package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.AngelinaSendMessageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static top.angelinaBot.container.QQFrameContainer.QQChannel;

/**
 * Mirai发送消息封装方法
 */
@Component(QQChannel)
@Slf4j
public class ChannelMessageUtilImpl implements AngelinaSendMessageUtil {

    @Value("${userConfig.token}")
    public String userConfigToken;

    @Value("${userConfig.appId}")
    public String userConfigAppId;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    RestTemplate restTemplate;

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

        String url = "https://sandbox.api.sgroup.qq.com/channels/" + replayInfo.getGroupId() + "/messages";

        JSONObject obj = new JSONObject();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bot " + userConfigAppId + "." + userConfigToken);
        httpHeaders.set("Content-Type", "multipart/form-data");
//        httpHeaders.set("Content-Type", "application/json");


        try {
            if (replayMessage != null) {
                //发送文字
                obj.put("content", replayMessage);
            }

            if (replayImgList.size() > 0) {
                //发送图片
                for (Object replayImg : replayImgList) {
                    if (replayImg instanceof String) {
                        obj.put("image", replayImg);
                    } else if (replayImg instanceof File) {
                        obj.put("file_image", ((File)replayImg).getAbsolutePath());
                    } else if (replayImg instanceof BufferedImage) {
                        File dir = new File("runFile/cache/");
                        if (!dir.exists()) {
                            boolean mkdir = dir.mkdir();
                        }
                        String path  = "runFile/cache/" + UUID.randomUUID();
                        ImageIO.write((BufferedImage) replayImg, "png", new File(path));
                        obj.put("image", path);
                    }
                }
            }

            String s = obj.toString();
            HttpEntity<String> httpEntity = new HttpEntity<>(s, httpHeaders);

            restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

            log.info("发送消息" + replayInfo);
            Thread.sleep(new Random().nextInt(5) * 500 + 500);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
        }
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        log.info("目前频道不发送私聊消息" + replayInfo.getReplayMessage());
    }
}
