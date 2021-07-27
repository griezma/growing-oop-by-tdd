package griezma.goos.auctionsniper.sniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperStateChanged(SniperSnapshot sniperSnapshot);
}