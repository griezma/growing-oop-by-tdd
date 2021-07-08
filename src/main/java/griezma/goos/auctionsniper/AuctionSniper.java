package griezma.goos.auctionsniper;

public class AuctionSniper implements AuctionEventListener {

    private Auction auction;
    private SniperListener listener;
    private boolean winning;

    public AuctionSniper(Auction auction, SniperListener listener) {
        this.auction = auction;
        this.listener = listener;
    }

    @Override
    public void auctionClosed() {
        if (winning) {
            listener.sniperWon();
        } else {
            listener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        winning = source == PriceSource.Sniper;
        if (winning) {
            listener.sniperWinning();
        } else {
            auction.bid(price + increment);
            listener.sniperBidding();
        }
    }
}
