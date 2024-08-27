
package progetto_lpo.parser;
import java.io.IOException;
import javax.lang.model.element.VariableElement;

import progetto_lpo.parser.ast.*;

import static java.util.Objects.requireNonNull;
import static progetto_lpo.parser.TokenType.*;

/*
Prog ::= StmtSeq EOF
StmtSeq ::= Stmt (';' StmtSeq)?
Stmt ::= 'var'? IDENT '=' Exp | 'print' Exp |  'if' '(' Exp ')' Block ('else' Block)? | 'for' '(' 'var' IDENT 'of Exp ')' Block 
Block ::= '{' StmtSeq '}'
Exp ::= And (',' And)* 
And ::= Eq ('&&' Eq)* 
Eq ::= Add ('==' Add)*
Add ::= Mul ('+' Mul)*
Mul::= Unary ('*' Unary)*
Unary ::= 'fst' Unary | 'snd' Unary | '-' Unary | '!' Unary | Dict 
Dict ::= Atom ('[' Exp (':' Exp?)? ']')* 
Atom :: = '[' Exp ':' Exp ']' | BOOL | NUM | IDENT | '(' Exp ')'
*/

public class MyLangParser implements Parser {

	private final MyLangTokenizer tokenizer; // the tokenizer used by the parser

	/*
	 * reads the next token through the tokenizer associated with the
	 * parser; TokenizerExceptions are chained into corresponding ParserExceptions
	 */
	private void nextToken() throws ParserException {
		try {
			tokenizer.next();
		} catch (TokenizerException e) {
			throw new ParserException(e);
		}
	}

	// decorates error message with the corresponding line number
	private String lineErrMsg(String msg) {
		return String.format("on line %s: %s", tokenizer.getLineNumber(), msg);
	}

	/*
	 * checks whether the token type of the currently recognized token matches
	 * 'expected'; if not, it throws a corresponding ParserException
	 */
	private void match(TokenType expected) throws ParserException {
		final var found = tokenizer.tokenType();
		if (found != expected)
			throw new ParserException(
					lineErrMsg(String.format("Expecting %s, found %s('%s')", expected, found, tokenizer.tokenString())));
	}

	/*
	 * checks whether the token type of the currently recognized token matches
	 * 'expected'; if so, it reads the next token, otherwise it throws a
	 * corresponding ParserException
	 */
	private void consume(TokenType expected) throws ParserException {
		match(expected);
		nextToken();
	}

	// throws a ParserException because the current token was not expected
	private <T> T unexpectedTokenError() throws ParserException {
		throw new ParserException(lineErrMsg(
				String.format("Unexpected token %s ('%s')", tokenizer.tokenType(), tokenizer.tokenString())));
	}

	// associates the parser with a corresponding non-null  tokenizer
	public MyLangParser(MyLangTokenizer tokenizer) {
		this.tokenizer = requireNonNull(tokenizer);
	}

	/*
	* parses a program Prog ::= StmtSeq EOF
	*/
	@Override
	public Prog parseProg() throws ParserException {
		nextToken(); // one look-ahead symbol
		final var prog = new MyLangProg(parseStmtSeq());
		match(EOF); // last token must have type EOF
		return prog;
	}

	@Override
	public void close() throws IOException {
		if (tokenizer != null)
			tokenizer.close();
	}

	/*
	* parses a non empty sequence of statements, binary operator STMT_SEP is right
	* associative StmtSeq ::= Stmt (';' StmtSeq)?
	*/
	private StmtSeq parseStmtSeq() throws ParserException {
		final var stmt = parseStmt();
		if (tokenizer.tokenType() == STMT_SEP) {
			nextToken();
			return new NonEmptyStmtSeq(stmt, parseStmtSeq());
		}
		return new NonEmptyStmtSeq(stmt, new EmptyStmtSeq());
	}

	/*
	* parses a statement 
	* Stmt ::= 'var'? IDENT '=' Exp | 'print' Exp |  'if' '(' Exp ')' Block ('else' Block)? | 'for' '(' 'var' IDENT 'of Exp ')' Block
	*/
	private Stmt parseStmt() throws ParserException {
		return switch (tokenizer.tokenType()) {
		case PRINT -> parsePrintStmt();
		case VAR -> parseVarStmt();
		case IDENT -> parseAssignStmt();
		case IF -> parseIfStmt();
		case FOR -> parseForStmt();
		default -> unexpectedTokenError();
		};
	}

	/*
	* parses the 'print' statement Stmt ::= 'print' Exp
	*/
	private PrintStmt parsePrintStmt() throws ParserException {
		consume(PRINT);
		final var exp = parseExp();
		return new PrintStmt(exp);
	}

	/*
	* parses the 'var' statement Stmt ::= 'var' IDENT '=' Exp
	*/
	private VarStmt parseVarStmt() throws ParserException {
		consume(VAR);
		Variable var = parseVariable();
		consume(ASSIGN);
		return new VarStmt(var,parseExp());
	}

	/*
	* parses the assignment statement Stmt ::= IDENT '=' Exp
	*/
	private AssignStmt parseAssignStmt() throws ParserException {
		Variable var = parseVariable();
		consume(ASSIGN);
		return new AssignStmt(var, parseExp());
	}

	/*
	* parses the 'if' statement Stmt ::= 'if' '(' Exp ')' Block ('else' Block)?
	*/
	private IfStmt parseIfStmt() throws ParserException {
		consume(IF);
		consume(OPEN_PAR);
		final var exp = parseExp();
		consume(CLOSE_PAR);
		final var block = parseBlock();
		if (tokenizer.tokenType() == ELSE) {
			consume(ELSE);
			final var else_block = parseBlock();
			return new IfStmt(exp, block, else_block);
		}
		return new IfStmt(exp, block);
	}

	/*
	* parses the 'for' statement Stmt ::= 'for' '(' 'var' IDENT 'of Exp ')' Block
	*/
	private ForStmt parseForStmt() throws ParserException {
		consume(FOR);
		consume(OPEN_PAR);
		consume(VAR);
		Variable var = parseVariable();
		consume(OF);
		final var exp = parseExp();
		consume(CLOSE_PAR);
		final var block = parseBlock();
		return new ForStmt(var, exp, block);
	}

	/*
	* parses a block of statements Block ::= '{' StmtSeq '}'
	*/
	private Block parseBlock() throws ParserException {
		consume(OPEN_BLOCK);
		final var exp = parseStmtSeq();
		consume(CLOSE_BLOCK);
		return new Block(exp);
	}

	/*
	* parses expressions, starting from the lowest precedence operator PAIR_OP
	* which is left-associative Exp ::= And (',' And)*
	*/
	private Exp parseExp() throws ParserException {
		var exp = parseAnd();
		while (tokenizer.tokenType() == PAIR_OP) {
			nextToken();
			exp = new PairLit(exp, parseAnd());
		}
		return exp;
	}

	/*
	* parses expressions, starting from the lowest precedence operator AND which is
	* left-associative And ::= Eq ('&&' Eq)*
	*/
	private Exp parseAnd() throws ParserException {
		var exp = parseEq();
		while (tokenizer.tokenType() == AND) {
			nextToken();
			exp = new And(exp, parseEq());
		}
		return exp;
	}

	/*
	* parses expressions, starting from the lowest precedence operator EQ which is
	* left-associative Eq ::= Add ('==' Add)*
	*/
	private Exp parseEq() throws ParserException {
		var exp = parseAdd();
		while (tokenizer.tokenType() == EQ) {
			nextToken();
			exp = new Eq(exp, parseAdd());
		}
		return exp;
	}

	/*
	* parses expressions, starting from the lowest precedence operator PLUS which
	* is left-associative Add ::= Mul ('+' Mul)*
	*/
	private Exp parseAdd() throws ParserException {
		var exp = parseMul();
		while (tokenizer.tokenType() == PLUS) {
			nextToken();
			exp = new Add(exp, parseMul());
		}
		return exp;
	}

	/*
	* Mul::= Unary ('*' Unary)*
	*/
	private Exp parseMul() throws ParserException {
		var exp = parseUnary();
		while (tokenizer.tokenType() == TIMES) {
			nextToken();
			exp = new Mul(exp, parseUnary());
		}
		return exp;
	}

	/*
	* parses expressions of type Unary 
	* Unary ::= 'fst' Unary | 'snd' Unary | '-' Unary | '!' Unary | Dict
	*/
	private Exp parseUnary() throws ParserException {
		return switch (tokenizer.tokenType()) {
			case MINUS -> parseMinus();  // Parsing del segno meno unario
			case NOT -> parseNot();  // Parsing della negazione unaria
			case FST -> parseFst();  // Parsing della funzione 'fst'
			case SND -> parseSnd();  // Parsing della funzione 'snd'
			
			// questo è sbagliato perche per andare in dict non si avra mai una parentesi quadra almeno credo
			// una soluzione potrebbe essere che se non sono presenti fst, snd, - o ! allora si va in parseDict() di default 
			//case OPEN_S_PAR -> parseDict();  // Parsing del dizionario (parsing della struttura con '[')
			
			//default -> parseAtom();  // In assenza di operatori unari, considera l'espressione come un atom
			default -> parseDict();
		};
	}

	/*
	* parses expressions of type Atom 
	* Atom :: = '[' Exp ':' Exp ']' | BOOL | NUM | IDENT | '(' Exp ')'
	*/
	private Exp parseAtom() throws ParserException {
		return switch (tokenizer.tokenType()) {
		case OPEN_S_PAR -> parseSquarePar();
		case BOOL -> parseBoolean();
		case NUM -> parseNum();
		case IDENT -> parseVariable();
		case OPEN_PAR -> parseRoundPar();
		default -> unexpectedTokenError();
		};
	}

	//Dict ::= Atom ('[' Exp (':' Exp?)? ']')*
	private Exp parseDict() throws ParserException {
		var exp = parseAtom();
		while (tokenizer.tokenType() == OPEN_S_PAR){
			consume(OPEN_S_PAR);
			Exp index = parseExp();
			if (tokenizer.tokenType() == DOUBLE_DOT) {
				consume(DOUBLE_DOT);
				// se ho la parentesi quadra chiusa allora ho una cancellazione
				if (tokenizer.tokenType() == CLOSE_S_PAR) {
					consume(CLOSE_S_PAR);
					exp = new DictDelete(exp, index);
				}
				else{
					//Exp value = parseExp();
					exp = new DictUpdate(exp ,index, parseExp());
					consume(CLOSE_S_PAR);
				}
			}else{
				exp = new DictAccess(exp, index);
				consume(CLOSE_S_PAR);
			}
		}
		return exp;
	}

	// parses number literals
	private IntLiteral parseNum() throws ParserException {
		final var val = tokenizer.intValue();
		consume(NUM);
		return new IntLiteral(val);
	}

	// parses boolean literals
	private BoolLiteral parseBoolean() throws ParserException {
		final var val = tokenizer.boolValue();
		consume(BOOL);
		return new BoolLiteral(val);
	}

	// parses variable identifiers
	private Variable parseVariable() throws ParserException {
		final var variable = tokenizer.tokenString();
		consume(IDENT);
		return new Variable(variable);
	}

	/*
	* parses expressions with unary operator MINUS Atom ::= '-' Atom
	*/
	private Sign parseMinus() throws ParserException {
		consume(MINUS);
		final var exp = new Sign(parseAtom());
		return exp;
	}

	/*
	* parses expressions with unary operator FST Atom ::= 'fst' Atom
	*/
	private Fst parseFst() throws ParserException {
		consume(FST);
		final var exp = new Fst(parseAtom());
		return exp;
	}

	/*
	* parses expressions with unary operator SND Atom ::= 'snd' Atom
	*/
	private Snd parseSnd() throws ParserException {
		consume(SND);
		final var exp = new Snd(parseAtom());
		return exp;
	}

	/*
	* parses expressions with unary operator NOT Atom ::= '!' Atom
	*/
	private Not parseNot() throws ParserException {
		consume(NOT);
		final var exp = new Not(parseAtom());
		return exp;
	}

	/*
	* parses expressions delimited by parentheses Atom ::= '(' Exp ')'
	*/
	private Exp parseRoundPar() throws ParserException {
		consume(OPEN_PAR); // this check is necessary for parsing correctly the 'if' statement
		final var exp = parseExp();
		consume(CLOSE_PAR);
		return exp;
	}

	/*
	* parses expressions delimited by parentheses Atom :: = '[' Exp ':' Exp ']'
	*/
	private Exp parseSquarePar() throws ParserException {
		consume(OPEN_S_PAR);  // Consuma '['
		final var key = parseExp();  // Parsing della prima espressione, che rappresenta la chiave
		consume(DOUBLE_DOT);  // Consuma ':'
		final var value = parseExp();  // Parsing della seconda espressione, che rappresenta il valore
		consume(CLOSE_S_PAR);  // Consuma ']'
		return new DictLiteral(key, value);  // Ritorna un dizionario letterale
	}

}
