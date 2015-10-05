package common.msg.response;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileList implements Serializable {

    private static final long serialVersionUID = 6453926456793760349L;
    
    private SortedSet<String> files;
    
    /**
     * @param files All the files to list.
     */
    public FileList(final List<? extends String> files) {
        this.files = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.files.add(".");
        this.files.add("..");
        this.files.addAll(files);
    }
    
    /**
     * @return The ordered set of files.
     */
    public SortedSet<String> getFiles() {
        return files;
    }
}
