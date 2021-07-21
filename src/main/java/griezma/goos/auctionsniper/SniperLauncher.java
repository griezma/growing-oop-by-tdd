package griezma.goos.auctionsniper;

import java.util.ArrayList;

final class SniperLauncher implements UserRequestListener {
  
    private final AuctionHouse auctionHouse;
    private final ArrayList<Auction> auctions = new ArrayList<Auction>();
    private final SniperCollector sniperCollector;

    SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.sniperCollector = collector;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        auction.join();

        sniperCollector.add(sniper);

        auctions.add(auction);
    }
}