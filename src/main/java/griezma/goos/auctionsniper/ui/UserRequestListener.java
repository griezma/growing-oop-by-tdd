package griezma.goos.auctionsniper.ui;

import java.util.EventListener;

import griezma.goos.auctionsniper.sniper.Item;

public interface UserRequestListener extends EventListener {
    void joinAuction(Item item);
}
