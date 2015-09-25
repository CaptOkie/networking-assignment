package common.msg.response;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathChange implements Serializable {

    private static final long serialVersionUID = -6870172906497676745L;

    private String path;

    public PathChange(final Path path) {
        this.path = path.toString();
    }

    public Path getPath() {
        return Paths.get(path);
    }
}
