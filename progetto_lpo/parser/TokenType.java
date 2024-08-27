package progetto_lpo.parser;


public enum TokenType {
	// used internally by the tokenizer, should never been accessed by the parser
	SYMBOL, KEYWORD, SKIP, 
	// non singleton categories
	IDENT, NUM,   
	// end-of-file
	EOF, 	
	// symbols
	ASSIGN, MINUS, PLUS, TIMES, NOT, AND, EQ, STMT_SEP, PAIR_OP, OPEN_PAR, CLOSE_PAR, OPEN_S_PAR, CLOSE_S_PAR, OPEN_BLOCK, CLOSE_BLOCK, DOUBLE_DOT,
	// keywords
	PRINT, VAR, BOOL, IF, ELSE, FST, SND, FOR, OF
}