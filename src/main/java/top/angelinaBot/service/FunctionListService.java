package top.angelinaBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.model.TextLine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

@Service
public class FunctionListService {

    //bot的呼叫关键字
    @Value("#{'${userConfig.botNames}'.split(' ')}")
    public String[] botNames;

    @AngelinaGroup(keyWords = {"菜单", "功能", "会什么"}, description = "洁哥功能列表")
    public ReplayInfo getFunctionList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        File file = new File("runFile/functionList.png");
        if (file.exists()) {
            replayInfo.setReplayImg(file);
        } else {
            replayInfo.setReplayImg(getFuncStrList());
        }
        return replayInfo;
    }

    /**
     * 根据AngelinaContainer中的方法，生成一个字符串集合
     * @return 需要生成图片的字符串集合
     */
    private BufferedImage getFuncStrList() {
        TextLine textLine = new TextLine();
        textLine.addCenterStringLine(botNames[0] + "菜单");
        textLine.addString("群聊菜单：");
        textLine.nextLine();
        Set<Method> group = new HashSet<>(AngelinaContainer.groupMap.values());
        Set<Method> friend = new HashSet<>(AngelinaContainer.friendMap.values());
        Set<Method> event = new HashSet<>(AngelinaContainer.eventMap.values());

        for(Method method: group) {
            AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);
            textLine.addString(annotation.keyWords()[0]);
            textLine.nextLine();
            if (!annotation.description().equals("")) {
                textLine.addSpace(2);
                textLine.addString(annotation.description());
            }
            textLine.nextLine();
        }
        textLine.addString("私聊菜单：");
        textLine.nextLine();
        for(Method method: friend) {
            AngelinaFriend annotation = method.getAnnotation(AngelinaFriend.class);
            textLine.addString(annotation.keyWords()[0]);
            textLine.nextLine();
            if (!annotation.description().equals("")) {
                textLine.addSpace(2);
                textLine.addString(annotation.description());
            }
            textLine.nextLine();
        }

        textLine.addString("事件菜单：");
        textLine.nextLine();
        for(Method method: event) {
            AngelinaEvent annotation = method.getAnnotation(AngelinaEvent.class);
            textLine.addString(annotation.event().toString());
            textLine.nextLine();
            if (!annotation.description().equals("")) {
                textLine.addSpace(2);
                textLine.addString(annotation.description());
            }
            textLine.nextLine();
        }
        return textLine.drawImage();
    }
}
