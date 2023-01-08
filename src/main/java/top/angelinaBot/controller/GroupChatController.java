package top.angelinaBot.controller;

import net.mamoe.mirai.message.data.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.bean.SpringContextRunner;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.BlackListMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.model.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.angelinaBot.model.PermissionEnum;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.DHashUtil;
import top.angelinaBot.util.SendMessageUtil;
import top.angelinaBot.vo.JsonResult;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private AdminMapper adminMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @Autowired
    private BlackListMapper blackListMapper;

    private final Map<String, List<Long>> qqMsgRateList = new HashMap<>();


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
        if (message.getLoginQq().equals(message.getQq())) {
            //不处理自身发送的消息
            return null;
        }

        if (blackListMapper.selectBlackByQQ(message.getQq()) > 0) {
            //不处理拉黑人的消息
            return null;
        }

        log.info("bot[{}]接受到群[{}]消息:{}", message.getLoginQq(), message.getGroupId(), message.getEventString());
        if (message.getCallMe()) { //当判断被呼叫时，调用反射响应回复
            if (!getMsgLimit(message)) {
                //超出反应速率
                return null;
            }

            activityMapper.getGroupMessage();
            if (!AngelinaContainer.groupFuncNameMap.containsKey(message.getKeyword())) {
                //没有找到对应功能
                return null;
            }

            String name = AngelinaContainer.groupFuncNameMap.get(message.getKeyword());
            if (adminMapper.canUseFunction(message.getGroupId(), name) != 0) {
                //判断该群是否已关闭该功能
                return null;
            }

            if (AngelinaContainer.chatMap.containsKey(name)) {
                //该功能是一个闲聊功能
                List<String> s = AngelinaContainer.chatMap.get(name);
                ReplayInfo replayInfo = new ReplayInfo(message);
                String sendStr = s.get(new Random().nextInt(s.size())).replace("{userName}", message.getName());
                //解析大括号内容
                Pattern pattern = Pattern.compile("\\{[^}]*\\}");
                Matcher m = pattern.matcher(sendStr);
                while (m.find()) {
                    String str = m.group();
                    String[] split = str.substring(1, str.length() - 1).split("@");
                    if (split[0].equals("audio")) {
                        //添加语音
                        replayInfo.setMp3(split[1]);
                    } else if (split[0].equals("img")) {
                        //添加图片
                        replayInfo.setReplayImg(new File(split[1]));
                    }
                }
                replayInfo.setReplayMessage(m.replaceAll(" "));
                sendMessageUtil.sendGroupMsg(replayInfo);
                functionMapper.insertFunction(name);
                return JsonResult.success(replayInfo);
            }

            if (AngelinaContainer.groupMap.containsKey(name)) {
                //该功能是一个群聊功能
                Method method = AngelinaContainer.groupMap.get(name);
                //在这里获取函数的Permission注解来判断方法权限
                AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);
                PermissionEnum p = annotation.permission();
                PermissionEnum userAdmin = message.getUserAdmin();
                if (userAdmin.getLevel() < p.getLevel()) {
                    ReplayInfo replayInfo = new ReplayInfo(message);
                    replayInfo.setReplayMessage("该功能需要" + p.getName() + "权限才可以使用");
                    sendMessageUtil.sendGroupMsg(replayInfo);
                    return JsonResult.success(replayInfo);
                }

                functionMapper.insertFunction(name);
                ReplayInfo invoke = (ReplayInfo) method.invoke(SpringContextRunner.getBean(method.getDeclaringClass()), message);
                if (message.isReplay()) {
                    sendMessageUtil.sendGroupMsg(invoke);
                }
                return JsonResult.success(invoke);
            }
        } else if (message.getKeyword() == null && message.getImgUrlList().size() == 1 && message.getImgTypeList().get(0) != ImageType.GIF) {
            //没有文字且只有一张非gif图片的时候，准备DHash运算
            String dHash = DHashUtil.getDHash(message.getImgUrlList().get(0));
            for (String s : AngelinaContainer.dHashMap.keySet()) {
                //循环比对海明距离，小于6的直接触发
                if (DHashUtil.getHammingDistance(dHash, s) < 6) {
                    Method method = AngelinaContainer.dHashMap.get(s);
                    AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);
                    functionMapper.insertFunction(annotation.keyWords()[0]);
                    ReplayInfo invoke = (ReplayInfo) method.invoke(SpringContextRunner.getBean(method.getDeclaringClass()), message);
                    if (message.isReplay()) {
                        sendMessageUtil.sendGroupMsg(invoke);
                    }
                    return JsonResult.success(invoke);
                }
            }
        }
        return null;
    }

    /**
     * 消息回复限速机制
     */
    private boolean getMsgLimit(MessageInfo messageInfo) {
        boolean flag = true;
        //每10秒限制三条消息,10秒内超过5条就不再提示
        int length = 3;
        int maxTips = 5;
        int second = 10;
        String qq = messageInfo.getQq();
        if (qq != null) {
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
        }
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
