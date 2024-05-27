package noorg.dehucka.testtask;

import noorg.dehucka.testtask.data.LineInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 25.05.2024.
 *
 * @author Denis Matytsin
 */
public class LogLineExtractor {

    private static final Pattern LINE_INFO_PATTERN = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}) \\+1000\\] \".*HTTP\\/1\\.1\" (\\d{3}) \\d (\\d+(?:\\.\\d*)?) \"-\"");

    @Nullable
    public LineInfo extract(@Nullable String line) {
        if (StringUtils.isBlank(line)) return null;

        Matcher matcher = LINE_INFO_PATTERN.matcher(line);

        if (!matcher.find())
            throw new RuntimeException("Не найдены время запроса, HTTP-код ответа и (или) время обработки запроса в миллисекундах в строке\n" + line);

        return LineInfo.builder()
                       .requestTime(LocalTime.parse(matcher.group(1)))
                       .httpCode(Integer.parseInt(matcher.group(2)))
                       .requestDuration(Float.parseFloat(matcher.group(3)))
                       .build();
    }
}
