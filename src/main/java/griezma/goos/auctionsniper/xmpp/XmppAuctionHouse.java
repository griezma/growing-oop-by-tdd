package griezma.goos.auctionsniper.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import griezma.goos.auctionsniper.LoggingXmppFailureReporter;
import griezma.goos.auctionsniper.auction.Auction;
import griezma.goos.auctionsniper.auction.AuctionHouse;

public class XmppAuctionHouse implements AuctionHouse {
    private static final String LOGGER_NAME = "AuctionSniper";
    private static final String LOG_FILE_NAME = "auction-sniper.log";

    private static final String AUCTION_RESOURCE = "Auction";

    private final XMPPConnection connection;
    private final XmppFailureReporter failureReporter;

    public static XmppAuctionHouse connect(String host, String user, String password) {
        try {
            XMPPConnection connection = new XMPPConnection(host);
            connection.connect();
            connection.login(user, password, AUCTION_RESOURCE);
            return new XmppAuctionHouse(connection);
        } catch (XMPPException e) {
            throw new RuntimeException("XMPP connection error", e);
        }
    }

    private XmppAuctionHouse(XMPPConnection connection) {
        failureReporter = new LoggingXmppFailureReporter(makeLogger());
        this.connection = connection;
    }

    @Override
    public Auction auctionFor(String itemId) {
        return new XmppAuction(this.connection, itemId, failureReporter);
    }

    @Override
    public void disconnect() {
        this.connection.disconnect();
    }
    
    private static Logger makeLogger() {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private static Handler simpleFileHandler() {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (Exception e) {
            throw new RuntimeException("Could not create logger", e);
        }
    }
}
