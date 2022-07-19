package top.angelinaBot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.message.data.ImageType;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.bean.SpringContextRunner;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.EnableMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.model.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.service.AdminService;
import top.angelinaBot.util.DHashUtil;
import top.angelinaBot.util.SendMessageUtil;
import top.angelinaBot.vo.JsonResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * QQ群聊消息处理接口
 **/
@RequestMapping("GroupChat")
@RestController
@Slf4j
public class GroupChatController {

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private EnableMapper enableMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private FunctionMapper functionMapper;

    private final Map<Long, List<Long>> qqMsgRateList = new HashMap<>();

    /**
     * 通用的qq群聊消息处理接口，可以通过代码内部调用，也可以通过Post接口调用
     *
     * @param message 消息的封装方法
     * @return 返回消息的封装
     * @throws InvocationTargetException 反射相关异常
     * @throws IllegalAccessException    反射相关异常
     */
    @PostMapping("receive")
    public JsonResult<ReplayInfo> receive(MessageInfo message) throws InvocationTargetException, IllegalAccessException {
        //如果群组关闭，则拒绝接收消息,即将发送方与接收方合并
        if (!message.getUserAdmin().equals(MemberPermission.MEMBER)) {
            String messageInfo = "";
            if(!(message.getText()==null)){
                messageInfo =message.getText();
            }
            if(messageInfo.equals("群组开启")){
                ReplayInfo replayInfo = new ReplayInfo(message);
                if(this.enableMapper.canUseGroup(message.getGroupId(),0)==1){
                    this.enableMapper.closeGroup(message.getGroupId(), 1);
                    replayInfo.setReplayMessage("开启成功");
                }else {
                    replayInfo.setReplayMessage("请不要重复开启");
                }sendMessageUtil.sendGroupMsg(replayInfo);
            }
        }
        if (this.enableMapper.canUseGroup(message.getGroupId(), 0) == 1){
            message.setQq(message.getLoginQq());
        }
        //不处理自身发送的消息
        if (!message.getLoginQq().equals(message.getQq())) {
            log.info("接受到群消息:{}", message.getEventString());
            if (message.getCallMe()) { //当判断被呼叫时，调用反射响应回复
                if (getMsgLimit(message)) {
                    activityMapper.getGroupMessage();
                    if (AngelinaContainer.chatMap.containsKey(message.getKeyword())) {
                        List<String> s = AngelinaContainer.chatMap.get(message.getKeyword());
                        ReplayInfo replayInfo = new ReplayInfo(message);
                        //判断该群是否已关闭该功能
                        if (adminMapper.canUseFunction(message.getGroupId(), s.get(0)) == 0) {
                            replayInfo.setReplayMessage(s.get(new Random().nextInt(s.size())).replace("{userName}", message.getName()));
                            sendMessageUtil.sendGroupMsg(replayInfo);
                            return JsonResult.success(replayInfo);
                        }
                    } else if (AngelinaContainer.groupMap.containsKey(message.getKeyword())) {
                        Method method = AngelinaContainer.groupMap.get(message.getKeyword());
                        if (adminMapper.canUseFunction(message.getGroupId(), method.getName()) == 0) {
                            functionMapper.insertFunction(method.getName());
                            ReplayInfo invoke = (ReplayInfo) method.invoke(SpringContextRunner.getBean(method.getDeclaringClass()), message);
                            if (message.isReplay()) {
                                sendMessageUtil.sendGroupMsg(invoke);
                            }
                            return JsonResult.success(invoke);
                        }
                    }
                }
            } else if (message.getKeyword() == null && message.getImgUrlList().size() == 1 && message.getImgTypeList().get(0) != ImageType.GIF) {
                //没有文字且只有一张非gif图片的时候，准备DHash运算
                String dHash = DHashUtil.getDHash(message.getImgUrlList().get(0));
                for (String s : AngelinaContainer.dHashMap.keySet()) {
                    //循环比对海明距离，小于6的直接触发
                    if (DHashUtil.getHammingDistance(dHash, s) < 6) {
                        Method method = AngelinaContainer.dHashMap.get(s);
                        functionMapper.insertFunction(method.getName());
                        ReplayInfo invoke = (ReplayInfo) method.invoke(SpringContextRunner.getBean(method.getDeclaringClass()), message);
                        if (message.isReplay()) {
                            sendMessageUtil.sendGroupMsg(invoke);
                        }
                        return JsonResult.success(invoke);
                    }
                }
            }else if(message.getJSONObjectCTMC().startsWith("[mirai:app")){
                ReplayInfo replayInfo = new ReplayInfo(message);
                JSONObject jSONObject = new JSONObject(message.getJSONObjectCTS());
                String prompt = jSONObject.getString("prompt").trim();
                if(prompt.equals("[QQ小程序]哔哩哔哩")){
                    //只有开启了解析才使用
                    if (this.enableMapper.canUseBilibili(message.getGroupId(), 1) == 1){
                        JSONObject meta = jSONObject.getJSONObject("meta");
                        JSONObject detail_1 = meta.getJSONObject("detail_1");
                        String desc = detail_1.getString("desc");
                        String qqdocurl =StringUtils.substringBefore(detail_1.getString("qqdocurl"),"?");
                        replayInfo.setReplayMessage("有群友发送了一条哔哩哔哩分享\n分享名为："+desc+"\n链接为："+qqdocurl);
                        sendMessageUtil.sendGroupMsg(replayInfo);
                    }
                }
            }
        }
        return null;
    }

    //消息回复限速机制
    private boolean getMsgLimit(MessageInfo messageInfo) {
        boolean flag = true;
        //每10秒限制三条消息,10秒内超过5条就不再提示
        int length = 3;
        int maxTips = 5;
        int second = 10;
        long qq = messageInfo.getQq();
        String name = messageInfo.getName();
        if (!qqMsgRateList.containsKey(qq)) {
            List<Long> msgList = new ArrayList<>(maxTips);
            msgList.add(System.currentTimeMillis());
            qqMsgRateList.put(qq, msgList);
        }
        List<Long> limit = qqMsgRateList.get(qq);
        if (limit.size() <= length) {
            //队列未满三条，直接返回消息
            limit.add(System.currentTimeMillis());
        } else {
            if (getSecondDiff(limit.get(0), second)) {
                //队列长度超过三条，但是距离首条消息已经大于10秒
                limit.remove(0);
                //把后面两次提示的时间戳删掉
                while (limit.size() > 3) {
                    limit.remove(3);
                }
                limit.add(System.currentTimeMillis());
            } else {
                if (limit.size() <= maxTips) {
                    //队列长度在3~5之间，并且距离首条消息不足10秒，发出提示
                    log.warn("{}超出单人回复速率,{}", name, limit.size());
                    ReplayInfo replayInfo = new ReplayInfo(messageInfo);
                    replayInfo.setReplayMessage(name + "说话太快了，请稍后再试");
                    sendMessageUtil.sendGroupMsg(replayInfo);
                    limit.add(System.currentTimeMillis());
                } else {
                    //队列长度等于5，直接忽略消息
                    log.warn("{}连续请求,已拒绝消息", name);
                }
                flag = false;
            }
        }
        //对队列进行垃圾回收
        gcMsgLimitRate();

        return flag;
    }

    /**
     * 计算时间差
     */
    public boolean getSecondDiff(Long timestamp, int second) {
        return (System.currentTimeMillis() - timestamp) / 1000 > second;
    }

    /**
     * 消息速率列表的辣鸡回收策略
     */
    public void gcMsgLimitRate() {
        //大于1024个队列的时候进行垃圾回收,大概占用24k
        if (qqMsgRateList.size() > 1024) {
            log.warn("开始对消息速率队列进行回收，当前map长度为：{}", qqMsgRateList.size());
            //回收所有超过30秒的会话
            qqMsgRateList.entrySet().removeIf(entry -> getSecondDiff(entry.getValue().get(0), 30));
            log.info("消息速率队列回收结束，当前map长度为：{}", qqMsgRateList.size());
        }
    }
}
