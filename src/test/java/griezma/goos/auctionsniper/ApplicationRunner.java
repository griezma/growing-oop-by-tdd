package griezma.goos.auctionsniper;

import griezma.goos.auctionsniper.ui.MainWindow;

public class ApplicationRunner {

    private static final String XMPP_HOST = "localhost";
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";

    public static final String SNIPER_XMPP_ID = "sniper@045a0220ac76/Auction";

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();

        for (FakeAuctionServer auction : auctions) {
            final String itemId = auction.getItemId();
            driver.startBiddingFor(itemId);
            driver.showsSniperStatus(itemId, 0, 0, MainWindow.STATUS_JOINING);
        }
    }

    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.setDaemon((true));
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

    private static String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[3 + auctions.length];
        arguments[0] = XMPP_HOST;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        int i = 3;
        for (FakeAuctionServer auction : auctions) {
            arguments[i++] = auction.getItemId();
        }
        return arguments;
    }

    public void showsSniperHasLost(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, MainWindow.STATUS_LOST);
    }

    public void showsSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, MainWindow.STATUS_BIDDING);
    }

    public void showsSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWon(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, MainWindow.STATUS_WON);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}