package noorg.dehucka.testtask.provider;

import org.jetbrains.annotations.Nullable;

/**
 * Created on 27.05.2024.
 *
 * @author Denis Matytsin
 */
public class WriteProvider {

    public void write(@Nullable String text) {
        System.out.println(text);
    }
}
