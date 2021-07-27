package griezma.goos.auctionsniper.auction;

public interface AuctionEventListener {
    enum PriceSource {
        Sniper,
        OtherBidder
    }

    void currentPrice(int price, int increment, PriceSource source);
    void auctionClosed();
    void auctionFailed();
}
