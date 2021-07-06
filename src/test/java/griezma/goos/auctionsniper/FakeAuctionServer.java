package griezma.goos.auctionsniper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;


public class FakeAuctionServer {
    static final String XMPP_HOSTNAME = "localhost";
    static final String ITEM_ID_AS_LOGIN = "auction-item-%s";
    static final String AUCTION_PASSWORD = "auction";
    static final String AUCTION_RESOURCE = "Auction";

    static final Logger log = Logger.getLogger("FakeAuctionServer");

    private final String itemId;
    private final XMPPConnection connection;
    private volatile Chat currentChat;

    private final SimpleMessageListener messageListener = new SimpleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener((chat, createdLocally) -> {
            log.info("chat created: " + chat.getParticipant());
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public String getItemId() {
        return itemId;
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }
    
    public void stop() {
        connection.disconnect();
    }
    
    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        log.info("reportPrice: " + bidder);
        String message = String.format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;", price, increment, bidder);
        currentChat.sendMessage(message);
    }
    
    public void hasReceivedJoinRequest(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }
 
    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<String> matcher) throws InterruptedException {
        messageListener.receivesAMessage(matcher);
        log.info(String.format("receivesAMessageMatching: sniperId=%s, participant=%s", sniperId, currentChat != null ? currentChat.getParticipant() : null));
        assertThat(currentChat.getParticipant(), startsWith(sniperId));
       
    }
}

class SimpleMessageListener implements MessageListener {
    private static final Logger log = Logger.getLogger("SimpleMessageListener");

    private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

    @Override
    public void processMessage(Chat chat, Message message) {
        log.info(String.format("processMessage: chat=%s, message=%s", chat.getParticipant(), message.getBody()));
        messages.add(message);
    }

    public void receivesAMessage(Matcher<String> matcher) throws InterruptedException {
        final Message message = messages.poll(2, TimeUnit.SECONDS);
        log.info("receivesAMessage: " + message.getBody());
        assertThat("Message", message.getBody(), matcher);
    }
}
