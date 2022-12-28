package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.AngelinaSendMessageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

        for (String groupId: replayInfo.getGroupId()) {

            String url = "https://sandbox.api.sgroup.qq.com/channels/" + groupId + "/messages";

            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bot " + userConfigAppId + "." + userConfigToken);
            httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            params.add("msg_id", replayInfo.getMessageId());

            try {
                if (replayMessage != null) {
                    //发送文字
                    params.add("content", replayMessage);
                }

                if (replayImgList.size() > 0) {
                    //发送图片
                    for (Object replayImg : replayImgList) {
                        if (replayImg instanceof String) {
                            params.add("image", replayImg);
                        } else if (replayImg instanceof File) {
                            params.add("file_image", new FileSystemResource((File) replayImg));
                        } else if (replayImg instanceof BufferedImage) {
                            File f = new File("runFile/cache/" + UUID.randomUUID());
                            ImageIO.write((BufferedImage) replayImg, "png", f);
                            params.add("file_image", new FileSystemResource(f));
                        }
                    }
                }
                HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, httpHeaders);
                String body = restTemplate.postForEntity(url, httpEntity, String.class).getBody();
                log.error(body);

                log.info("发送消息" + replayInfo);
                Thread.sleep(new Random().nextInt(5) * 500 + 500);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("发送消息失败");
            }
        }
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        log.info("目前频道不发送私聊消息" + replayInfo.getReplayMessage());
    }


    public static byte[] file2byte(File file) {
        if (file == null) {
            return null;
        }
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fileInputStream.read(b)) != -1) {
                byteArrayOutputStream.write(b, 0 , n);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static byte[] bufferImage2byte(BufferedImage bf) {
        if (bf == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bf, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
