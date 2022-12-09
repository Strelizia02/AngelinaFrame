package top.angelinaBot.model;

public enum PermissionEnum {
    Administrator("超级管理员", 3),
    GroupMaster("群主", 2),
    GroupAdministrator("群管理", 1),
    GroupUser("普通成员", 0);

    private String name;
    private int level;


    PermissionEnum(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
