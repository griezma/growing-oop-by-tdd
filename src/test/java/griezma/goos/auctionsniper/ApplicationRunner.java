package griezma.goos.auctionsniper;

public class ApplicationRunner {

    private static final String XMPP_HOST = "localhost";
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";

    private AuctionSniperDriver driver;

    public void startBiddingIn(FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOST, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon((true));
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(Main.MainWindow.STATUS_JOINING);
    }

    public void showsSniperHasLost() {
        driver.showsSniperStatus(Main.MainWindow.STATUS_LOST);
    }


    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}