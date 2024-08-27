package progetto_lpo.parser.ast;

import progetto_lpo.visitors.Visitor;
import static java.util.Objects.requireNonNull;

public class Dict implements Exp {
    private final Exp key;
    private final Exp value;

    public Dict(Exp key, Exp value) {
        this.key = requireNonNull(key);
        this.value = requireNonNull(value);
    }

    public Exp getKey() {
        return key;
    }

    public Exp getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDict(key,value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + key + " : " + value + "]";
    }
}

