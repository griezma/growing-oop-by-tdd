package griezma.goos.auctionsniper.e2e;

import org.junit.After;
import org.junit.Test;

import griezma.goos.auctionsniper.ApplicationRunner;
import griezma.goos.auctionsniper.FakeAuctionServer;

import static griezma.goos.auctionsniper.ApplicationRunner.SNIPER_XMPP_ID;


public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequest(SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLost();
    }

    @Test
    public void sniperMakesAHigherBidButLooses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequest(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding();

        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

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
