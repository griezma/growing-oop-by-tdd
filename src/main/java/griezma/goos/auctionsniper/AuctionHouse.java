package griezma.goos.auctionsniper;

public interface AuctionHouse {
    void disconnect();

    Auction auctionFor(String itemId);
}
