package griezma.goos.auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import griezma.goos.auctionsniper.Auction;
import griezma.goos.auctionsniper.AuctionHouse;

public class XmppAuctionHouse implements AuctionHouse {
    private static final String AUCTION_RESOURCE = "Auction";

    private final XMPPConnection connection;

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
        this.connection = connection;
    }
    
    @Override
    public Auction auctionFor(String itemId) {
        return new XmppAuction(this.connection, itemId);
    }

    @Override
    public void disconnect() {
        this.connection.disconnect();
    }


}
