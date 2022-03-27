package top.angelinaBot.model;

import java.util.List;

public class MessageInfo {
    private String keyword;
    private List<String> args;
    private Long qq;
    private String name;
    private Long groupId;
    private List<String> imgUrlList;
    private String fileMd5;
    private Boolean isCallMe;
    private List<Long> atQQList;
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
