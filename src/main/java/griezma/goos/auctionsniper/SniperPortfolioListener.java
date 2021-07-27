package griezma.goos.auctionsniper;

import java.util.EventListener;

import griezma.goos.auctionsniper.sniper.AuctionSniper;

public interface SniperPortfolioListener extends EventListener {
    void sniperAdded(AuctionSniper sniper);   
}
