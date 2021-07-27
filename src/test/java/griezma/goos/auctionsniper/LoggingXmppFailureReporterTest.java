package griezma.goos.auctionsniper;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Test;

public class LoggingXmppFailureReporterTest {
    private Logger logger = mock(Logger.class);
    private LoggingXmppFailureReporter reporter = new LoggingXmppFailureReporter(logger);

    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writesFailureToLog() {
        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));

        verify(logger).severe(withTextContaining("auction id", "bad message", Exception.class.getName()));
    }

    private String withTextContaining(String... args) {
        Stream<Matcher<String>> matchers = Stream.of(args).map(Matchers::containsString);
        return argThat(allOf(matchers.collect(toList())));
    }
    
}
