package stoner.tcache.common;

public class BaseType {

    private String type;

    private String desc;

    public BaseType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    protected void setDesc(String desc) {
        this.desc = desc;
    }
}
