package griezma.goos.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import griezma.goos.auctionsniper.ui.MainWindow;
import griezma.goos.auctionsniper.ui.SnipersTableModel;

public class Main {

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
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
    private final SnipersTableModel snipers = new SnipersTableModel();
    private Auction auction;

    private Main() throws Exception {
        startUI();
    }

    private void startUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        log.info(String.format("joinAuction: user=%s", connection.getUser()));
        disconnectWhenUICloses(connection);

        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                null
        );

        auction = new XmppAuction(chat);

        chat.addMessageListener(
            new AuctionMessageTranslator(
                connection.getUser(),
                new AuctionSniper(itemId, auction, new SwingThreadSniperListener())));

        auction.join();
    }

    private void disconnectWhenUICloses(XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    public class SwingThreadSniperListener implements SniperListener {
    
        @Override
        public void sniperStateChanged(SniperSnapshot sniperState) {
            log.info("sniperStateChanged: " + sniperState);
            SwingUtilities.invokeLater(() -> snipers.sniperStateChanged(sniperState));
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

