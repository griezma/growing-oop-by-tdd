package griezma.goos.auctionsniper.sniper;

import griezma.goos.auctionsniper.auction.Auction;
import griezma.goos.auctionsniper.auction.AuctionEventListener;
import griezma.goos.auctionsniper.utils.Announcer;

public class AuctionSniper implements AuctionEventListener {

    private Auction auction;
    private Item item;
    private SniperSnapshot snapshot;
    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);

    public AuctionSniper(Item item, Auction auction) {
        this.auction = auction;
        this.item = item;
        this.snapshot = SniperSnapshot.joining(item.identifier);
    }

    public void addSniperListener(SniperListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        switch(source) {  
            case Sniper:
            snapshot = snapshot.winning(price);
            break;

            case OtherBidder:
            final int bid = price + increment;
            if (item.allowsBid(bid)) {
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
            } else {
                snapshot = snapshot.losing(price);
            }
            break;
        }
        notifyChange();
    }
    
    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void auctionFailed() {
        snapshot = snapshot.failed();
        notifyChange();
    
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }
}
