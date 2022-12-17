package top.angelinaBot.service;

import com.sun.management.OperatingSystemMXBean;
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
import top.angelinaBot.container.QQFrameContainer;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.model.Count;
import top.angelinaBot.model.FunctionCount;
import top.angelinaBot.model.MessageCount;
import top.angelinaBot.model.QQ;
import top.angelinaBot.util.MiraiFrameUtil;

import java.lang.management.ManagementFactory;
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

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired(required = false)
    private SendDataService sendDataService;

    public void heartBeats() {
        String url = "http://localhost:8087/bot/heartBeats";

        String botId = null;
        String s = adminMapper.selectId();
        if (!"0".equals(s)) {
            botId = s;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", botId);
        requestBody.put("name", botNames.split(" ")[0]);
        List<QQ> qqList1 = new ArrayList<>();
        for (int i = 0; i < qqList.length; i++) {
            qqList1.add(new QQ(qqList[i], QQFrameContainer.Miari, typeList[i], Bot.getInstance(Long.parseLong(qqList[i])).isOnline()));
        }
        if (userConfigToken.isEmpty()) {
            qqList1.add(new QQ(userConfigToken, QQFrameContainer.QQChannel, userConfigType, true));
        }

        requestBody.put("list", qqList1);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        String body = restTemplate.postForEntity(url, httpEntity, String.class).getBody();

        log.info("心跳成功");
        if ("0".equals(s)) {
            JSONObject obj = new JSONObject(body);
            adminMapper.updateId(obj.getString("data"));
        }
    }

    public void exterminateJob() {
        String url = "http://localhost:8087/bot/pushData";
        List<MessageCount> messageCount = activityMapper.selectCount();

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() - runtime.freeMemory();
        List<FunctionCount> functionCount = functionMapper.selectFunction();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messageCount", messageCount);
        requestBody.put("totalMemory", totalMemory);
        requestBody.put("functionCount", functionCount);
        requestBody.put("botId", adminMapper.selectId());

        if (sendDataService != null) {
            Count count = sendDataService.sendData();
            if (count != null) {
                requestBody.put("qqCount", count.getQqCount());
                requestBody.put("groupCount", count.getGroupCount());
            }
        }

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        restTemplate.postForEntity(url, httpEntity, String.class);

        activityMapper.clearActivity();
        functionMapper.deleteFunctionTable();
        log.info("运行数据同步成功");
    }
}
