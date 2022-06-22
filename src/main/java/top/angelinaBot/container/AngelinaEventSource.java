package top.angelinaBot.container;

import top.angelinaBot.model.AngelinaMessageEvent;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

import java.util.HashMap;
import java.util.Map;


public class AngelinaEventSource {


    private static volatile AngelinaEventSource instance;

    private AngelinaEventSource(){}

    public static AngelinaEventSource getInstance() {
        if (instance == null) {
            synchronized (AngelinaEventSource.class) {
                if (instance == null) {
                    instance = new AngelinaEventSource();
                }
            }
        }
        return instance;
    }

    public Map<AngelinaListener, AngelinaMessageEvent> listenerSet = new HashMap<>();

    public static AngelinaMessageEvent waiter(AngelinaListener listener) {
        AngelinaMessageEvent o = new AngelinaMessageEvent();
        AngelinaEventSource.getInstance().registerEventListener(listener, o);
        synchronized (o.getLock()) {
            System.out.println("线程等待");
            try {
                o.getLock().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return o;
    }

    public void registerEventListener(AngelinaListener listener, AngelinaMessageEvent o) {
        if (listener != null) {
            listenerSet.put(listener, o);
        }
    }

    public void handle(MessageInfo message) {
        for (AngelinaListener l: listenerSet.keySet()) {
            if (l.callback(message)) {
                AngelinaMessageEvent event = listenerSet.get(l);
                synchronized (event.getLock()) {
                    System.out.println("唤起线程");
                    event.setMessageInfo(message);
                    event.getLock().notify();
                }
                listenerSet.remove(l);
            }
        }
    }

}


