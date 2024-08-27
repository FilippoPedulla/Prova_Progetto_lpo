package progetto_lpo.visitors.typechecking;

public class DictType implements Type{
    private final Type key;
	private final Type value;

    public static final String TYPE_NAME = "DICT";

    public DictType(Type key, Type value) {
        this.key = key;
        this.value = value;
    }

    public Type getFstType() {
        return key;
    }

    public Type getSndType() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof DictType pt)
            return key.equals(pt.key) && value.equals(pt.value);
        return false;
    }
}
