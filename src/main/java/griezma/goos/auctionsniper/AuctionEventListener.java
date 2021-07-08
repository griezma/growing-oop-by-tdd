package griezma.goos.auctionsniper;

public interface AuctionEventListener {
    enum PriceSource {
        Sniper,
        OtherBidder
    }

    void auctionClosed();

    void currentPrice(int price, int increment, PriceSource source);
}
