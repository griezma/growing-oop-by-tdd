package mani.kata.auctionsnipe;

public class ApplicationRunner {

    private static final String XMPP_HOSTNAME = "localhost";
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";

    private AuctionSniperDriver driver;

    public void startBiddingIn(FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon((true));
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(Main.STATUS_JOINING);
    }

    public void showsSniperHasLost() {
        driver.showsSniperStatus(Main.STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}