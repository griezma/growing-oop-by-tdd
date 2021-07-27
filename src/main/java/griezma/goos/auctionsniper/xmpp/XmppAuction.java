package griezma.goos.auctionsniper.xmpp;

import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import griezma.goos.auctionsniper.auction.Auction;
import griezma.goos.auctionsniper.auction.AuctionEventListener;
import griezma.goos.auctionsniper.utils.Announcer;

public class XmppAuction implements Auction {
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d";
    
    private static final Logger log = Logger.getLogger("XmppAuction"); 

    private final Chat chat;
    private final Announcer<AuctionEventListener> eventListeners = Announcer.to(AuctionEventListener.class);

    public XmppAuction(XMPPConnection connection, String itemId, XmppFailureReporter failureReporter) {
        AuctionMessageTranslator messageTranslator = translatorFor(connection, failureReporter);
        final String auctionId = auctionId(itemId, connection);
        chat = connection.getChatManager().createChat(auctionId, messageTranslator);
        
        addAuctionEventListener(chatDisconnnectOnFailure(messageTranslator));

        log.info(String.format("Chat created: user=%s, participant=%s", connection.getUser(), chat.getParticipant()));
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        log.info("addAuctionEventListener");
        eventListeners.addListener(listener);    
    }

    @Override
    public void join() {
        log.info("join");
        try {
            chat.sendMessage(JOIN_COMMAND_FORMAT);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bid(int amount) {
        log.info("bid: " + amount);
        try {
            chat.sendMessage(String.format(BID_COMMAND_FORMAT, amount));
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection, XmppFailureReporter failureReporter) {
        return new AuctionMessageTranslator(connection.getUser(), eventListeners.announce(), failureReporter);
    }

    private AuctionEventListener chatDisconnnectOnFailure(AuctionMessageTranslator translator) {
        return new AuctionEventListener() {

            @Override
            public void currentPrice(int price, int increment, PriceSource source) {
            }
            @Override
            public void auctionClosed() {
            }
            @Override
            public void auctionFailed() {
               chat.removeMessageListener(translator);
            }
        };
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
