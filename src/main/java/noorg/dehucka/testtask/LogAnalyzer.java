package noorg.dehucka.testtask;

import lombok.AllArgsConstructor;
import noorg.dehucka.testtask.data.LineInfo;
import noorg.dehucka.testtask.provider.ReadProvider;
import noorg.dehucka.testtask.provider.WriteProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;

/**
 * Created on 23.05.2024.
 *
 * @author Denis Matytsin
 */
@AllArgsConstructor
public class LogAnalyzer {

    private final LogLineExtractor lineExtractor;
    private final ReadProvider readerProvider;
    private final WriteProvider writeProvider;

    /** Минимально допустимый уровень доступности (проценты) (например, "99.9") */
    private final float minAccessibilityLevel;
    /** Приемлемое время ответа (миллисекунды) (например, "45") */
    private final int maxRequestDuration;

    public void analyze() {
        LocalTime periodStart = null; // начало периода, у которого доступность меньше minAccessibilityLevel
        LocalTime periodEnd = null; // конец периода, у которого доступность меньше minAccessibilityLevel
        LocalTime currentTime = null; // текущая обрабатываемая секунда
        int currentRequestsCount = 0; // общее количество запросов за текущую секунду
        int currentRequestFailuresCount = 0; // количество отказов за текущую секунду
        int totalRequestsCount = 0; // общее количество запросов за период
        int totalRequestFailuresCount = 0; // количество отказов за период

        try (BufferedReader reader = readerProvider.getReader()) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                LineInfo lineInfo = lineExtractor.extract(line);
                if (lineInfo == null) break;

                LocalTime requestTime = lineInfo.getRequestTime();
                // если предыдущий запрос был в ту же секунду, то только увеличиваются счётчики за секунду
                if (requestTime.equals(currentTime)) {
                    currentRequestsCount += 1;
                    if (hasFailure(lineInfo)) currentRequestFailuresCount += 1;
                    continue;
                }

                // иначе если запрос в следующую секунду, то проверяется уровень доступности предыдущей секунды
                float currentAccessibilityLevel = (currentRequestsCount - currentRequestFailuresCount) * 100f / currentRequestsCount;
                // если он меньше допустимого, то обновляется/создаётся период и увеличиваются счётчики за период
                if (currentAccessibilityLevel < minAccessibilityLevel) {
                    if (periodStart == null) periodStart = currentTime;
                    periodEnd = currentTime;
                    totalRequestsCount += currentRequestsCount;
                    totalRequestFailuresCount += currentRequestFailuresCount;
                } else {
                    // если больше допустимого, то выводится последний период и всё обнуляется
                    float totalAccessibilityLevel = (totalRequestsCount - totalRequestFailuresCount) * 100f / totalRequestsCount;
                    printlnTotalPeriod(periodStart, periodEnd, totalAccessibilityLevel);

                    periodStart = null;
                    totalRequestsCount = 0;
                    totalRequestFailuresCount = 0;
                }

                // обнуляются и задаются значения текущего запроса, потому что он не вошёл в блок с проверкой "requestTime.equals(currentTime)"
                currentTime = requestTime;
                currentRequestsCount = 1;
                currentRequestFailuresCount = hasFailure(lineInfo) ? 1 : 0;
            }

            // контрольная проверка для того, чтобы вывести период, после которого не было секунды с допустимым уровнем доступности
            float currentAccessibilityLevel = (currentRequestsCount - currentRequestFailuresCount) * 100f / currentRequestsCount;
            if (currentAccessibilityLevel < minAccessibilityLevel) {
                if (periodStart == null) periodStart = currentTime;
                periodEnd = currentTime;
                totalRequestsCount += currentRequestsCount;
                totalRequestFailuresCount += currentRequestFailuresCount;
            }
            float totalAccessibilityLevel = (totalRequestsCount - totalRequestFailuresCount) * 100f / totalRequestsCount;
            printlnTotalPeriod(periodStart, periodEnd, totalAccessibilityLevel);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка во время считывания очередной строки", e);
        }
    }

    private boolean hasFailure(LineInfo lineInfo) {
        return lineInfo.getRequestDuration() > maxRequestDuration || lineInfo.getHttpCode() / 100 == 5;
    }

    private void printlnTotalPeriod(LocalTime startTime, LocalTime endTime, float accessibilityLevel) {
        if (startTime == null) return;

        writeProvider.write(startTime + " " + endTime + " " + accessibilityLevel);
    }
}
