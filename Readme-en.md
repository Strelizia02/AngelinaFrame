# Project Description

This project only simplifies the development process of QQBot, and does not directly provide the relevant QQ protocol. The framework has a built-in [Mirai](https://github.com/mamoe/mirai) version and integrates the [Mirai](https://github.com/mamoe/mirai) framework, if you are using other frameworks for development, please read [How to encapsulate other QQ frameworks]()

Project construction: SpringBoot + QQ framework

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

[Developer's Manual]()

# Project Cases

[BotByAngelinaFrame]()
