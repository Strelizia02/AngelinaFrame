package top.angelinaBot.controller;

import org.json.JSONObject;
import top.angelinaBot.aspect.AngelinaAspect;
import top.angelinaBot.bean.SpringContextRunner;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.MsgBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.DHashUtil;
import top.angelinaBot.util.SendMessageUtil;
import top.angelinaBot.vo.JsonResult;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * QQ群聊消息处理接口
 **/
@RequestMapping("GroupChat")
@RestController
@Slf4j
public class GroupChatController {

    @Value("${userConfig.loginQq}")
    private Long loginQq;

    @Resource(name = "opq")
    private SendMessageUtil sendMessageUtil;

    @PostMapping("receive")
    public JsonResult<ReplayInfo> receive(
            @RequestBody MsgBody msgBody
    ) throws InvocationTargetException, IllegalAccessException {
        JSONObject jsonObject = new JSONObject();
        //获取发送消息的群友qq
        Long qq = msgBody.getQq();
        //不处理自身发送的消息
        if (!qq.equals(loginQq)) {
            log.info("接受到消息:{}", msgBody.getText());
            //获取群号、昵称、消息
            Long groupId = msgBody.getGroupId();
            String name = msgBody.getName();
            String text = msgBody.getText();
            //封装消息
            MessageInfo message = new MessageInfo(qq, groupId, name, text);
            if (message.getCallMe()) { //当判断被呼叫时，调用反射响应回复
                if (AngelinaAspect.keyWordsMap.containsKey(message.getKeyword())) {
                    Method method = AngelinaAspect.keyWordsMap.get(message.getKeyword());
                    ReplayInfo invoke = (ReplayInfo) method.invoke(getClassNameByMethod(method), message);
                    sendMessageUtil.sendGroupMsg(invoke);
                    return JsonResult.success(invoke);
                }
            }else if (message.getKeyword() == null && message.getImgUrlList().size() == 1){
                //没有文字且只有一张图片的时候，准备DHash运算
                String dHash = DHashUtil.getDHash(message.getImgUrlList().get(0));
                for (String s: AngelinaAspect.dHashMap.keySet()){
                    //循环比对海明距离，小于6的直接触发
                    if (DHashUtil.getHammingDistance(dHash, s) < 6){
                        Method method = AngelinaAspect.dHashMap.get(dHash);
                        ReplayInfo invoke = (ReplayInfo) method.invoke(getClassNameByMethod(method), message);
                        sendMessageUtil.sendGroupMsg(invoke);
                        return JsonResult.success(invoke);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据Method获取Spring容器中类的实例
     *
     * @return
     */
    public Object getClassNameByMethod(Method method) {
        //获取method所属的类名
        String className = method.getDeclaringClass().getSimpleName();
        //类名首字母小写，去spring容器中查
        String classNameLower = Character.toLowerCase(className.charAt(0)) + className.substring(1);
        //返回bean
        return SpringContextRunner.getBean(classNameLower);
    }

}
