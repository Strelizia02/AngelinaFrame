package top.angelinaBot.bean;

import io.socket.client.IO;

import io.socket.client.Socket;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * WebSocket接口，作为客户端接收QQ框架的消息
 **/
@Configuration
public class WebSocketConfig {

    /**
     * WebSocketListener，实现具体监听方法
     */
    public static void webSocketListener() {
        Socket socket = null;
        try {
            socket = IO.socket("url");
            socket.on("OnGroupMsgs", args -> {
            }).on("OnFriendMsgs", args -> {
            }).on("OnEvents", args -> {
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            socket.close();
        }
    }

}