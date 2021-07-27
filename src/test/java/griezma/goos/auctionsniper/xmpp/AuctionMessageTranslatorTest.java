package griezma.goos.auctionsniper.xmpp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import griezma.goos.auctionsniper.auction.AuctionEventListener;
import griezma.goos.auctionsniper.auction.AuctionEventListener.PriceSource;

public class AuctionMessageTranslatorTest {
    private static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniper";

    private AuctionEventListener eventListener = mock(AuctionEventListener.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, eventListener);
    
    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {

        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);
        verify(eventListener, times(1)).auctionClosed();

    }

    @Test
    public void notifiesBiddingWhenBidMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 10; Increment: 8; Bidder: other party;");

        translator.processMessage(UNUSED_CHAT, message);
        verify(eventListener, times(1)).currentPrice(10, 8, PriceSource.OtherBidder);
    }

    @Test
    public void notifiesBiddingWhenBidMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 10; Increment: 8; Bidder: " + SNIPER_ID + ";");

        translator.processMessage(UNUSED_CHAT, message);
        verify(eventListener, times(1)).currentPrice(10, 8, PriceSource.Sniper);
    }
}