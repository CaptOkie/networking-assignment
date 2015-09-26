package common.msg.response;

import java.io.Serializable;

public class MakeDirectory implements Serializable {

    private static final long serialVersionUID = 6064997207963526895L;
    
    private final boolean success;
    
    public MakeDirectory(final boolean success) {
        this.success = success;
    }
    
    public boolean isSuccess() {
        return success;
    }
}
