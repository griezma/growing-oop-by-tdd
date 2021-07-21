package griezma.goos.auctionsniper.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import griezma.goos.auctionsniper.Auction;
import griezma.goos.auctionsniper.AuctionSniper;
import griezma.goos.auctionsniper.SniperSnapshot;
import griezma.goos.auctionsniper.SniperState;
import griezma.goos.auctionsniper.ui.SnipersTableModel.Column;

public class SnipersTableModelTest {
    private TableModelListener listener = mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();
    private final SniperPortfolio portfolio = new SniperPortfolio();
    
    private final Auction auction = mock(Auction.class);

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
        portfolio.addPortfolioListener(model);
    }
    
    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        portfolio.add(auctionSniper("item id"));
        model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

        verify(listener, atLeast(1)).tableChanged(argThat(aRowChangeEvent(0)));

        assertColumnEquals(0, Column.ITEM_ID, "item id");
        assertColumnEquals(0, Column.LAST_PRICE, 555);
        assertColumnEquals(0, Column.LAST_BID, 666);
        assertColumnEquals(0, Column.STATE, MainWindow.STATUS_BIDDING);
    }

    @Test
    public void setsUpColumnHeadings() {
        for (SnipersTableModel.Column column :  Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenerWhenAddingASniper() {
        assertEquals(0, model.getRowCount());

        portfolio.add(auctionSniper("item123"));

        assertEquals(1, model.getRowCount());
        verify(listener).tableChanged(any());
        // verify(listener).tableChanged(argThat(aRowChangeEvent(0)));
        assertRowMatchesSnapshot(0, SniperSnapshot.joining("item123"));
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        portfolio.add(auctionSniper("item 0"));
        portfolio.add(auctionSniper("item 1"));

        assertColumnEquals(0, Column.ITEM_ID, "item 0");
        assertColumnEquals(1, Column.ITEM_ID, "item 1");
    }

    private AuctionSniper auctionSniper(String item) {
        return new AuctionSniper(item, auction);
    }

    private void assertRowMatchesSnapshot(int rowIndex, SniperSnapshot expected) {
        assertColumnEquals(rowIndex, Column.ITEM_ID, expected.itemId);
        assertColumnEquals(rowIndex, Column.LAST_PRICE, expected.lastPrice);
        assertColumnEquals(rowIndex, Column.LAST_BID, expected.lastBid);
        assertColumnEquals(rowIndex, Column.STATE, model.textFor(expected.sniperState));
        
    }

    private void assertColumnEquals(int rowIndex, Column column, Object expected) {
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangeEvent(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(model, rowIndex));
    }
}
