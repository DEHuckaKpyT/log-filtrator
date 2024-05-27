package noorg.dehucka.testtask;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 27.05.2024.
 *
 * @author Denis Matytsin
 */
class ArgsParserTest {
    private final ArgsParser parser = new ArgsParser(new String[]{"-u", "99.9", "-t", "45"});

    @Test
    void getMinAccessibilityLevel() {
        // Act
        float actual = parser.getMinAccessibilityLevel();

        // Assert
        assertThat(actual).isEqualTo(99.9f);
    }

    @Test
    void getMaxRequestDuration() {
        // Act
        int actual = parser.getMaxRequestDuration();

        // Assert
        assertThat(actual).isEqualTo(45);
    }
}