package griezma.goos.auctionsniper.xmpp;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import griezma.goos.auctionsniper.AuctionEventListener;
import griezma.goos.auctionsniper.AuctionEventListener.PriceSource;

class AuctionMessageTranslator implements MessageListener {
    private static Logger log = Logger.getLogger("AuctionMessageTranslator");
    
    private AuctionEventListener eventListener;
    private final String sniperId;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener eventListener) {
        this.eventListener = eventListener;
        this.sniperId = sniperId;
    }

    @Override
    public void processMessage(Chat unusedChat, Message message) {
        log.info("processMessage: " + message.getBody());

        AuctionEvent event = AuctionEvent.from(message.getBody());

        if (event.getType().equals("CLOSE")) {
            eventListener.auctionClosed();
        } 
        else if (event.getType().equals("PRICE")) {
            eventListener.currentPrice(event.currentPrice(), event.increment(), event.priceSource(sniperId));
        }
    }

    private static class AuctionEvent {
        static AuctionEvent from(String message) {
            return new AuctionEvent(parseMessage(message));
        }

        private static Map<String,String> parseMessage(String message) {
            return Arrays.stream(message.split(";"))
                .skip(1)
                .map(junk -> junk.split(":"))
                .collect(Collectors.toMap(pair -> pair[0].trim(), pair -> pair[1].trim()));
        }

        private Map<String,String> fields;

        private AuctionEvent(Map<String,String> fields) {
            this.fields = fields;
        }

        String getType() {
            return get("Event");
        }

        int currentPrice() {
            return getInt("CurrentPrice");
        }

        int increment() {
            return getInt("Increment");
        }

        String bidder() {
            return get("Bidder");
        }

        PriceSource priceSource(String sniperId) {
            return bidder().equals(sniperId) ? PriceSource.Sniper : PriceSource.OtherBidder;
        }

        private String get(String field) {
            return fields.get(field);
        } 

        private int getInt(String field) {
            return Integer.parseInt(fields.get(field));
        } 
    }
}
