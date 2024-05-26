package noorg.dehucka.testtask;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import noorg.dehucka.testtask.data.LineInfo;

import java.io.BufferedReader;
import java.io.FileReader;
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

    /** Минимально допустимый уровень доступности (проценты. Например, "99.9") */
    private final float minAccessibilityLevel;
    /** Приемлемое время ответа (миллисекунды. Например, "45") */
    private final int maxRequestDuration;

    public void analyze() {
        LocalTime periodStart = null;
        LocalTime periodEnd = null;
        LocalTime currentTime = null;
        int currentRequestsCount = 0;
        int currentRequestFailuresCount = 0;
        int totalRequestsCount = 0;
        int totalRequestFailuresCount = 0;

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
        try (BufferedReader reader = getReader()) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                LineInfo lineInfo = lineExtractor.extract(line);

                LocalTime requestTime = lineInfo.getRequestTime();
                if (requestTime.equals(currentTime)) {
                    currentRequestsCount += 1;
                    if (hasFailure(lineInfo))
                        currentRequestFailuresCount += 1;
                    int a = 0;//TODO remove
                    continue;
                }


                float currentAccessibilityLevel = (currentRequestsCount - currentRequestFailuresCount) * 100f / currentRequestsCount;
                if (currentAccessibilityLevel < minAccessibilityLevel) {
                    if (periodStart == null) periodStart = currentTime;
                    periodEnd = currentTime;
                    totalRequestsCount += currentRequestsCount;
                    totalRequestFailuresCount += currentRequestFailuresCount;
                } else {
                    float totalAccessibilityLevel = (totalRequestsCount - totalRequestFailuresCount) * 100f / totalRequestsCount;
                    printlnTotalPeriod(periodStart, periodEnd, totalAccessibilityLevel);

                    periodStart = null;
                    totalRequestsCount = 0;
                    totalRequestFailuresCount = 0;
                }

                currentTime = requestTime;
                currentRequestsCount = 1;
                currentRequestFailuresCount = hasFailure(lineInfo) ? 1 : 0;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка во время считывания следующей строки", e);
        }
    }

    @SneakyThrows
    private BufferedReader getReader() {
        return new BufferedReader(new FileReader("C:\\data\\personal\\ideaProjects\\log-filtrator\\src\\main\\resources\\access.log"));
    }

    private boolean hasFailure(LineInfo lineInfo) {
        return lineInfo.getRequestDuration() > maxRequestDuration || lineInfo.getHttpCode() / 100 == 5;
    }

    private void printlnTotalPeriod(LocalTime startTime, LocalTime endTime, float accessibilityLevel) {
        if (startTime == null) return;

        System.out.println(startTime + " " + endTime + " " + accessibilityLevel);
    }

//    private void executePerLine(Consumer<String> block) {
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                block.accept(line);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Ошибка во время считывания следующей строки", e);
//        }
//    }
}
