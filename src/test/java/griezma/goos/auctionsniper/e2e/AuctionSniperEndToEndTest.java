package griezma.goos.auctionsniper.e2e;

import org.junit.After;
import org.junit.Test;

import griezma.goos.auctionsniper.ApplicationRunner;
import griezma.goos.auctionsniper.FakeAuctionServer;

import static griezma.goos.auctionsniper.ApplicationRunner.SNIPER_XMPP_ID;


public class AuctionSniperEndToEndTest {

    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequest(SNIPER_XMPP_ID);

        auction.announceClosed();
        application.showsSniperHasLost(auction, 0);
    }

    @Test
    public void sniperWinsByBiddingHigher() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequest(SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.showsSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
        application.showsSniperIsWinning(auction, 1098);

        auction.announceClosed();
        application.showsSniperHasWon(auction, 1098);
    }

    @Test
    public void sniperBidsForMultipleItems() throws Exception {
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);

        auction.hasReceivedJoinRequest(SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequest(SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        auction.hasReceivedBid(1098, SNIPER_XMPP_ID);

        auction2.reportPrice(500, 21, "other bidder");
        auction2.hasReceivedBid(521, SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, SNIPER_XMPP_ID);

        application.showsSniperIsWinning(auction, 1098);
        application.showsSniperIsWinning(auction2, 521);

        auction.announceClosed();
        auction2.announceClosed();

        application.showsSniperHasWon(auction, 1098);
        application.showsSniperHasWon(auction2, 521);
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
