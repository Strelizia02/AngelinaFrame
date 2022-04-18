package top.angelinaBot.model;

/**
 * 活跃度模型
 */
public class ActivityInfo {

    /**
     * 0 -> 接收群聊
     * 1 -> 接收私聊
     * 2 -> 接收事件
     * 3 -> 发送消息
     */
    private Integer type;
    private String hour;
    private Long count;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
