package top.angelinaBot.util;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.BotConfiguration;

/**
 * Mirai框架的通用启动方法
 */
public class MiraiFrameUtil {

    /**
     * Mirai框架的启动方法，一次性启动Mirai并开启消息监听
     */
    public static void startMirai(){
        Bot bot = BotFactory.INSTANCE.newBot(12345L, "123456", new BotConfiguration(){{
            setProtocol(BotConfiguration.MiraiProtocol.ANDROID_PAD);
        }});
        bot.login();
        Listener<GroupMessageEvent> listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            MessageChain chain = event.getMessage(); // 可获取到消息内容等, 详细查阅 `GroupMessageEvent`

            event.getSubject().sendMessage("Hello!"); // 回复消息
        });
        listener.complete();
    }
}
