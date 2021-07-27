package griezma.goos.auctionsniper.sniper;

public enum SniperState {
    JOINING {
        @Override
        public SniperState whenAuctionClosed() { 
            return LOST;
        }
    },
    BIDDING {
        @Override
        public SniperState whenAuctionClosed() { 
            return LOST;
        }
    },
    WINNING {
        @Override
        public SniperState whenAuctionClosed() { 
            return WON;
        }
    },
    LOSING {
        @Override
        public SniperState whenAuctionClosed() {
            return LOST;
        }
    },
    LOST,
    WON;

    public SniperState whenAuctionClosed() {
        throw new IllegalStateException("Defect: Auction is already closed");
    }
}