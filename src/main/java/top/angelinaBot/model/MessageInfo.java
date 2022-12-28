package top.angelinaBot.model;

import net.mamoe.mirai.message.data.ImageType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * qq消息格式化Bean
 **/
public class MessageInfo {
    /**
     * 登录qq
    */
    private String loginQq;
    /**
     * 原文字消息
    */
    private String text;
    /**
     * 文字消息的第一节
    */
    private String keyword;
    /**
     * 文字消息的参数
    */
    private List<String> args = new ArrayList<>();
    /**
     * 发送人qq
    */
    private String qq;
    /**
     * 发送人昵称
    */
    private String name;
    /**
     * 群号
    */
    private String groupId;
    /**
     * 消息唯一编码
     */
    private String messageId;
    /**
     * 图片Url合集
    */
    private List<String> imgUrlList = new ArrayList<>();
    /**
     * 图片类型合集
    */
    private List<ImageType> imgTypeList = new ArrayList<>();
    /**
     * 是否被呼叫
    */
    private Boolean isCallMe = false;
    /**
     * 艾特了哪些人
    */
    private List<String> atQQList = new ArrayList<>();
    /**
     * 发送时间戳
    */
    private Integer time;
    /**
     * 消息缩略字符串
    */
    private String eventString;
    /**
     * 接收到的事件
    */
    private EventEnum event;
    /**
     * 是否要发送消息
    */
    private Boolean isReplay = true;
    /**
     * 用户所拥有的最高权限
    */
    private PermissionEnum  userAdmin;
    /**
     * 所用框架
     */
    String frame;

    public MessageInfo() {

    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public boolean isReplay() {
        return isReplay;
    }

    public void setReplay(boolean replay) {
        isReplay = replay;
    }

    public EventEnum getEvent() {
        return event;
    }

    public void setEvent(EventEnum event) {
        this.event = event;
    }

    public List<ImageType> getImgTypeList() {
        return imgTypeList;
    }

    public void setImgTypeList(List<ImageType> imgTypeList) {
        this.imgTypeList = imgTypeList;
    }

    public String getEventString() {
        return eventString;
    }

    public void setEventString(String eventString) {
        this.eventString = eventString;
    }

    public String getLoginQq() {
        return loginQq;
    }

    public void setLoginQq(String loginQq) {
        this.loginQq = loginQq;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getCallMe() {
        return isCallMe;
    }

    public void setCallMe(Boolean callMe) {
        isCallMe = callMe;
    }

    public List<String> getAtQQList() {
        return atQQList;
    }

    public void setAtQQList(List<String> atQQList) {
        this.atQQList = atQQList;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Boolean getReplay() {
        return isReplay;
    }

    public PermissionEnum getUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(PermissionEnum userAdmin) {
        if (this.userAdmin != null && userAdmin.getLevel() > this.userAdmin.getLevel()) {
            this.userAdmin = userAdmin;
        } else if (this.userAdmin == null) {
            this.userAdmin = userAdmin;
        }
    }

    public void setReplay(Boolean replay) {
        isReplay = replay;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
