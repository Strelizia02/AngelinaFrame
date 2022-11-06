package top.angelinaBot.container;

import org.springframework.beans.factory.annotation.Qualifier;
import top.angelinaBot.util.SendMessageUtil;

import java.util.HashMap;
import java.util.Map;

public class QQFrameContainer {

    final static public String Miari = "mirai";

    final static public String QQChannel = "qqChannel";

    final static public String Gocq = "gocq";

    final static public String Oicq = "oicq";

    @Qualifier(Miari)
    private static SendMessageUtil MiarisendMessageUtil;

    @Qualifier(QQChannel)
    private static SendMessageUtil QQChannelsendMessageUtil;

//    @Qualifier(Gocq)
//    private SendMessageUtil GocqsendMessageUtil;
//
//    @Qualifier(Oicq)
//    private SendMessageUtil OicqsendMessageUtil;

    final static public Map<String, SendMessageUtil> qqFrameMap = new HashMap<>();

    static {
        qqFrameMap.put(Miari, MiarisendMessageUtil);
        qqFrameMap.put(QQChannel, QQChannelsendMessageUtil);
//        qqFrameMap.put(Gocq, GocqsendMessageUtil);
//        qqFrameMap.put(Oicq, OicqsendMessageUtil);
    }
}
