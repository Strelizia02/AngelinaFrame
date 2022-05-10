package top.angelinaBot.model;

/**
 * 事件触发的枚举类，当前只允许自动触发这几个事件
 */
public enum EventEnum {
    GroupRecall("群撤回消息"),
    NudgeEvent("戳一戳"),
    MemberJoinEvent("有新成员入群"),
    MemberLeaveEvent("有成员退群");

    private final String eventName;

    EventEnum(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return eventName;
    }

    public String getEventName() {
        return eventName;
    }
}
