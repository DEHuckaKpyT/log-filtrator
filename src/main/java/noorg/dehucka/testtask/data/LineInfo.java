package noorg.dehucka.testtask.data;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

/**
 * Created on 25.05.2024.
 *
 * @author Denis Matytsin
 */
@Value
@Builder
public class LineInfo {
    /** Время, в которое совершён запрос */
    @NotNull
    LocalTime requestTime;
    /** HTTP код ответа */
    int httpCode;
    /** Время обработки запроса в миллисекундах */
    float requestDuration;
}
