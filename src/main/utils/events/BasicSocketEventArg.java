package utils.events;

import utils.SenderType;

public class BasicSocketEventArg implements SocketEventArguments {

    protected long timeStamp;
    protected EventType replyType;
    protected SenderType senderType;

    public BasicSocketEventArg(SenderType senderType, EventType replyType) {
        this.timeStamp = System.currentTimeMillis();
        this.senderType = senderType;
        this.replyType = replyType;
    }

    /**
     * Returns new copy of {@link ConnectionIdEventArg}
     * and setting the sender as the newSender
     *
     * @param newSender Redirecting object type
     */
    public SocketEventArguments createRedirectedCopy(SenderType newSender) {
        BasicSocketEventArg redirected = (BasicSocketEventArg) this.clone();
        redirected.senderType = newSender;
        return redirected;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public EventType getReplyType() {
        return replyType;
    }

    @Override
    public String toString() {
        return String.format("%s from %s", this.replyType, this.senderType);
    }


    @Override
    public SocketEventArguments clone() {

        BasicSocketEventArg arg = null;
        try {
            arg = (BasicSocketEventArg) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        arg.senderType = this.senderType;
        arg.replyType = this.replyType;
        arg.timeStamp = this.timeStamp;
        return arg;

    }
}
