package griezma.goos.auctionsniper.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Test;

import griezma.goos.auctionsniper.AuctionSniperDriver;
import griezma.goos.auctionsniper.UserRequestListener;

public class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makeUserRequestWhenJoinButtonClicked() {
        ValueMatcherProbe<String> buttonProbe = new ValueMatcherProbe<>(equalTo("an item-id"), "join request");
        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                buttonProbe.setReceivedValue(itemId);
            }
        });
        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }
    
}
