package griezma.goos.auctionsniper.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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

import griezma.goos.auctionsniper.SniperSnapshot;
import griezma.goos.auctionsniper.SniperState;
import griezma.goos.auctionsniper.ui.SnipersTableModel.Column;

public class SnipersTableModelTest {
    private TableModelListener listener = mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }
    
    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        model.addSniper(SniperSnapshot.joining("item id"));
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
        SniperSnapshot joining = SniperSnapshot.joining("item123");

        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        verify(listener, times(1)).tableChanged(argThat(aRowChangeEvent(0)));
        assertRowMatchesSnapshot(0, joining);
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertColumnEquals(0, Column.ITEM_ID, "item 0");
        assertColumnEquals(1, Column.ITEM_ID, "item 1");
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
