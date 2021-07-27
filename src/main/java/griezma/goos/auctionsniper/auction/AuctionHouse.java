package griezma.goos.auctionsniper.auction;

public interface AuctionHouse {
    Auction auctionFor(String itemId);
    void disconnect();
}
