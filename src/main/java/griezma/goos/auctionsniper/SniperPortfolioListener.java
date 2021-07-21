package griezma.goos.auctionsniper;

import java.util.EventListener;

public interface SniperPortfolioListener extends EventListener {
    void sniperAdded(AuctionSniper sniper);   
}
