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
        application.showsSniperHasLost(auction, 0, 0);
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

    @Test
    public void sniperLosesAuctionWhenPriceBecomesTooHigh() throws Exception {
        final int stopPrice = 1100;
        auction.startSellingItem();
        application.startBiddingIn(auction, stopPrice);
        auction.hasReceivedJoinRequest(SNIPER_XMPP_ID);
        
        final int otherBid = 1000;
        final int increment = 98;
        auction.reportPrice(otherBid, increment, "other bidder");
        application.showsSniperIsBidding(auction, 1000, otherBid + increment);
        auction.hasReceivedBid(otherBid + increment, SNIPER_XMPP_ID);

        final int bid = otherBid + increment;
        auction.reportPrice(bid, 97, SNIPER_XMPP_ID);
        application.showsSniperIsWinning(auction, bid);
        
        final int winningPrice = 1197;
        auction.reportPrice(winningPrice, 10, "third party");
        
        final int lastOwnBid = otherBid + increment;
        application.showsSniperIsLosing(auction, winningPrice, lastOwnBid);

        auction.announceClosed();
        application.showsSniperHasLost(auction, winningPrice, lastOwnBid);
    }

    @Test
    public void sniperLosesImmediatelyWhenFirstPriceIsTooHigh() throws Exception {
        auction.startSellingItem();
        final int stopPrice = 1100;
        application.startBiddingIn(auction, stopPrice);

        final int otherBid = 1099;
        final int increment = 2;
        auction.reportPrice(otherBid, increment, "other bidder");
        application.showsSniperIsLosing(auction, otherBid, 0);

        auction.announceClosed();
        application.showsSniperHasLost(auction, otherBid, 0);
    }

    @Test
    public void sniperContinuousLosingOnceStopPriceReached() throws Exception {
        auction.startSellingItem();
        final int stopPrice = 1100;
        application.startBiddingIn(auction, stopPrice);

        auction.reportPrice(1000, 98, "other bidder");
        final int ownBid = 1000 + 98;
        auction.reportPrice(ownBid, 97, SNIPER_XMPP_ID);

        application.showsSniperIsWinning(auction, ownBid);

        final int thirdBid = ownBid + 97;
        auction.reportPrice(thirdBid, 96, "third bidder");
        application.showsSniperIsLosing(auction, thirdBid, ownBid);

        final int winningPrice = thirdBid + 96;
        auction.reportPrice(winningPrice, 95, "fourth bidder");

        application.showsSniperIsLosing(auction, winningPrice, ownBid);

        auction.announceClosed();

        application.showsSniperHasLost(auction, winningPrice, ownBid);
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