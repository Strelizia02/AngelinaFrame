package top.angelinaBot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.container.AngelinaEventSource;
import top.angelinaBot.container.AngelinaListener;
import top.angelinaBot.model.*;
import top.angelinaBot.util.SendMessageUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

@Service
public class FunctionListService {

    @Autowired
    private SendMessageUtil sendMessageUtil;

    //bot的呼叫关键字
    @Value("#{'${userConfig.botNames}'.split(' ')}")
    public String[] botNames;

    @AngelinaGroup(keyWords = {"菜单", "功能", "会什么"}, description = "全部功能列表")
    public ReplayInfo getFunctionList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        //优先用替换文件
        File png = new File("runFile/functionList.png");
        File jpg = new File("runFile/functionList.jpg");
        if (png.exists()) {
            replayInfo.setReplayImg(png);
        } else if (jpg.exists()) {
            replayInfo.setReplayImg(jpg);
        } else {
            replayInfo.setReplayImg(getFuncStrList());
        }

        String type;
        //判断有无参数
        if (messageInfo.getArgs().size() > 1) {
            type = messageInfo.getArgs().get(1);
        } else {
            TextLine textLine = new TextLine();
            textLine.addCenterStringLine(botNames[0] + "菜单");
            textLine.nextLine();
            textLine.addString("[1]功能管理");
            textLine.nextLine();
            textLine.addString("[2]模拟寻访");
            textLine.nextLine();
            textLine.addString("[3]游戏数据");
            textLine.nextLine();
            textLine.addString("[4]B站推送");
            textLine.nextLine();
            textLine.addString("[5]其他娱乐");
            textLine.nextLine();
            textLine.addString("[6]全部菜单");
            textLine.nextLine();
            textLine.addString("请于十秒内发送编号查看菜单，超时或者发送其他信息则返回全部菜单");



            AngelinaListener angelinaListener = new AngelinaListener() {
                @Override
                public boolean callback(MessageInfo message) {
                    return message.getGroupId().equals(messageInfo.getGroupId()) &&
                            (message.getText().equals("1") || message.getText().equals("2") ||
                                    message.getText().equals("3") || message.getText().equals("4") ||
                                    message.getText().equals("5") || message.getText().equals("6"));
                }
            };
            angelinaListener.setGroupId(messageInfo.getGroupId());
            angelinaListener.setSecond(10);
            MessageInfo recall = AngelinaEventSource.waiter(angelinaListener).getMessageInfo();
            type = recall.getText();
        }

        FunctionType functionType = null;
        switch (type) {
            case "1":
            case "功能管理":
                functionType = FunctionType.FunctionAdmin;
                break;
            case "2":
            case "模拟寻访":
                functionType = FunctionType.MockSearch;
                break;
            case "3":
            case "游戏数据":
                functionType = FunctionType.ArknightsData;
                break;
            case "4":
            case "B站推送":
                functionType = FunctionType.BiliDynamic;
                break;
            case "5":
            case "其他娱乐":
                functionType = FunctionType.Others;
                break;
            default:
                break;
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
