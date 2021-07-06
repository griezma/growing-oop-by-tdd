package griezma.goos.auctionsniper;

import javax.swing.SwingUtilities;

import griezma.goos.auctionsniper.Main.MainWindow;

public class SniperStateDisplayer implements SniperListener {
    private final MainWindow ui;

    SniperStateDisplayer(MainWindow ui) {
        this.ui = ui;
    }
    

    @Override
    public void sniperLost() {
        showStatus(MainWindow.STATUS_LOST);
    }

    @Override
    public void sniperBidding() {
        showStatus(MainWindow.STATUS_BIDDING);
    }

    public void sniperWinning() {
        showStatus(MainWindow.STATUS_WINNING);
    }

    private void showStatus(String status) {
        SwingUtilities.invokeLater(() -> ui.showStatus(status));
    }

}
