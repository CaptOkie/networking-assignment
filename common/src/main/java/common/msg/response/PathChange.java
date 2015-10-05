package common.msg.response;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathChange implements Serializable {

    private static final long serialVersionUID = -6870172906497676745L;

    private String path;

    /**
     * @param path The path to set as the change.
     */
    public PathChange(final Path path) {
        this.path = path.toString();
    }

    /**
     * @return The path in the {@link PathChange}.
     */
    public Path getPath() {
        return Paths.get(path);
    }
}
