package mani.kata.auctionsnipe;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;

public class Main {
    private enum Args {
        HOSTNAME, USERNAME, PASSWORD, ITEM_ID
    }
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String STATUS_JOINING = "joining";
    public static final String SNIPER_STATUS_NAME = "STATUS";
    public static final String STATUS_LOST = "lost";

    public static void main(String... args ) throws Exception {
        Main main = new Main();

        main.joinAuction(
                connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                args[ARG_ITEM_ID]);
    }


    private static String auctionId(String itemId, XMPPConnection connection) {
        String result = String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
        //System.out.println("main auctionId " + result);
        return result;
    }

    private static XMPPConnection connection(String host, String user, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(host);
        connection.connect();
        connection.login(user,
                password,
                AUCTION_RESOURCE);
        return connection;
    }


    private MainWindow ui;

    Main() throws Exception {
        startUI();
    }

    private void startUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                (aChat, message) -> {
                    SwingUtilities.invokeLater(() -> ui.showStatus(STATUS_LOST));
                }
        );
        chat.sendMessage(new Message());
    }
}

