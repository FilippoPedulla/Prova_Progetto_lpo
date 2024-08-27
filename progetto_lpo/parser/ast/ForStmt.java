package progetto_lpo.parser.ast;

import static java.util.Objects.requireNonNull;

import progetto_lpo.visitors.Visitor;

public class ForStmt implements Stmt {
	private final Variable var;
	private final Exp exp;
	private final Block block;

	public ForStmt(Variable var, Exp exp, Block block) {
		this.var = requireNonNull(var);
		this.exp = requireNonNull(exp);
		this.block = requireNonNull(block);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + var + "," + exp + "," + block + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitForStmt(var, exp, block);
	}

}