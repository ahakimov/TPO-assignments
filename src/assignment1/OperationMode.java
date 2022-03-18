package assignment1;

public enum OperationMode {
    Write("WRITE", 1),
    Read("READ", 0);
    private final String value_;
    private final int mark_;

    private  OperationMode(String value, int mark) {
        value_ = value;
        mark_ = mark;
    }
    public static OperationMode getMode(String value) {
        if (value.equalsIgnoreCase(Write.getValue())) {
            return Write;
        } else if (value.equalsIgnoreCase(Read.getValue())) {
            return Read;
        } else {
            return null;
        }
    }
    public String getValue() { return value_; }
    public int getMark() {return mark_; }
}
