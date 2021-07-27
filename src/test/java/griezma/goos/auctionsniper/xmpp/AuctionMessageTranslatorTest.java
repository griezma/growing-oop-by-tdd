package griezma.goos.auctionsniper.xmpp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.runners.util.FailureDetector;

import griezma.goos.auctionsniper.auction.AuctionEventListener;
import griezma.goos.auctionsniper.auction.AuctionEventListener.PriceSource;

public class AuctionMessageTranslatorTest {
    private static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniper";

    private AuctionEventListener eventListener = mock(AuctionEventListener.class);
    private XmppFailureReporter failureReporter = mock(XmppFailureReporter.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, eventListener, failureReporter);
    
    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {

        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(eventListener).auctionClosed();
    }

    @Test
    public void notifiesBiddingWhenBidMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 10; Increment: 8; Bidder: other party;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(eventListener).currentPrice(10, 8, PriceSource.OtherBidder);
    }

    @Test
    public void notifiesBiddingWhenBidMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 10; Increment: 8; Bidder: " + SNIPER_ID + ";");

        translator.processMessage(UNUSED_CHAT, message);

        verify(eventListener).currentPrice(10, 8, PriceSource.Sniper);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() {
        Message badMessage = new Message();
        badMessage.setBody("a bad message");

        translator.processMessage(UNUSED_CHAT, badMessage);

        verify(eventListener).auctionFailed();
        verify(failureReporter).cannotTranslateMessage(eq(SNIPER_ID), eq("a bad message"), any(Exception.class));
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeIsMissing() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; CurrentPrice: 10; Increment: 8; Bidder: " + SNIPER_ID + ";");

        translator.processMessage(UNUSED_CHAT, message);

        verify(eventListener).auctionFailed();
    }
}