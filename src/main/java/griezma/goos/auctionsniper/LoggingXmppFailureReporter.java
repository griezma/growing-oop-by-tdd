package griezma.goos.auctionsniper;

import java.util.logging.Logger;

import griezma.goos.auctionsniper.xmpp.XmppFailureReporter;

public class LoggingXmppFailureReporter implements XmppFailureReporter {
    private Logger logger;

    public LoggingXmppFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.severe(String.format("<%s> Could not translate message \"%s\" because %s", auctionId, failedMessage, exception.toString()));
    }
}
