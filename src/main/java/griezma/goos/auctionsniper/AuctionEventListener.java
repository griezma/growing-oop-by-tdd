package griezma.goos.auctionsniper;

public interface AuctionEventListener {
    void auctionClosed();

    void currentPrice(int price, int increment);
}