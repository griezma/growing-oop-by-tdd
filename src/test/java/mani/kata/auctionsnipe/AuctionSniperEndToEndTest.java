package mani.kata.auctionsnipe;

import static org.junit.Assert.assertTrue;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Test;


public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("54321");
    private final ApplicationRunner app = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        app.startBiddingIn(auction);
        auction.hasReceivedJoinRequest();
        auction.announceClosed();
        app.showsSniperHasLost();
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApp() {
        app.stop();
    }
}
