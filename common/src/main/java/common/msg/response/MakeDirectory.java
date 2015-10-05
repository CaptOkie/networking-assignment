package common.msg.response;

import java.io.Serializable;

public class MakeDirectory implements Serializable {

    private static final long serialVersionUID = 6064997207963526895L;
    
    private final boolean success;
    
    /**
     * @param success <code>true</code> if the directory was made, <code>false</code> otherwise.
     */
    public MakeDirectory(final boolean success) {
        this.success = success;
    }
    
    /**
     * @return <code>true</code> if the directory was made, <code>false</code> otherwise.
     */
    public boolean isSuccess() {
        return success;
    }
}
