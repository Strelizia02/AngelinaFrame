# 项目描述

本项目仅为简化QQBot的开发流程，并非直接提供相关QQ协议，框架内置了[mirai]()，[opq]()两个版本，分别集成了mirai和opq框架，如果您是使用其他框架进行开发，请详阅[如何封装其他QQ框架]()

项目构建：SpringBoot + 任意QQ框架

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

本项目完全开源，开源许可请参见[LICENSE]()

# 责任声明

本项目旨在学习交流，请勿商用。

请勿使用本项目从事任何违反法律法规的活动。

项目中包含部分需要将密码明文硬编码的行为，对qq账号有一定安全风险，请勿使用高价值的qq号进行调试。

# 如何使用

[开发者手册]()

# 项目案例

[BotByAngelinaFrame]()
