package net.ddns.pzshare;

public class TestMsgSender implements MsgSender {
    private Boolean riseException = false;

    @Override
    public void send(Long userId, String msg) throws SendException {
        if (riseException)
            throw new SendException("Test exception");
    }

    void riseException(Boolean riseEx) {
        riseException = riseEx;
    }
}
