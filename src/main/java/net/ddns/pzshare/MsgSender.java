package net.ddns.pzshare;

public interface MsgSender {
    void send(Long userId, String msg) throws SendException;
}
