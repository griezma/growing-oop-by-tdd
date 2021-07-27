package griezma.goos.auctionsniper;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.LogManager;

import org.hamcrest.Matcher;

public class AuctionLogDriver {
    private static final String LOG_FILE_NAME = "auction-sniper.log";
    
    private final Path logFile = Path.of(LOG_FILE_NAME);
    
    public void hasEntry(Matcher<String> matcher) throws IOException {
        assertThat(Files.readString(logFile), matcher);
    }

    public void clearLog() {
        try {
            Files.delete(logFile);
        } catch (IOException e) {
        }
        LogManager.getLogManager().reset();
    }
}
