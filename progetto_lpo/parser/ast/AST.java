package progetto_lpo.parser.ast;

import progetto_lpo.visitors.Visitor;

public interface AST {
	<T> T accept(Visitor<T> visitor);
}
