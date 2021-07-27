package griezma.goos.auctionsniper;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import griezma.goos.auctionsniper.ui.MainWindow;

public class AuctionSniperDriver extends JFrameDriver {
    static {
        System.setProperty("com.objogate.wl.keyboard", "US");
    }
    
    public AuctionSniperDriver(int timeout) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(MainWindow.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeout, 100));
    }

    public void startBiddingFor(String itemId) {
        itemIdField().replaceAllText(itemId);
        bidButton().click();
    }

    public void startBiddingFor(String itemId, int stopPrice) {
        itemIdField().replaceAllText(itemId);
        stopPriceField().replaceAllText(String.valueOf(stopPrice));
        bidButton().click();
    }

    private JTextFieldDriver itemIdField() {
        JTextFieldDriver newItemId = new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JTextFieldDriver stopPriceField() {
        JTextFieldDriver stopPrice = new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_STOP_PRICE_NAME));
        stopPrice.focusWithMouse();
        return stopPrice;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
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
