package griezma.goos.auctionsniper.sniper;

public class SniperSnapshot {

    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public final SniperState sniperState;
    
    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.sniperState = state;
    }

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }

    public SniperSnapshot losing(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
    }

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, sniperState.whenAuctionClosed());
    }

    public SniperSnapshot failed() {
        return new SniperSnapshot(itemId, 0, 0, SniperState.FAILED);
    }

    public boolean isForSameItemAs(SniperSnapshot other) {
        return other.itemId.equals(itemId);
    }

    @Override
    public int hashCode() {
        return itemId.hashCode() ^ lastPrice ^ lastBid;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SniperSnapshot)) {
            return false;
        }
        final SniperSnapshot otherState = (SniperSnapshot)other;
        return otherState.itemId.equals(itemId) 
            && otherState.lastPrice == lastPrice 
            && otherState.lastBid == lastBid 
            && otherState.sniperState == sniperState;
    }

    @Override
    public String toString() {
        return String.format(
            "[SniperState: itemId=%s, lastPrice=%d, lastBid=%d, state=%s]", 
                itemId, lastPrice, lastBid, sniperState.toString());
    }


}
