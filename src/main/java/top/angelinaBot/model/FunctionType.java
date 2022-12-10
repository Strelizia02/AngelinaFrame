package top.angelinaBot.model;

public enum FunctionType {
    FunctionAdmin("功能管理"),
    MockSearch("模拟寻访"),
    ArknightsData("游戏数据"),
    BiliDynamic("B站推送"),
    Others("其他娱乐"),

    Friend("私聊菜单"),

    Event("事件菜单");


    String name;

    FunctionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
