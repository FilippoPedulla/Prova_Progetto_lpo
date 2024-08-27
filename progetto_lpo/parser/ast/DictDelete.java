package progetto_lpo.parser.ast;
import progetto_lpo.visitors.Visitor;

// Rappresenta una cancellazione da un dizionario, come delete dict[key]
public class DictDelete implements Exp {
    private final Exp dict;
    private final Exp index;

    public DictDelete(Exp dict, Exp index) {
        this.dict = dict;
        this.index = index;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDictDelete(dict, index);
    }
}
