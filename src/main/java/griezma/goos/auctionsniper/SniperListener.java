package griezma.goos.auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperLost();

    void sniperBidding(); 
}