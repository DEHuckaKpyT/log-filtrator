package noorg.dehucka.testtask;

/**
 * Created on 23.05.2024.
 *
 * @author Denis Matytsin
 */
public class Main {
    public static void main(String[] args) {
        ArgsParser parser = new ArgsParser(args);
        LogAnalyzer analyzer = new LogAnalyzer(new LogLineExtractor(),
                                               parser.getMinAccessibilityLevel(),
                                               parser.getMaxRequestDuration());

        analyzer.analyze();
    }
}
