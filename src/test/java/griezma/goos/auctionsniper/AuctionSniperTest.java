package griezma.goos.auctionsniper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import griezma.goos.auctionsniper.AuctionEventListener.PriceSource;

public class AuctionSniperTest {
    private static final String ITEM_ID = "item-54321";
    
    private final Auction auction = mock(Auction.class);
    private final SniperListener listener = mock(SniperListener.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, listener);

    @Test
    public void reportsLostWhenAutionCloses() {
        sniper.auctionClosed();
        
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenPriceArrives() {
        final int price = 10;
        final int increment = 7;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, PriceSource.OtherBidder);
        
        verify(auction, times(1)).bid(bid);
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
    }

    @Test
    public void reportsWinnningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, PriceSource.OtherBidder);
        sniper.currentPrice(135, 45, PriceSource.Sniper);

        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
    }
}
