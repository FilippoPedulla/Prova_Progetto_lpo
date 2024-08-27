package progetto_lpo.parser;

import progetto_lpo.parser.ast.Prog;

public interface Parser extends AutoCloseable {

	Prog parseProg() throws ParserException;

}