package top.angelinaBot.dao;

public interface ActivityMapper {

    Integer getGroupMessage();

    Integer getFriendMessage();

    Integer getEventMessage();

    Integer sendMessage();

    void initActivityTable();

    void clearActivity();
}
