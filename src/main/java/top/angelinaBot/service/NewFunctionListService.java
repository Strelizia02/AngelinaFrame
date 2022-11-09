package top.angelinaBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.model.TextLine;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Service
public class FunctionListService {

    //bot的呼叫关键字
    @Value("#{'${userConfig.botNames}'.split(' ')}")
    public String[] botNames;

    //功能块表
    private static final Map<String, Set<Method>> funcMap = new HashMap<>();

    //菜单类别与功能块对应表
    private static final Map<String, Set<String>> sortMap = new HashMap<>();
    private static final Map<String, Set<String>> unDefineMap = new HashMap<>();

    @AngelinaGroup(keyWords = {"新菜单"}, description = "bot功能菜单")
    public ReplayInfo getFunctionList(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        if (funcMap.isEmpty()){
            packFunc();
        }
        if (messageInfo.getArgs().size()>1){
            switch (messageInfo.getArgs().get(1)){
                case "私聊菜单" : replayInfo.setReplayImg(getFriendFuncList()); break;
                case "事件菜单" : replayInfo.setReplayImg(getEventFuncList()); break;
                case "群聊菜单" : replayInfo.setReplayMessage("不可指定！！！");break;
                default: if(!sortMap.containsKey(messageInfo.getArgs().get(1)) && !unDefineMap.containsKey(messageInfo.getArgs().get(1))){
                    replayInfo.setReplayMessage("未能找到指定的菜单，请重新指定");
                }else {
                    replayInfo.setReplayImg(getGroupFuncList(messageInfo.getArgs().get(1)));
                }break;

            }
        }else {
            replayInfo.setReplayImg(getFuncList());
        }

        return replayInfo;
    }

    //用于实现表格的初始化
    public void packFunc(){
        Set<Method> group = new HashSet<>(AngelinaContainer.groupMap.values());
        for(Method method: group) {
            AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);

            //初始化功能块表
            if (!annotation.funcClass().equals("null")) {
                Set<Method> funcSet;
                if(!funcMap.containsKey(annotation.funcClass())){
                    funcSet = new HashSet<>();
                }else {
                    funcSet = funcMap.get(annotation.funcClass());
                }
                funcSet.add(method);
                funcMap.put(annotation.funcClass(),funcSet);
            }

            //初始化菜单类别与功能块对应表
            Set<String> sortSet;
            if (annotation.funcClass().equals("null")){
                if(!unDefineMap.containsKey(annotation.sort())){
                    sortSet = new HashSet<>();
                }else {
                    sortSet = unDefineMap.get(annotation.sort());
                }
                sortSet.add(annotation.keyWords()[0]);
                unDefineMap.put(annotation.sort(),sortSet);
            }else {
                if(!sortMap.containsKey(annotation.sort())){
                    sortSet = new HashSet<>();
                }else {
                    sortSet = sortMap.get(annotation.sort());
                }
                sortSet.add(annotation.funcClass());
                sortMap.put(annotation.sort(),sortSet);
            }
        }

    }

    /**
     * 根据AngelinaContainer中的方法，生成一个字符串集合
     * @return 需要生成图片的字符串集合
     */
    private BufferedImage getFuncList() {
        Set<String> sortSet = new HashSet<>(sortMap.keySet());
        Set<String> unDefineSet = unDefineMap.keySet();
        sortSet.addAll(unDefineSet);
        TextLine textLine = new TextLine();
        textLine.addCenterStringLine(botNames[0] + "功能菜单列表");
        textLine.addString("私聊菜单");
        textLine.nextLine();
        textLine.addString("事件菜单");
        textLine.nextLine();
        textLine.addString("群聊菜单（不可指定）：");
        textLine.nextLine();
        for(String sort : sortSet){
            textLine.addString(sort);
            textLine.nextLine();
        }
        textLine.nextLine();
        textLine.addString("可以添加具体菜单名获取详细菜单");

        return textLine.drawImage(100,false);
    }

    /**
     * 通过对输入列表类别名字来查询属于该类别下的功能块，先查询非未定义，再查询未定义
     * 当类别为未定义时，不再从功能块表中查询，转为到未定义表中查询
     * 当类别非未定义时，对功能块增加额外的两个换行
     * 对得到的功能块，需要再次到功能块表中去得到表中对应的功能分别展示
     * @param sortName 输入的列表类别名字
     * @return 最终的功能列表制图
     */
    private BufferedImage getGroupFuncList(String sortName) {
        TextLine textLine = new TextLine();
        textLine.addCenterStringLine(botNames[0] + sortName);
        Set<String> sortSet = sortMap.get(sortName);
        if (sortSet != null) {
            for (String className : sortSet) {
                textLine.addString(className);
                textLine.addString("：");
                textLine.nextLine();
                for (Method method : funcMap.get(className)) {
                    AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);
                    textLine.addSpace(2);
                    textLine.addString(annotation.keyWords()[0]);
                    textLine.nextLine();
                    if (!annotation.description().equals("")) {
                        textLine.addSpace(4);
                        textLine.addString(annotation.description());
                    }
                    textLine.nextLine();
                }
            }
        }
        Set<String> unDefineSet = unDefineMap.get(sortName);
        if (unDefineSet != null) {
            for (String className : unDefineSet) {
                Method method = AngelinaContainer.groupMap.get(className);
                AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);
                textLine.addString(annotation.keyWords()[0]);
                textLine.nextLine();
                if (!annotation.description().equals("")) {
                    textLine.addSpace(2);
                    textLine.addString(annotation.description());
                }
                textLine.nextLine();
            }
        }
        return textLine.drawImage(100,false);
    }



    private BufferedImage getFriendFuncList() {
        TextLine textLine = new TextLine();
        textLine.addCenterStringLine(botNames[0] + "私聊菜单");
        Set<Method> friend = new HashSet<>(AngelinaContainer.friendMap.values());
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

        return textLine.drawImage(100,false);
    }



    private BufferedImage getEventFuncList() {
        TextLine textLine = new TextLine();
        textLine.addCenterStringLine(botNames[0] + "事件菜单");
        Set<Method> event = new HashSet<>(AngelinaContainer.eventMap.values());
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
        return textLine.drawImage(100,false);
    }


}
