package mani.kata.auctionsnipe;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class FakeAuctionServer {
    private static final String XMPP_HOSTNAME = "localhost";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_PASSWORD = "auction";
    private static final String AUCTION_RESOURCE = "Auction";


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
            currentChat = chat;
            chat.addMessageListener(messageListener);
        });
    }

    public String getItemId() {
        return itemId;
    }

    public void hasReceivedJoinRequest() throws InterruptedException {
        messageListener.receivesAMessage(is(anything()));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(
                String.format("SOLVersion: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s",
                        price, increment, bidder));
    }

    public void hasReceivedBid(int bidPrice, String bidderId) throws InterruptedException {
        assertThat(currentChat.getParticipant(), equalTo(bidderId));

        messageListener.receivesAMessage(allOf(
                containsString("Command: BID;"),
                containsString("Price: " + bidPrice + ';')
        ));
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> matcher) throws InterruptedException {
        messageListener.receivesAMessage(matcher);
        assertThat(currentChat.getParticipant(), startsWith(sniperId));
    }
}

class SimpleMessageListener implements MessageListener {
    private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

    @Override
    public void processMessage(Chat chat, Message message) {
        messages.add(message);
    }

    public void receivesAMessage(Matcher<? super String> matcher) throws InterruptedException {
        final Message message = messages.poll(2, TimeUnit.SECONDS);
        assertThat("Message", message, is(notNullValue()));
        assertThat(message.getBody(), matcher);
    }
}
