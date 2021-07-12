package griezma.goos.auctionsniper.ui;

import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import griezma.goos.auctionsniper.SniperListener;
import griezma.goos.auctionsniper.SniperSnapshot;
import griezma.goos.auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
    private static final Logger log = Logger.getLogger("SnipersTableModel");

    enum Column {
        ITEM_ID("Item") {
            @Override public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.itemId;
            }
        },
        LAST_PRICE("Last Price") {
            @Override public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.lastPrice;
            }
        },
        LAST_BID("Last Bid") {
            @Override public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.lastBid;
            }
        },
        STATE("State") {
            @Override public Object valueIn(SniperSnapshot snapshot) {
                return snapshot.sniperState;
            }
        };

        static Column at(int index) {
            return values()[index];
        }

        final String name;

        Column(String name) {
            this.name = name;
        }

        abstract public Object valueIn(SniperSnapshot snapshot);
    }

    private static String[] STATUS_TEXT = {
        MainWindow.STATUS_JOINING,
        MainWindow.STATUS_BIDDING,
        MainWindow.STATUS_WINNING,
        MainWindow.STATUS_LOST,
        MainWindow.STATUS_WON
    };

    private SniperSnapshot sniperSnapshot = SniperSnapshot.joining("");

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        Column col = Column.at(colIndex);

        switch (col) {
            case ITEM_ID: return sniperSnapshot.itemId;
            case LAST_PRICE: return sniperSnapshot.lastPrice;
            case LAST_BID: return sniperSnapshot.lastBid;
            case STATE: return textFor(sniperSnapshot.sniperState);
            default: throw new IllegalArgumentException("Invalid column index: " + colIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        log.info("sniperStateChanged: " + newSnapshot);
        this.sniperSnapshot = newSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    private String textFor(SniperState sniperState) {
        return STATUS_TEXT[sniperState.ordinal()];
    }
}