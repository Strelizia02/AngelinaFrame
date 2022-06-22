package top.angelinaBot.container;

import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

public interface AngelinaListener {
    boolean callback(MessageInfo message);
}
