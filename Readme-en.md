# Project Description

This project only simplifies the development process of QQBot, and does not directly provide the relevant QQ protocol. The framework has a built-in [Mirai](https://github.com/mamoe/mirai).

阅读 [中文文档](https://github.com/Strelizia02/AngelinaFrame/blob/master/README.md)

Project construction: SpringBoot + QQ framework

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

Example of use:

````java
@Service//The instance is managed by the Spring container
public class HelloWorldService {
    
    @AngelinaGroup(keyWords = {"Hello", "hello"})//When calling [Angelina Hello] or [angelina hello] in the QQ group chat, call this method
    public ReplayInfo helloWorld(MessageInfo messageInfo) {
        ReplayInfo replayInfo = new ReplayInfo();
        replayInfo.setReplayMessage("Angelina Hello World!");
        return replayInfo;//Encapsulate your return result into ReplayInfo
    }
}
````

The Angelina framework makes magic changes to SpringBoot and calls the message processing method through reflection. Developers only need to deal with business logic, and do not need to care about how to monitor/send messages. They only need to input and output according to the method regulations, plus @Angelina annotation, and the Angelina framework partially completes operations such as message reception and transmission.


# Disclaimer

This project is completely open source, and the open source license adopts AGPLv3. For details, please refer to [LICENSE](https://github.com/Strelizia02/AngelinaFrame/blob/master/LICENSE)

This project is only for learning and communication.

Do not use this project to engage in any activities that violate laws and regulations.

The project contains some behaviors that need to hard-code the password in plain text, which has certain security risks to the QQ account. Please do not use the high-value QQ account for debugging.

# How To Use

[Developer's Manual](https://github.com/Strelizia02/AngelinaFrame/wiki)

# Project Cases

[BotByAngelinaFrame](https://github.com/Strelizia02/AngelinaBot)
