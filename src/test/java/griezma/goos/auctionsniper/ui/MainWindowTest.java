package griezma.goos.auctionsniper.ui;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Test;

import griezma.goos.auctionsniper.AuctionSniperDriver;
import griezma.goos.auctionsniper.SniperPortfolio;
import griezma.goos.auctionsniper.sniper.Item;

public class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makeUserRequestWhenJoinButtonClicked() {
        ValueMatcherProbe<Item> itemProbe = new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "join request");
        mainWindow.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(Item item) {
                itemProbe.setReceivedValue(item);
            }
        });
        driver.startBiddingFor("an item-id", 789);
        driver.check(itemProbe);
    }
    
}
