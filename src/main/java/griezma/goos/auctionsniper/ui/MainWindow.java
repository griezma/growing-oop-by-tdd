package griezma.goos.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import griezma.goos.auctionsniper.SniperPortfolio;
import griezma.goos.auctionsniper.sniper.Item;
import griezma.goos.auctionsniper.utils.Announcer;

public class MainWindow extends JFrame {

    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "AuctionSniperMain";
    
    public static final String STATUS_JOINING = "joining";
    public static final String STATUS_LOST = "lost";
    public static final String STATUS_BIDDING = "bidding";
    public static final String STATUS_WINNING = "winning";
    public static final String STATUS_WON = "won";
    public static final String STATUS_LOSING = "losing";
    
    private static final String SNIPERS_TABLE_NAME = "AuctionSniperTable";
    public static final String NEW_ITEM_ID_NAME = "NewItemField";
    private static final String STOP_PRICE_LABEL = "Stop price:";
    private static final String ITEMID_LABEL = "Item:";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "StopPriceField";
    public static final String JOIN_BUTTON_NAME = "JoinAuctionButton";
    
    private Announcer<UserRequestListener> userRequestListener = Announcer.to(UserRequestListener.class);
    
    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public void addUserRequestListener(UserRequestListener listener) {
        this.userRequestListener.addListener(listener);
    }

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());

        controls.add(new JLabel(ITEMID_LABEL));
        
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        controls.add(itemIdField);

        controls.add(new JLabel(STOP_PRICE_LABEL));

        final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getNumberInstance());
        stopPriceField.setColumns(25);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(stopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userRequestListener.announce().joinAuction(new Item(itemId(), stopPrice()));
            }
            String itemId() {
                return itemIdField.getText();
            }
            int stopPrice() {
                Number value = (Number)stopPriceField.getValue();
                return value != null ? value.intValue() : Integer.MAX_VALUE;
            }
        });
        controls.add(joinAuctionButton);
        return controls;
    }
}