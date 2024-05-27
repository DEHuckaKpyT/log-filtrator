package noorg.dehucka.testtask;

import noorg.dehucka.testtask.provider.ReadProvider;
import noorg.dehucka.testtask.provider.WriteProvider;

/**
 * Created on 23.05.2024.
 *
 * @author Denis Matytsin
 */
public class App {
    public static void main(String[] args) {
        ArgsParser parser = new ArgsParser(args);
        LogAnalyzer analyzer = new LogAnalyzer(new LogLineExtractor(),
                                               new ReadProvider(),
                                               new WriteProvider(),
                                               parser.getMinAccessibilityLevel(),
                                               parser.getMaxRequestDuration());

        analyzer.analyze();
    }
}
