package top.angelinaBot.model;

import java.util.List;

/**
 * @author strelitzia
 * @Date 2022/04/03
 * qq消息格式化Bean
 **/
public class MessageInfo {
    //文字消息的第一节
    private String keyword;
    //文字消息的参数
    private List<String> args;
    //发送人qq
    private Long qq;
    //发送人昵称
    private String name;
    //群号
    private Long groupId;
    //图片url集合
    private List<String> imgUrlList;
    //fileMd5消息
    private String fileMd5;
    //是否被呼叫
    private Boolean isCallMe;
    //艾特了哪些人
    private List<Long> atQQList;
    //发送时间
    private String time;

    public MessageInfo() {}

    public MessageInfo(Long qq, Long groupId, String name, String text){
        //TODO 解析json字符串
        this.qq = qq;
        this.groupId = groupId;
        this.name = name;
        this.keyword = text;
        this.isCallMe = true;
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

    public Long getQq() {
        return qq;
    }

    public void setQq(Long qq) {
        this.qq = qq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Boolean getCallMe() {
        return isCallMe;
    }

    public void setCallMe(Boolean callMe) {
        isCallMe = callMe;
    }

    public List<Long> getAtQQList() {
        return atQQList;
    }

    public void setAtQQList(List<Long> atQQList) {
        this.atQQList = atQQList;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
