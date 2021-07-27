package griezma.goos.auctionsniper.ui;


import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import griezma.goos.auctionsniper.sniper.SniperListener;
import griezma.goos.auctionsniper.sniper.SniperSnapshot;

public class SwingThreadSniperListener implements SniperListener {
    private static final Logger log = Logger.getLogger("SwingThreadSniperListener");

    private SnipersTableModel snipers;

    public SwingThreadSniperListener(SnipersTableModel snipers) {
        this.snipers = snipers;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot sniperState) {
        log.info("sniperStateChanged: " + sniperState);
        SwingUtilities.invokeLater(() -> this.snipers.sniperStateChanged(sniperState));
    }
}