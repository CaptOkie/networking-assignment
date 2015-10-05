package common.utils;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils {

    /**
     * Joins a list of objects together.
     * @param separator The separator to place in between the items.
     * @param items The items to join.
     * @return The newly created string.
     */
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
