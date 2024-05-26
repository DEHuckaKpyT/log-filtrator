package noorg.dehucka.testtask;

import org.apache.commons.cli.*;

/**
 * Created on 25.05.2024.
 *
 * @author Denis Matytsin
 */
public class ArgsParser {
    private final CommandLine commandLine;

    private static final String MIN_ACCESSIBILITY_LEVEL_OPTION_NAME = "u";
    private static final String MAX_REQUEST_DURATION_OPTION_NAME = "t";

    public ArgsParser(String[] args) {
        Options options = new Options();

        Option input = new Option(MIN_ACCESSIBILITY_LEVEL_OPTION_NAME, true, "Минимально допустимый уровень доступности (проценты. Например, \"99.9\")");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option(MAX_REQUEST_DURATION_OPTION_NAME, true, "Приемлемое время ответа (миллисекунды. Например, \"45\")");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException("Ошибка во время разбора входных параметров", e);
        }
    }

    public float getMinAccessibilityLevel() {
        return Float.parseFloat(getArg(MIN_ACCESSIBILITY_LEVEL_OPTION_NAME));
    }

    public int getMaxRequestDuration() {
        return Integer.parseInt(getArg(MAX_REQUEST_DURATION_OPTION_NAME));
    }

    private String getArg(String option) {
        return commandLine.getOptionValue(option);
    }
}
