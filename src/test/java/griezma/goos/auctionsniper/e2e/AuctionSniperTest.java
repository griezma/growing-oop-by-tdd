package griezma.goos.auctionsniper.e2e;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import griezma.goos.auctionsniper.Auction;
import griezma.goos.auctionsniper.AuctionSniper;
import griezma.goos.auctionsniper.SniperListener;

public class AuctionSniperTest {
    private final Auction auction = mock(Auction.class);
    private final SniperListener listener = mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(auction, listener);

    @Test
    public void reportsLostWhenAutionCloses() {
        sniper.auctionClosed();
        
        verify(listener, times(1)).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenPriceArrives() {
        final int price = 10;
        final int increment = 7;

        sniper.currentPrice(price, increment);
        
        verify(auction, times(1)).bid(price + increment);
        verify(listener).sniperBidding();
    }
}
