package griezma.goos.auctionsniper.e2e;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import griezma.goos.auctionsniper.Auction;
import griezma.goos.auctionsniper.AuctionSniper;
import griezma.goos.auctionsniper.SniperListener;
import griezma.goos.auctionsniper.AuctionEventListener.PriceSource;

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
    public void bidsHigherAndReportsBiddingWhenPriceArrivesFromOtherBidder() {
        final int price = 10;
        final int increment = 7;

        sniper.currentPrice(price, increment, PriceSource.OtherBidder);
        
        verify(auction, times(1)).bid(price + increment);
        verify(listener).sniperBidding();
    }

    @Test
    public void reportsWinnningWheCurrentPriceComesFromSniper() {    
        sniper.currentPrice(123, 45, PriceSource.Sniper);

        verify(auction, never()).bid(anyInt());
        verify(listener).sniperWinning();
    } 

    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(listener).sniperLost();
    }

    @Test
    public void reportsLostWhenAuctionClosesWhileBidding() {
        sniper.currentPrice(10, 7, PriceSource.OtherBidder);

        sniper.auctionClosed();

        verify(listener).sniperLost();        
    }
}
