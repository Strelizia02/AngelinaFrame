package top.angelinaBot.util;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.angelinaBot.Exception.AngelinaException;
import top.angelinaBot.controller.GroupChatController;
import top.angelinaBot.model.MessageInfo;

import java.lang.reflect.InvocationTargetException;

/**
 * Mirai框架的通用启动方法
 */
@Component
public class MiraiFrameUtil {

    @Autowired
    private GroupChatController groupChatController;

    //qq列表
    @Value("#{'${userConfig.qqList}'.split(' ')}")
    private String[] qqList;

    //密码列表
    @Value("#{'${userConfig.pwList}'.split(' ')}")
    private String[] pwList;

    //bot的呼叫关键字
    @Value("#{'${userConfig.botNames}'.split(' ')}")
    public String[] botNames;

    /**
     * Mirai框架的启动方法，一次性启动Mirai并开启消息监听
     */
    public void startMirai(){
        //判断一下qq数量和密码数量是否匹配
        if (qqList.length == 0 || qqList.length != pwList.length){
            throw new AngelinaException("当前配置文件中账号密码配置有误，请审视配置文件");
        }

        for (int i = 0; i < qqList.length; i++) {
            //循环登录所有配置的qq账号，如果有需要滑块验证的，需要单独解决
            Bot bot = BotFactory.INSTANCE.newBot(Long.parseLong(qqList[i]), pwList[i], new BotConfiguration() {{
                setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
            }});
            bot.login();
        }

        //监听全部qq账号所接受的消息
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            MessageInfo messageInfo = new MessageInfo(event, botNames);
            try {
                groupChatController.receive(messageInfo);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
