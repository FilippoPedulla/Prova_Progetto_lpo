package progetto_lpo.visitors;

import progetto_lpo.parser.ast.Block;
import progetto_lpo.parser.ast.Exp;
import progetto_lpo.parser.ast.Stmt;
import progetto_lpo.parser.ast.StmtSeq;
import progetto_lpo.parser.ast.Variable;

public interface Visitor<T> {
	T visitAdd(Exp left, Exp right);

	T visitAssignStmt(Variable var, Exp exp);

	T visitIntLiteral(int value);

	T visitEq(Exp left, Exp right);

	T visitNonEmptyStmtSeq(Stmt first, StmtSeq rest);

	T visitMul(Exp left, Exp right);

	T visitPrintStmt(Exp exp);

	T visitMyLangProg(StmtSeq stmtSeq);

	T visitSign(Exp exp);

	T visitVariable(Variable var); // only in this case more efficient then T visitVariable(String name)

	T visitEmptyStmtSeq();

	T visitVarStmt(Variable var, Exp exp);

	T visitNot(Exp exp);

	T visitAnd(Exp left, Exp right);

	T visitBoolLiteral(boolean value);

	T visitIfStmt(Exp exp, Block thenBlock, Block elseBlock);

	T visitBlock(StmtSeq stmtSeq);

	T visitPairLit(Exp left, Exp right);

	T visitFst(Exp exp);

	T visitSnd(Exp exp);
																//////////////////////////////////////////////////
	T visitForStmt(Variable var, Exp exp, Block block);

	T visitDict(Exp key, Exp value);

	T visitDictUpdate(Exp dict, Exp exp, Exp value);

	T visitDictDelete(Exp dict, Exp index);

	T visitDictAccess(Exp dict, Exp index);
}
