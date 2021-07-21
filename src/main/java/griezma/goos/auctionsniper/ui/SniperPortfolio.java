package griezma.goos.auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import griezma.goos.auctionsniper.AuctionSniper;
import griezma.goos.auctionsniper.SniperPortfolioListener;
import griezma.goos.auctionsniper.SniperCollector;
import griezma.goos.auctionsniper.utils.Announcer;

public class SniperPortfolio implements SniperCollector {

    private final Announcer<SniperPortfolioListener> listeners = Announcer.to(SniperPortfolioListener.class);
    private List<AuctionSniper> snipers = new ArrayList<>();

    public void addPortfolioListener(SniperPortfolioListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void add(AuctionSniper sniper) {
        snipers.add(sniper);
        listeners.announce().sniperAdded(sniper);        
    }
}
