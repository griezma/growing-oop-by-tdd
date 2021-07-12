package griezma.goos.auctionsniper;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

import javax.swing.table.JTableHeader;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import griezma.goos.auctionsniper.ui.MainWindow;

public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeout) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeout, 100));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
        JTableDriver tableDriver = new JTableDriver(this);
        tableDriver.hasRow(matching(
            withLabelText(equalTo(itemId)),
            withLabelText(String.valueOf(lastPrice)),
            withLabelText(String.valueOf(lastBid)),
            withLabelText(equalTo(statusText))    
        ));
    }

    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(
            withLabelText("Item"),
            withLabelText("Last Price"),
            withLabelText("Last Bid"),
            withLabelText("State")
        ));
    }
}
