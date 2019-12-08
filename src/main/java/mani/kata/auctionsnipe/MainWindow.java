package mani.kata.auctionsnipe;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;

import static mani.kata.auctionsnipe.Main.SNIPER_STATUS_NAME;
import static mani.kata.auctionsnipe.Main.STATUS_JOINING;

public class MainWindow extends JFrame {
    public static final String MAIN_WINDOW_NAME = "AuctionSniperMain";

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

    public void showStatus(String statusLost) {
        sniperStatus.setText(statusLost);
    }
}
