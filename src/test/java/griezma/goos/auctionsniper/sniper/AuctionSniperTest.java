package griezma.goos.auctionsniper.sniper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import griezma.goos.auctionsniper.auction.Auction;
import griezma.goos.auctionsniper.auction.AuctionEventListener.PriceSource;


public class AuctionSniperTest {
    private static final String ITEM_ID = "item-54321";
    
    private final Auction auction = mock(Auction.class);
    private final SniperListener listener = mock(SniperListener.class);

    @Test
    public void reportsLostWhenAutionCloses() {
        sniperWithStopPrice(null).auctionClosed();
        
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, SniperState.LOST));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenPriceArrives() {
        final int price = 10;
        final int increment = 7;
        final int bid = price + increment;

        sniperWithStopPrice(null).currentPrice(price, increment, PriceSource.OtherBidder);
        
        verify(auction, times(1)).bid(bid);
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
    }

    @Test
    public void reportsWinnningWhenCurrentPriceComesFromSniper() {
        final AuctionSniper sniper = sniperWithStopPrice(null);
        sniper.currentPrice(123, 12, PriceSource.OtherBidder);
        sniper.currentPrice(135, 45, PriceSource.Sniper);

        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAboveStopPrice() {
        final int stopPrice = 1000;
        final AuctionSniper sniper = sniperWithStopPrice(stopPrice);

        sniper.currentPrice(123, 45, PriceSource.OtherBidder);
        verify(listener).sniperStateChanged(withSniperState(SniperState.BIDDING));

        sniper.currentPrice(2345, 25, PriceSource.OtherBidder);
        verify(listener).sniperStateChanged(withSniperState(SniperState.LOSING));
    }

    @Test
    public void reportsLostIfAuctionClosesWhileLosing() {
        final int stopPrice = 1000;
        final AuctionSniper sniper = sniperWithStopPrice(stopPrice);

        final int firstPrice = 123;
        final int firstIncrement = 45;
        sniper.currentPrice(firstPrice, firstIncrement, PriceSource.OtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.OtherBidder);
        verify(listener).sniperStateChanged(argThat(snapshot -> snapshot.sniperState == SniperState.LOSING));

        sniper.auctionClosed();
        verify(listener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, firstPrice + firstIncrement, SniperState.LOST));
    }

    @Test
    public void reportsFailedAfterInvalidMessage() {
        final AuctionSniper sniper = sniperWithStopPrice(null);

        sniper.currentPrice(500, 20, PriceSource.Sniper);
        verify(listener).sniperStateChanged(withSniperState(SniperState.WINNING));

        sniper.auctionFailed();
        verify(listener).sniperStateChanged(withSniperState(SniperState.FAILED));
    }

    private AuctionSniper sniperWithStopPrice(Integer stopPriceArg) {
        final int stopPrice = stopPriceArg != null ? stopPriceArg : Integer.MAX_VALUE;
        final AuctionSniper sniper = new AuctionSniper(new Item(ITEM_ID, stopPrice), auction);
        sniper.addSniperListener(listener);
        return sniper;
    }

    private SniperSnapshot withSniperState(SniperState sniperState) {
        return argThat(snapshot -> snapshot.sniperState == sniperState);
    }
}
