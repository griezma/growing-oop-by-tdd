package griezma.goos.auctionsniper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

public class AuctionMessageTranslatorTest {
    private static final Chat UNUSED_CHAT = null;

    private AuctionEventListener eventListener = mock(AuctionEventListener.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(eventListener);
    
    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {

        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
        verify(eventListener, times(1)).auctionClosed();

    }

    @Test
    public void notifiesBiddingWhenBidMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 10; Increment: 8; Bidder: other party;");

        translator.processMessage(UNUSED_CHAT, message);
        verify(eventListener, times(1)).currentPrice(10, 8);
    }
}