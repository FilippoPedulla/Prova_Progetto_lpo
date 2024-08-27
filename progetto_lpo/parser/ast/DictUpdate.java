package progetto_lpo.parser.ast;
import progetto_lpo.visitors.Visitor;


// Rappresenta un aggiornamento a un dizionario, come dict[key:value] dove in dict la chiave key viene aggiornata al valore value
public class DictUpdate implements Exp {
    private final Exp dict;
    private final Exp index;
    private final Exp value;

    public DictUpdate(Exp dict, Exp index, Exp value) {
        this.dict = dict;
        this.index = index;
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDictUpdate(dict, index, value);
    }


}


