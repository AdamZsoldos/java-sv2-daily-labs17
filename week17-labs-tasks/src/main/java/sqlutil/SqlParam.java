package sqlutil;

@SuppressWarnings("java:S6206")
public class SqlParam {

    private final int index;
    private final Object value;

    public static SqlParam of(int index, Object value) {
        return new SqlParam(index, value);
    }

    private SqlParam(int index, Object value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public Object getValue() {
        return value;
    }
}
