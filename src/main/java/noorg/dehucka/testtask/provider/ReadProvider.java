package noorg.dehucka.testtask.provider;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * Created on 27.05.2024.
 *
 * @author Denis Matytsin
 */
public class ReadProvider {

    @NotNull
    public BufferedReader getReader() throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}
