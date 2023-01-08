package top.angelinaBot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.bean.SpringContextRunner;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.dao.AdminMapper;
import top.angelinaBot.dao.BlackListMapper;
import top.angelinaBot.dao.FunctionMapper;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;
import top.angelinaBot.vo.JsonResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * QQ事件消息处理接口
 **/
@RequestMapping("Events")
@RestController
@Slf4j
public class EventsController {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private FunctionMapper functionMapper;

    @Autowired
    private BlackListMapper blackListMapper;

    @Autowired
    private SendMessageUtil sendMessageUtil;

    @Autowired
    private ActivityMapper activityMapper;

    /**
     * 通用的qq事件处理接口，可以通过代码内部调用，也可以通过Post接口调用
     * @param message 消息的封装方法
     * @return 返回消息的封装
     * @throws InvocationTargetException 反射相关异常
     * @throws IllegalAccessException 反射相关异常
     */
    @PostMapping("receive")
    public JsonResult<ReplayInfo> receive(MessageInfo message) throws InvocationTargetException, IllegalAccessException {
        //不处理自身发送的消息
        if (message.getLoginQq().equals(message.getQq())) {
            return null;
        }

        if (blackListMapper.selectBlackByQQ(message.getQq()) > 0) {
            //不处理拉黑人的消息
            return null;
        }

        log.info("接受到事件:{}", message.getEvent());
        activityMapper.getEventMessage();
        if (!AngelinaContainer.eventMap.containsKey(message.getEvent())) {
            //没有找到对应功能
            return null;
        }

        Method method = AngelinaContainer.eventMap.get(message.getEvent());
        if (adminMapper.canUseFunction(message.getGroupId(), message.getEvent().getEventName()) > 0) {
            //判断该群是否已关闭该功能
            return null;
        }

        AngelinaEvent annotation = method.getAnnotation(AngelinaEvent.class);
        functionMapper.insertFunction(annotation.event().getEventName());
        ReplayInfo invoke = (ReplayInfo) method.invoke(SpringContextRunner.getBean(method.getDeclaringClass()), message);
        if (message.isReplay()) {
            sendMessageUtil.sendGroupMsg(invoke);
        }
        return JsonResult.success(invoke);
    }
}
