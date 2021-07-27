package griezma.goos.auctionsniper.auction;

public interface Auction {
    void join();
    void bid(int amount);
    void addAuctionEventListener(AuctionEventListener auctionSniper);
}
