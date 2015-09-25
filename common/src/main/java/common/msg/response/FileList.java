package common.msg.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileList implements Serializable {

    private static final long serialVersionUID = 6453926456793760349L;
    
    private List<String> files;
    
    public FileList(final List<? extends String> files) {
        this.files = new ArrayList<>(files);
    }
    
    public List<String> getFiles() {
        return files;
    }
}
