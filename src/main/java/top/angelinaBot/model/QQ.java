package top.angelinaBot.model;

import lombok.Data;

/**
 * QQ的字段封装
 */
@Data
public class QQ {

    private String qq;

    private String frame;

    private String type;

    private Boolean isOnline;

    public QQ(String qq, String frame, String type, Boolean isOnline) {
        this.qq = qq;
        this.frame = frame;
        this.type = type;
        this.isOnline = isOnline;
    }
}
