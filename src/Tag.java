public enum Tag {
	
	// Uma representacao em constante de todos os nomes de tokens para a linguagem.
	
	// Operadores
	OP_EQ,
	OP_NE,
	OP_GT,
	OP_LT,
	OP_GE,
	OP_LE,
	OP_AD,
	OP_MIN,
	OP_MUL,
	OP_DIV,
	OP_ATRIB,
	
	// Simbolos
	SMB_OBC,
	SMB_CBC,
	SMB_OPA,
	SMB_CPA,
	SMB_COM,
	SMB_SEM,
	
	// Palavras-chave
	KW_PROGRAM,
	KW_IF,
	KW_ELSE,
	KW_WHILE,
	KW_WRITE,
	KW_READ,
	KW_NUM,
	KW_CHAR,
	KW_NOT,
	KW_OR,
	KW_AND,	
	
	// Identificadores
	ID,

	// Literal
	LIT,

	// Constantes
	NUM_CONST,
	CHAR_CONST,

	// Comentários
	COM_MULT_LINES,
	COM_ONE_LINE,

	// EOF
	EOF,

	// Tipo
	T_NUM,
	T_CHAR,
	T_BOOL,
	T_VOID,
	T_ERRO
	
}
