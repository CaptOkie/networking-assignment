package common.utils;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils {

    public static String join(final String separator, final Collection<?> items) {
        
        final Iterator<?> iterator = items.iterator();        
        if (!iterator.hasNext()) {
            return "";
        }
        
        final StringBuilder builder = new StringBuilder(iterator.next().toString());
        while (iterator.hasNext()) {
            builder.append(separator);
            builder.append(iterator.next());
        }
        
        return builder.toString();
    }
}
