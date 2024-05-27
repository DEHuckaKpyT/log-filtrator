package noorg.dehucka.testtask;

import noorg.dehucka.testtask.provider.ReadProvider;
import noorg.dehucka.testtask.provider.WriteProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Created on 27.05.2024.
 *
 * @author Denis Matytsin
 */
class LogAnalyzerTest {

    private final ReadProvider readProvider = mock(ReadProvider.class);
    private final WriteProvider writeProvider = mock(WriteProvider.class);
    private final LogAnalyzer analyzer = new LogAnalyzer(new LogLineExtractor(),
                                                         readProvider,
                                                         writeProvider,
                                                         50.1f,
                                                         10);

    private final BufferedReader reader = mock(BufferedReader.class);

    @BeforeEach
    void setUp() throws FileNotFoundException {
        when(readProvider.getReader()).thenReturn(reader);
    }

    @Test
    void analyzeSingleSecondBadAccessibility() throws IOException {
        // Arrange
        when(reader.readLine()).thenReturn("...01:01:01 +1000] \"HTTP/1.1\" 200 2 10.1 \"-\"...")
                               .thenReturn("");

        // Act
        analyzer.analyze();

        // Assert
        verify(writeProvider).write("01:01:01 01:01:01 0.0");
    }

    @Test
    void analyzeFirstSecondBadAccessibility() throws IOException {
        // Arrange
        when(reader.readLine()).thenReturn("...01:01:01 +1000] \"HTTP/1.1\" 200 2 1.1 \"-\"...")
                               .thenReturn("...01:01:01 +1000] \"HTTP/1.1\" 200 2 20.2 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 3.3 \"-\"...")
                               .thenReturn("...01:01:03 +1000] \"HTTP/1.1\" 200 2 4.4 \"-\"...")
                               .thenReturn(null);

        // Act
        analyzer.analyze();

        // Assert
        verify(writeProvider).write("01:01:01 01:01:01 50.0");
    }

    @Test
    void analyzeMiddleSecondBadAccessibility() throws IOException {
        // Arrange
        when(reader.readLine()).thenReturn("...01:01:01 +1000] \"HTTP/1.1\" 200 2 1.1 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 20.2 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 3.3 \"-\"...")
                               .thenReturn("...01:01:03 +1000] \"HTTP/1.1\" 200 2 4.4 \"-\"...")
                               .thenReturn(null);

        // Act
        analyzer.analyze();

        // Assert
        verify(writeProvider).write("01:01:02 01:01:02 50.0");
    }

    @Test
    void analyzeLastSecondBadAccessibility() throws IOException {
        // Arrange
        when(reader.readLine()).thenReturn("...01:01:01 +1000] \"HTTP/1.1\" 200 2 1.1 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 2.2 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 3.3 \"-\"...")
                               .thenReturn("...01:01:03 +1000] \"HTTP/1.1\" 200 2 40.4 \"-\"...")
                               .thenReturn(null);

        // Act
        analyzer.analyze();

        // Assert
        verify(writeProvider).write("01:01:03 01:01:03 0.0");
    }

    @Test
    void analyzeSeveralMultipleSecondsBadAccessibility() throws IOException {
        // Arrange
        when(reader.readLine()).thenReturn("...01:01:01 +1000] \"HTTP/1.1\" 200 2 10.1 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 20.2 \"-\"...")
                               .thenReturn("...01:01:02 +1000] \"HTTP/1.1\" 200 2 3.3 \"-\"...")
                               .thenReturn("...01:01:03 +1000] \"HTTP/1.1\" 200 2 3.3 \"-\"...")
                               .thenReturn("...01:01:04 +1000] \"HTTP/1.1\" 200 2 40.4 \"-\"...")
                               .thenReturn("...01:01:05 +1000] \"HTTP/1.1\" 200 2 5 \"-\"...")
                               .thenReturn("...01:01:05 +1000] \"HTTP/1.1\" 200 2 6 \"-\"...")
                               .thenReturn("...01:01:06 +1000] \"HTTP/1.1\" 200 2 7 \"-\"...")
                               .thenReturn("...01:01:06 +1000] \"HTTP/1.1\" 200 2 80.8 \"-\"...")
                               .thenReturn("...01:01:07 +1000] \"HTTP/1.1\" 200 2 90.9 \"-\"...")
                               .thenReturn(null);

        // Act
        analyzer.analyze();

        // Assert
        InOrder inOrder = inOrder(writeProvider);
        inOrder.verify(writeProvider).write("01:01:01 01:01:02 33.333332");
        inOrder.verify(writeProvider).write("01:01:04 01:01:04 0.0");
        inOrder.verify(writeProvider).write("01:01:06 01:01:07 33.333332");
    }
}