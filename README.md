# 项目描述

本项目仅为简化QQBot的开发流程，并非直接提供相关QQ协议，框架内置了[Mirai](https://github.com/mamoe/mirai)框架。

Read [English Readme](https://github.com/Strelizia02/AngelinaFrame/blob/master/Readme-en.md)

项目构建：SpringBoot + QQ框架

<div>
    <img alt="Java" src="https://img.shields.io/badge/Java-1.8-blue?style=plastic">
    <img alt="SQLite" src="https://img.shields.io/badge/SQLite-3.36-pink?logo=SQLite&logoColor=pink&style=plastic">    
    <img alt="SpringBoot" src="https://img.shields.io/badge/SpringBoot-2.4.0-green?logo=Spring Boot&style=plastic">
    <img alt="AngelinaFrame" src="https://img.shields.io/badge/BotFrame-AngelinaFrame-red?style=plastic">
    </br>
    <img alt="platform" src="https://img.shields.io/badge/platform-Windows-blueviolet?logo=Windows&style=plastic">
    <img alt="platform" src="https://img.shields.io/badge/platform-Linux-blueviolet?logo=Linux&style=plastic">
    <img alt="license" src="https://img.shields.io/badge/license-AGPL3.0-green?style=plastic">
    <img alt="IntelliJ IDEA" src="https://img.shields.io/badge/JetBrains-IDEA-red?logo=IntelliJ IDEA&logoColor=white&style=plastic"
</div>

使用样例：

```java
@Service//由Spring容器对实例进行托管
public class HelloWorldService {
    
    @AngelinaGroup(keyWords = {"Hello", "hello"})//当QQ群聊中呼叫【洁哥Hello】或【洁哥hello】时，调用该方法
    public ReplayInfo helloWorld(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo();
        replayInfo.setReplayMessage("Angelina Hello World！");
        return replayInfo;//将你的返回结果封装成ReplayInfo
    }
}
```

Angelina框架对SpringBoot进行魔改，通过反射调用消息处理方法。开发者仅需处理业务逻辑，无需在意如何监听消息/发送消息，仅需按照方法规定输入输出，再加上@Angelina注解，由Angelina框架部分完成消息接收和发送等操作。


# 责任声明

本项目完全开源，开源许可采用AGPLv3，详情请参见[LICENSE](https://github.com/Strelizia02/AngelinaFrame/blob/master/LICENSE)

本项目仅用于学习交流。

请勿使用本项目从事任何违反法律法规的活动。

项目中包含部分需要将密码明文硬编码的行为，对qq账号有一定安全风险，请勿使用高价值的qq号进行调试。

# 如何使用

[开发者手册](https://github.com/Strelizia02/AngelinaFrame/wiki)

# 项目案例

[BotByAngelinaFrame](https://github.com/Strelizia02/AngelinaBot)
