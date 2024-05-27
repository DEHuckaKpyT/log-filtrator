package noorg.dehucka.testtask;

import noorg.dehucka.testtask.data.LineInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Created on 27.05.2024.
 *
 * @author Denis Matytsin
 */
class LogLineExtractorTest {
    private final LogLineExtractor extractor = new LogLineExtractor();

    @Test
    void extractGreenWay() {
        // Arrange
        String line = "192.168.32.181 - - [14/06/2017:16:48:51 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=870b3daa HTTP/1.1\" 200 2 22.152589 \"-\" \"@list-item-updater\" prio:0";

        // Act
        LineInfo actual = extractor.extract(line);

        // Assert
        assertThat(actual).isNotNull();
        assertSoftly(softly -> {
            softly.assertThat(actual.getRequestTime()).isEqualTo(LocalTime.parse("16:48:51"));
            softly.assertThat(actual.getHttpCode()).isEqualTo(200);
            softly.assertThat(actual.getRequestDuration()).isEqualTo(22.152589f);
        });
    }

    @Test
    void extractFromBlank() {
        // Act
        LineInfo actual = extractor.extract(" \n\t\r");

        // Assert
        assertThat(actual).isNull();
    }

    @Test
    void extractFromBadString() {
        // Act & Assert
        assertThatThrownBy(() -> extractor.extract("123"), "Не найдены время запроса, HTTP-код ответа и (или) время обработки запроса в миллисекундах в строке\n123");
    }
}