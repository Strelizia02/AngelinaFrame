package top.angelinaBot.container;

import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

public interface AngelinaListener {
    ReplayInfo callback(MessageInfo message);
}
