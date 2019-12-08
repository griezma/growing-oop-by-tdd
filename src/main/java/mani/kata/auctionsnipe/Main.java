package mani.kata.auctionsnipe;


import javax.swing.*;

public class Main {

    public static final String STATUS_JOINING = "joining";
    public static final String SNIPER_STATUS_NAME = "STATUS";
    public static final String STATUS_LOST = "lost";

    public static void main(String... args ) throws Exception {
        Main main = new Main();
    }

    private MainWindow ui;

    Main() throws Exception {
        startUI();
    }

    private void startUI() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
    }
}

