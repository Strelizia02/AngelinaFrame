package top.angelinaBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaEvent;
import top.angelinaBot.annotation.AngelinaFriend;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.container.AngelinaContainer;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

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
        List<String> funcStrList = getFuncStrList();
        String title = botNames[0] + "菜单";
        int width = Math.max(getStringListWidth(funcStrList), title.length()) * 50 + 50;
        int height = funcStrList.size() * 75 + 100;
        BufferedImage back = new BufferedImage(width + 40, height + 40, BufferedImage.TYPE_INT_RGB);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(208, 145, 122, 205)); // 先用白色填充整张图片,也就是背景
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("宋体", Font.BOLD, 50));
        int gHeight = 75;
        g.drawString(title, width/2 - title.length() * 25, gHeight);
        gHeight += 75;
        for (String s: funcStrList) {
            g.drawString(s, 0, gHeight);
            gHeight += 75;
        }
        g.dispose();

        Graphics graphics = back.getGraphics();
        graphics.setColor(new Color(255, 156, 118, 255)); // 先用白色填充整张图片,也就是背景
        graphics.fillRect(0, 0, width + 40, height + 40);
        graphics.drawImage(image, 20, 20, null);
        graphics.dispose();
        replayInfo.setReplayImg(back);
        return replayInfo;
    }

    /**
     * 根据AngelinaContainer中的方法，生成一个字符串集合
     * @return 需要生成图片的字符串集合
     */
    private List<String> getFuncStrList() {
        List<String> strList = new ArrayList<>();
        strList.add("群聊菜单：");

        Set<Method> group = new HashSet<>(AngelinaContainer.groupMap.values());
        Set<Method> friend = new HashSet<>(AngelinaContainer.friendMap.values());
        Set<Method> event = new HashSet<>(AngelinaContainer.eventMap.values());

        for(Method method: group) {
            AngelinaGroup annotation = method.getAnnotation(AngelinaGroup.class);
            strList.add(annotation.keyWords()[0]);
            if (annotation.description().equals("")) {
                strList.add("  " + Arrays.toString(annotation.keyWords()));
            } else {
                strList.add("  " + annotation.description());
            }
        }
        strList.add("私聊菜单：");
        for(Method method: friend) {
            AngelinaFriend annotation = method.getAnnotation(AngelinaFriend.class);
            strList.add(annotation.keyWords()[0]);
            if (annotation.description().equals("")) {
                strList.add("  " + Arrays.toString(annotation.keyWords()));
            } else {
                strList.add("  " + annotation.description());
            }
        }

        strList.add("事件菜单：");
        for(Method method: event) {
            AngelinaEvent annotation = method.getAnnotation(AngelinaEvent.class);
            strList.add(annotation.event().toString());
            if (annotation.description().equals("")) {
                strList.add("  " + annotation.event() + "的方法");
            } else {
                strList.add("  " + annotation.description());
            }
        }
        return strList;
    }

    /**
     * 获取一个字符串集合的最大字符串长度
     */
    private int getStringListWidth(List<String> strings) {
        int width = 0;
        for (String s: strings) {
            if (width < s.length()) {
                width = s.length();
            }
        }
        return width;
    }
}
