package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;

import net.mamoe.mirai.utils.ExternalResource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
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

import static top.angelinaBot.container.QQFrameContainer.QQChannel;

/**
 * Mirai发送消息封装方法
 */
@Component(QQChannel)
@Slf4j
public class ChannelMessageUtilImpl implements SendMessageUtil {

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

        String url = "https://sandbox.api.sgroup.qq.com/channels/" + replayInfo.getChannel() + "/messages";

        JSONObject obj = new JSONObject();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bot " + userConfigAppId + "." + userConfigToken);
//        httpHeaders.set("Content-Type", "multipart/form-data");
        httpHeaders.set("Content-Type", "application/json");


        try {
            if (replayMessage != null) {
                //发送文字
                obj.put("content", replayMessage);
            }

            if (replayImgList.size() > 0) {
                //发送图片
                for (Object replayImg : replayImgList) {
                    obj.put("file_image", replayImg);
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
