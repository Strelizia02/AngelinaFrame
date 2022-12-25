package top.angelinaBot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.bean.SpringContextRunner;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;
import top.angelinaBot.vo.JsonResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * QQ私聊消息处理接口
 **/
@RequestMapping("FriendChat")
@RestController
@Slf4j
public class FriendChatController {

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 通用的qq私聊消息处理接口，可以通过代码内部调用，也可以通过Post接口调用
     * @param message 消息的封装方法
     * @return 返回消息的封装
     * @throws InvocationTargetException 反射相关异常
     * @throws IllegalAccessException 反射相关异常
     */
    @PostMapping("receive")
    public JsonResult<ReplayInfo> receive(MessageInfo message) throws InvocationTargetException, IllegalAccessException {
        log.info("接受到私聊消息:{}", message.getText());
        if (AngelinaContainer.friendMap.containsKey(message.getKeyword())) {
            functionMapper.insertFunction(message.getName());
            Method method = AngelinaContainer.friendMap.get(message.getKeyword());
            ReplayInfo invoke = (ReplayInfo) method.invoke(SpringContextRunner.getBean(method.getDeclaringClass()), message);
            if (message.isReplay()) {
                sendMessageUtil.sendFriendMsg(invoke);
            }
            return JsonResult.success(invoke);
        }
        return null;
    }
}
