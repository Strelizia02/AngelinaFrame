package top.angelinaBot.model;

public class AngelinaMessageEvent {
    private Object lock = new Object();
    private ReplayInfo replay;

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public ReplayInfo getReplay() {
        return replay;
    }

    public void setReplay(ReplayInfo replay) {
        this.replay = replay;
    }
}
