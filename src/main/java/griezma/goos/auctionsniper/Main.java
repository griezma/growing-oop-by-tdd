package griezma.goos.auctionsniper;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {

    public static final String MAIN_WINDOW_NAME = "AuctionSniperMain";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-item-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String SNIPER_STATUS_NAME = "STATUS";

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d";
    
    static private final Logger log = Logger.getLogger("Main");

    static {
        initLogging();
    }

    public static void main(String... args ) throws Exception {

        Main main = new Main();

        main.joinAuction(
                connection(
                        args[ARG_HOSTNAME],
                        args[ARG_USERNAME],
                        args[ARG_PASSWORD]),
                        args[ARG_ITEM_ID]);
    }

    private MainWindow ui;
    private Auction auction;

    private Main() throws Exception {
        startUI();
    }

    private void startUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        log.info("joinAuction " + itemId);
        disconnectWhenUICloses(connection);

        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                null
        );

        auction = new XmppAuction(chat);

        chat.addMessageListener(
            new AuctionMessageTranslator(
                new AuctionSniper(auction, new SniperStateDisplayer())));

        auction.join();
    }

    // @Override
    // public void currentPrice(int price, int increment, String bidder) {
    //     log.info(String.format("currentPrice: price=%d, bidder=%s", price, bidder));
    //     SwingUtilities.invokeLater(() -> ui.showStatus(MainWindow.STATUS_BIDDING));
    // }

    private void disconnectWhenUICloses(XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    public class SniperStateDisplayer implements SniperListener {
    
        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }
    
        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }
    
        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }
    
        private void showStatus(String status) {
            SwingUtilities.invokeLater(() -> ui.showStatus(status));
        }
    
    }

    public static class XmppAuction implements Auction {
        private final Chat chat;

        private XmppAuction(Chat chat) {
            this.chat = chat;
        }

        public void join() {
            try {
                chat.sendMessage(JOIN_COMMAND_FORMAT);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }

        public void bid(int amount) {
            try {
                chat.sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MainWindow extends JFrame {
        public static final String STATUS_JOINING = "joining";
        public static final String STATUS_LOST = "lost";
        public static final String STATUS_BIDDING = "bidding";
        public static final String STATUS_WINNING = "winning";

        private final JLabel sniperStatus = createLabel(STATUS_JOINING);

        public MainWindow(){
            super("Auction Sniper");
            setName(MAIN_WINDOW_NAME);
            add(sniperStatus);
            pack();
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        }
        
        private JLabel createLabel(String initialText) {
            JLabel result = new JLabel(initialText);
            result.setName(SNIPER_STATUS_NAME);
            result.setBorder(new LineBorder(Color.BLACK));
            return result;
        }

        public void showStatus(String status) {
            sniperStatus.setText(status);
        }
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        String auctionId = String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
        log.info(String.format("auctionId: %s", auctionId));
        return auctionId;
    }

    private static XMPPConnection connection(String host, String user, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(host);
        connection.connect();
        connection.login(user, password, AUCTION_RESOURCE);
        return connection;
    }

    private static void initLogging() {
        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

