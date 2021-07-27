package griezma.goos.auctionsniper;

import java.util.ArrayList;

import griezma.goos.auctionsniper.auction.Auction;
import griezma.goos.auctionsniper.auction.AuctionHouse;
import griezma.goos.auctionsniper.sniper.AuctionSniper;
import griezma.goos.auctionsniper.sniper.Item;
import griezma.goos.auctionsniper.sniper.SniperCollector;
import griezma.goos.auctionsniper.ui.UserRequestListener;

final class SniperLauncher implements UserRequestListener {
  
    private final AuctionHouse auctionHouse;
    private final ArrayList<Auction> auctions = new ArrayList<Auction>();
    private final SniperCollector sniperCollector;

    SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.sniperCollector = collector;
    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item.identifier);
        AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListener(sniper);
        auction.join();

        sniperCollector.add(sniper);

        auctions.add(auction);
    }
}