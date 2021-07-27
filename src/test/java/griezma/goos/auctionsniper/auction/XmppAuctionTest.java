package griezma.goos.auctionsniper.auction;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import griezma.goos.auctionsniper.ApplicationRunner;
import griezma.goos.auctionsniper.FakeAuctionServer;
import griezma.goos.auctionsniper.Main;
import griezma.goos.auctionsniper.xmpp.XmppAuctionHouse;

public class XmppAuctionTest {
    static { initLogging(); }

    private FakeAuctionServer server;
    private AuctionHouse auctionHouse;

    @Before
    public void beforeEach() throws Exception {
        server = new FakeAuctionServer("item-54321");
        server.startSellingItem();

        auctionHouse = XmppAuctionHouse.connect(ApplicationRunner.XMPP_HOST, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);
        Auction auction = auctionHouse.auctionFor(server.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        server.hasReceivedJoinRequest(ApplicationRunner.SNIPER_XMPP_ID);

        server.announceClosed();

        assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS));
    }

    private AuctionEventListener auctionClosedListener(CountDownLatch countdown) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                countdown.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource source) {
            }
        };
    }

    private static void initLogging() {
        try {
            Class.forName(Main.class.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
