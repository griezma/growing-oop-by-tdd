package griezma.goos.auctionsniper.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import griezma.goos.auctionsniper.auction.AuctionEventListener;

public class AnnouncerTest {
    
    private AuctionEventListener listener = mock(AuctionEventListener.class);

    @Test
    public void announcesToOneListener() {
        Announcer<AuctionEventListener> announcer = Announcer.to(AuctionEventListener.class);
        AuctionEventListener proxy = announcer.announce();
        announcer.addListener(listener);

        proxy.auctionClosed();

        verify(listener).auctionClosed();
    }
}
