package top.angelinaBot.bean;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.angelinaBot.Exception.AngelinaException;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.container.QQFrameContainer;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.service.CenterService;
import top.angelinaBot.service.InitAngelinaService;
import top.angelinaBot.util.AngelinaSendMessageUtil;
import top.angelinaBot.util.ChannelUtil;
import top.angelinaBot.util.MiraiFrameUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

import static top.angelinaBot.container.QQFrameContainer.*;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * 实现SmartInitializingSingleton接口，SpringBoot加载完所有Bean以后的回调这个接口。
 **/
@Component
@Slf4j
public class AngelinaInitialization implements SmartInitializingSingleton {

    @Autowired
    private MiraiFrameUtil miraiFrameUtil;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Value("${userConfig.botNames}")
    public String botNames;

    @Value("#{'${userConfig.administrators}'.split(' ')}")
    public String[] administrators;

    @Autowired
    public ChannelUtil channelUtil;

    @Value("${userConfig.appId}")
    public String appIds;

    @Value("${userConfig.qqList}")
    private String qqList;

    @Autowired(required = false)
    @Qualifier(Miari)
    private AngelinaSendMessageUtil MiarisendMessageUtil;

    @Autowired(required = false)
    @Qualifier(QQChannel)
    private AngelinaSendMessageUtil QQChannelsendMessageUtil;

    @Autowired(required = false)
    @Qualifier(Gocq)
    private AngelinaSendMessageUtil GocqsendMessageUtil;

    @Autowired(required = false)
    @Qualifier(Oicq)
    private AngelinaSendMessageUtil OicqsendMessageUtil;

    @Autowired
    private QQFrameContainer qqFrameContainer;

    @Autowired
    private CenterService centerService;

    @Autowired(required = false)
    private InitAngelinaService initAngelinaService;

    /**
     * 该方法仅在加载完所有的Bean以后，Spring完全启动前执行一次
     */
    @Override
    public void afterSingletonsInstantiated() {
        //读取所有的超级管理员
        AngelinaContainer.administrators.addAll(Arrays.asList(administrators));

        initQQFrameContainer();
        if (!"".equals(qqList)) {
            miraiFrameUtil.startMirai();
        } else {
            log.warn("您尚未填写登录QQ");
        }

        if (!"".equals(appIds)) {
            channelUtil.init();
            channelUtil.heartBeats();
        } else {
            log.warn("您尚未填写登录的频道appID");
        }
        if (botNames.equals("")) {
            throw new AngelinaException("请填写bot的名称！");
        }
        getJsonReplay();
        activityMapper.initActivityTable();
        functionMapper.initFunctionTable();
        adminMapper.initAdminTable();
        adminMapper.initIdTable();

        //用户自定义初始化接口
        if (initAngelinaService != null) {
            initAngelinaService.init();
        }
    }

    /**
     * 读取闲聊配置
     */
    private void getJsonReplay() {
        try {
            //闲聊配置应在运行目录下
            File file = new File("chatReplay.json");
            if (file.exists()) {
                //仅当文件存在时，流式读取文件内容
                FileReader fileReader = new FileReader(file);
                Reader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
                int ch;
                StringBuilder sb = new StringBuilder();
                while ((ch = reader.read()) != -1) {
                    sb.append((char) ch);
                }
                fileReader.close();
                reader.close();
                String jsonStr = sb.toString();
                //将文件内容格式化为JSON
                JSONArray chatReplayJson = new JSONArray(jsonStr);
                for (int i = 0; i < chatReplayJson.length(); i++) {
                    //循环判断每个JSON对象需要复读的内容
                    JSONObject funcJson = chatReplayJson.getJSONObject(i);
                    JSONArray keyWords = funcJson.getJSONArray("keyWords");
                    JSONArray replay = funcJson.getJSONArray("replay");
                    for (int j = 0; j < keyWords.length(); j++) {
                        //关键词内出现空格应预警
                        if (keyWords.getString(j).contains(" ")) {
                            log.warn("keyWord: " + keyWords.getString(j) + "中包含空格字符，实际代码处理中会去掉空格字符。");
                        }
                        //去掉空格，空格内容会让用户产生歧义
                        String keyWord = keyWords.getString(j).trim();
                        if (AngelinaContainer.chatMap.containsKey(keyWord)) {
                            //闲聊关键字重复判定
                            throw new AngelinaException("chatReplay.json中 " + keyWord + " 出现了多次，请检查修改chatReplay.json");
                        } else {
                            if (AngelinaContainer.groupMap.containsKey(keyWord)) {
                                Method method = AngelinaContainer.groupMap.get(keyWord);
                                //闲聊和群聊的关键字重复判定
                                throw new AngelinaException("chatReplay.json中" + keyWord + "与 " + method.getName() + " 的KeyWord重复，请检查修改重复内容");
                            } else {
                                List<String> replayList = new ArrayList<>();
                                for (int k = 0; k < replay.length(); k++) {
                                    replayList.add(replay.getString(k).trim());
                                }
                                AngelinaContainer.chatMap.put(keyWord, replayList);
                            }
                        }
                    }
                }
            } else {
                //如果文件不存在，默认创建一个空文件
                boolean newFile = file.createNewFile();
                FileWriter fw = new FileWriter(file);
                fw.write("[]");
                fw.close();
            }
        }catch (IOException e) {
            log.error("读取闲聊JSON文件失败，请检查chatReplay.json文件");
        }
    }

    /**
     * 初始化所有QQ框架信息
     */
    public void initQQFrameContainer() {
        qqFrameContainer.qqFrameMap.put(Miari, MiarisendMessageUtil);
        qqFrameContainer.qqFrameMap.put(QQChannel, QQChannelsendMessageUtil);
        qqFrameContainer.qqFrameMap.put(Gocq, GocqsendMessageUtil);
        qqFrameContainer.qqFrameMap.put(Oicq, OicqsendMessageUtil);
    }
}
