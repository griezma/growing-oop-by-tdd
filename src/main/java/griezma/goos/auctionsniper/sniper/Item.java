package griezma.goos.auctionsniper.sniper;

public class Item {
    public final String identifier;
    public final int stopPrice;

    public Item(String identifier, int stopPrice) {
        this.identifier = identifier;
        this.stopPrice = stopPrice;
    }

    public boolean allowsBid(int bid) {
        return bid <= stopPrice;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Item))
            return false;
        Item other = (Item) o;
        return other.identifier.equals(identifier) && other.stopPrice == stopPrice;
    }

    @Override
    public String toString() {
        return "Item[identifier=" + identifier + ", stopPrice=" + stopPrice + ']';
    }
}
