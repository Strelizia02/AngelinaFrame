package top.angelinaBot.container;

import org.springframework.stereotype.Component;
import top.angelinaBot.util.AngelinaSendMessageUtil;

import java.util.HashMap;
import java.util.Map;

@Component
public class QQFrameContainer {

    final static public String Miari = "mirai";

    final static public String QQChannel = "qqChannel";

    final static public String Gocq = "gocq";

    final static public String Oicq = "oicq";

    final public Map<String, AngelinaSendMessageUtil> qqFrameMap = new HashMap<>();

}
