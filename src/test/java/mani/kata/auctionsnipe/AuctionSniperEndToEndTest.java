package mani.kata.auctionsnipe;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;


public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequest();

        auction.announceClosed();
        application.showsSniperHasLost();
    }

    @Test
    public void sniperMakesHigherBidAndLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLost();
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApp() {
        application.stop();
    }
}
