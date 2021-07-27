package griezma.goos.auctionsniper.xmpp;

public interface XmppFailureReporter {
    void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}
