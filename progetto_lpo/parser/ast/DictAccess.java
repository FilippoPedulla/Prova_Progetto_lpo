package progetto_lpo.parser.ast;
import progetto_lpo.visitors.Visitor;

// Rappresenta un accesso a un dizionario, come dict[key]
public class DictAccess implements Exp {
    private final Exp dict;
    private final Exp index;

    public DictAccess(Exp dict, Exp index) {
        this.dict = dict;
        this.index = index;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDictAccess(dict, index);
    }
}

