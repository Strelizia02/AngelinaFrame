public enum FunctionType {
    FunctionAdmin("功能管理"),
    MockSearch("模拟寻访"),
    ArknightsData("游戏数据"),
    BiliDynamic("B站推送"),
    Others("其他娱乐");


    String name;

    FunctionType(String name) {
        this.name = name;
    }
}
