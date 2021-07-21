package griezma.goos.auctionsniper;

import griezma.goos.auctionsniper.utils.Announcer;

public class AuctionSniper implements AuctionEventListener {

    private Auction auction;
    private SniperSnapshot snapshot;
    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    public void addSniperListener(SniperListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        boolean winning = source == PriceSource.Sniper;
        
        if (winning) {
            snapshot = snapshot.winning(price);
        } else {
            final int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
        }
        notifyChange();
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }
}
