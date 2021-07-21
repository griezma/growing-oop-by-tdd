package griezma.goos.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import griezma.goos.auctionsniper.ui.MainWindow;
import griezma.goos.auctionsniper.ui.SniperPortfolio;
import griezma.goos.auctionsniper.xmpp.XmppAuctionHouse;

public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    public static final String SNIPER_STATUS_NAME = "STATUS";

    static final Logger log = Logger.getLogger("Main");

    static {
        initLogging();
    }

    public static void main(String... args) throws Exception {
        AuctionHouse auctionHouse = XmppAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        
        Main main = new Main();
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private MainWindow ui;
    private final SniperPortfolio portfolio = new SniperPortfolio();

    
    private Main() throws Exception {
        startUI();
    }

    private void startUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(portfolio));
    }
    
    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(AuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }

    static void initLogging() {
        System.out.println("Main: initLogging");
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

