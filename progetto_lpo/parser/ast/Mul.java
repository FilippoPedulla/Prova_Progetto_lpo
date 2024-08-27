package progetto_lpo.parser.ast;

import progetto_lpo.visitors.Visitor;

public class Mul extends BinaryOp {
	public Mul(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitMul(left, right);
	}
}
