public class Parser {
	
	Lexer lexer;
	Token token;	
	
	public Parser(Lexer lexer) throws Exception {
		this.lexer = lexer;
		this.token = lexer.proxToken();
		
		if (this.token == null) {
			throw new Exception("Nenhum token reconhecido");
		}
				
	}
	
	private void sinalizaErroSemantico(String mensagem) {
		System.out.println("[Erro Semantico] na linha " + token.getLinha() + " e coluna " + token.getColuna() + ": ");
		System.out.println(mensagem + "\n");
	}
	
	private void sinalizaErroSintatico(String mensagem) {
		System.out.println("[Erro Sintatico] na linha " + token.getLinha() + " e coluna " + token.getColuna() + ": ");
		System.out.println(mensagem + "\n");
	}
	
	private void advance() throws Exception {
		System.out.println("[DEBUG] token: " + token.toString());
		token = lexer.proxToken();
		if (this.token == null) {
			throw new Exception("Mais que 3 erros foram encontrados, abortando execução.");
		}
	}
	
	private void skip(String mensagem) throws Exception {
		this.sinalizaErroSintatico(mensagem);
		this.advance();
	}
	
	private boolean eat(Tag t) throws Exception {
        while(token.getNome() == Tag.COM_MULT_LINES || token.getNome() == Tag.COM_ONE_LINE) {
            this.advance();
        }
		if(token.getNome() == t) {
			this.advance();
			return true;
		} else {
			return false;
		}
	}
	
    //prog → “program” “id” body
    public void prog() throws Exception {

        Token tempToken = token;

        if(!this.eat(Tag.KW_PROGRAM)) {
            throw new Exception("Esperado \"program\", encontrado: " + token.getLexema());
        }
        if (this.eat(Tag.ID)) {
            tempToken.setTipo(Tag.T_VOID);
            lexer.ts.addToken(tempToken.getLexema(), tempToken);
        }
        else {
            throw new Exception("Esperado \"identificador\", encontrado: " + token.getLexema());
        }
        
        this.body();
    }

    //body → decl-list “{“ stmt-list “}” 
    private void body() throws Exception {
        this.declList();
        if(!this.eat(Tag.SMB_OBC)) {
            throw new Exception("Esperado \"{\", encontrado: " + token.getLexema());
        }
        this.stmtList();
        if(!this.eat(Tag.SMB_CBC)) {
            throw new Exception("Esperado \"}\", encontrado: " + token.getLexema());
        }
    }

    //decl-list → decl “;” decl-list  | ε 
    private void declList() throws Exception {
        if (this.token.getNome() == Tag.KW_NUM || this.token.getNome() == Tag.KW_CHAR) {           
            this.decl();
            if(!this.eat(Tag.SMB_SEM)) {
                throw new Exception("Esperado \";\", encontrado: " + token.getLexema());
            }            
            this.declList();
        }
        else if (this.token.getNome() == Tag.SMB_OBC) {
            return;
        }
    }

    //decl → type id-list 
    private void decl() throws Exception{
        No noType = this.type();
        this.idList(noType);
    }

    //type → “num” | “char” 
    private No type() throws Exception {
        No noType = new No();
        if (this.eat(Tag.KW_NUM)) {
            noType.tipo = Tag.T_NUM;
        }
        else if(this.eat(Tag.KW_CHAR)) {
            noType.tipo = Tag.T_CHAR;
        }
        else {
            throw new Exception("Esperado \"numero, char\", encontrado: " + token.getLexema());
        }
        return noType;
    }

    //id-list → “id” id-list’
    private void idList(No noType) throws Exception {
        Token tempToken = token;

        if (this.eat(Tag.ID)) {
            tempToken.setTipo(noType.tipo);
            lexer.ts.addToken(tempToken.getLexema(), tempToken);
        }
        else {
            throw new Exception("Esperado \"identificador\", encontrado: " + token.getLexema());
        }
        this.idListLinha(noType);
    }

    //id-list’ → “,” id-list  | ε 
    private void idListLinha(No noType) throws Exception {
        if (this.eat(Tag.SMB_COM)) {
            this.idList(noType);
        }
        else if (this.token.getNome() == Tag.SMB_SEM) {
            return;
        }
        else {
            throw new Exception("Esperado \", ou ;\", encontrado: " + token.getLexema());
        }
    }

    //stmt-list → stmt “;” stmt-list  | ε 
    private void stmtList() throws Exception {
        if (this.token.getNome() == Tag.ID || this.token.getNome() == Tag.KW_IF || this.token.getNome() == Tag.KW_WHILE
            || this.token.getNome() == Tag.KW_READ || this.token.getNome() == Tag.KW_WRITE) {
            this.stmt();
            if (!this.eat(Tag.SMB_SEM)) {
                throw new Exception("Esperado \";\", encontrado: " + token.getLexema());
            }
            this.stmtList();
        }
        else if (this.token.getNome() != Tag.SMB_CBC) {
            return;
        }
    }

    //stmt → assign-stmt  | if-stmt  | while-stmt  | read-stmt  | write-stmt 
    private void stmt() throws Exception{
        if (this.token.getNome() == Tag.ID) {
            this.assignStmt();
        }
        else if (this.token.getNome() == Tag.KW_IF) {
            this.ifStmt();
        }
        else if (this.token.getNome() == Tag.KW_WHILE) {
            this.whileStmt();
        }
        else if (this.token.getNome() == Tag.KW_READ) {
            this.readStmt();
        }
        else if (this.token.getNome() == Tag.KW_WRITE) {
            this.writeStmt();
        }
        else {
            throw new Exception("Esperado \"identificador, if, while, read ou write\", encontrado: " + token.getLexema());
        }
    }

    //assign-stmt → “id” “=” simple_expr 
    private void assignStmt() throws Exception {

        Token tempToken = token;

        if (this.eat(Tag.ID)) {
            if (lexer.ts.getToken(tempToken.getLexema()).getTipo() == Tag.T_VOID) {
                throw new Exception("ID não declarado");
            }
        }
        else {
            throw new Exception("Esperado \"identificador\", encontrado: " + token.getLexema());
        }
        if (!this.eat(Tag.OP_ATRIB)) {
            throw new Exception("Esperado \"=\", encontrado: " + token.getLexema());
        }

        No noSimpleExpr = this.simpleExpr();

        if (noSimpleExpr.tipo != lexer.ts.getToken(tempToken.getLexema()).getTipo() ) {
            throw new Exception("atribuição incompatível");
        }

    }

    //if-stmt → “if” “(“ expression “)” “{“ stmt-list “}” if-stmt’ 
    private void ifStmt() throws Exception {
        if (!this.eat(Tag.KW_IF)) {
            throw new Exception("Esperado \"if\", encontrado: " + token.getLexema());
        }
        if (!this.eat(Tag.SMB_OPA)) {
            throw new Exception("Esperado \"(\", encontrado: " + token.getLexema());
        }
        No noExpression = this.expression();
        if (!this.eat(Tag.SMB_CPA)) {
            throw new Exception("Esperado \")\", encontrado: " + token.getLexema());
        }

        if (noExpression.tipo != Tag.T_BOOL) {
            throw new Exception("expressão lógica mal formada}");
        }

        if (!this.eat(Tag.SMB_OBC)) {
            throw new Exception("Esperado \"{\", encontrado: " + token.getLexema());
        }
        this.stmtList();
        if (!this.eat(Tag.SMB_CBC)) {
            throw new Exception("Esperado \"}\", encontrado: " + token.getLexema());
        }
        this.ifStmtLinha();
    }

    //if-stmt’ → “else” “{“ stmt-list “}”  | ε 
    private void ifStmtLinha() throws Exception {
        if (this.token.getNome() == Tag.KW_ELSE) {
            if (!this.eat(Tag.KW_ELSE)) {
                throw new Exception("Esperado \"else\", encontrado: " + token.getLexema());
            }
            if (!this.eat(Tag.SMB_OBC)) {
                throw new Exception("Esperado \"{\", encontrado: " + token.getLexema());
            }
            this.stmtList();
            if (!this.eat(Tag.SMB_CBC)) {
                throw new Exception("Esperado \"}\", encontrado: " + token.getLexema());
            }
        }
        else if (this.token.getNome() == Tag.SMB_SEM) {
            return;
        }
        else {
            throw new Exception("Esperado \"else ou ;\", encontrado: " + token.getLexema());
        }
    }

    //while-stmt → stmt-prefix “{“ stmt-list “}” 
    private void whileStmt() throws Exception{
        this.stmtPrefix();
        if (!this.eat(Tag.SMB_OBC)) {
            throw new Exception("Esperado \"{\", encontrado: " + token.getLexema());
        }
        this.stmtList();
        if (!this.eat(Tag.SMB_CBC)) {
            throw new Exception("Esperado \"}\", encontrado: " + token.getLexema());
        }
    }

    //stmt-prefix → “while” “(“ expression “)” 
    private void stmtPrefix() throws Exception {
        if (!this.eat(Tag.KW_WHILE)) {
            throw new Exception("Esperado \"while\", encontrado: " + token.getLexema());
        }
        if (!this.eat(Tag.SMB_OPA)) {
            throw new Exception("Esperado \"(\", encontrado: " + token.getLexema());
        }
        No noExpression = this.expression();
        if (!this.eat(Tag.SMB_CPA)) {
            throw new Exception("Esperado \")\", encontrado: " + token.getLexema());
        }

        if (noExpression.tipo != Tag.T_BOOL) {
            throw new Exception("expressão lógica mal formada}");
        }
    }

    //read-stmt → “read” “id” 
    private void readStmt() throws Exception {
        if (!this.eat(Tag.KW_READ)) {
            throw new Exception("Esperado \"read\", encontrado: " + token.getLexema());
        }

        Token tempToken = token;

        if (this.eat(Tag.ID)) {
            if (lexer.ts.getToken(tempToken.getLexema()).getTipo() == Tag.T_VOID) {
                throw new Exception("ID não declarado");
            }
        }
        else {
            throw new Exception("Esperado \"identificador\", encontrado: " + token.getLexema());
        }
    }

    //write-stmt → “write” simple-expr 
    private void writeStmt() throws Exception {
        if (!this.eat(Tag.KW_WRITE)) {
            throw new Exception("Esperado \"write\", encontrado: " + token.getLexema());
        }
        No noSimpleExpr = this.simpleExpr();

        if (noSimpleExpr.tipo != Tag.T_CHAR && noSimpleExpr.tipo != Tag.T_NUM) {
            throw new Exception("incompatibilidade para impressão de valores");
        }
    }

    //expression → simple-expr expression’ 
    private No expression() throws Exception {

        No noType = new No();

        No noSimpleExpr = this.simpleExpr();
        No noExpressionLinha = this.expressionLinha();

        if (noExpressionLinha.tipo == Tag.T_VOID) {
            noType.tipo = noSimpleExpr.tipo;
        }
        else if (noExpressionLinha.tipo == noSimpleExpr.tipo && noSimpleExpr.tipo == Tag.T_BOOL) {
            noType.tipo = Tag.T_BOOL;
        }
        else {
            noType.tipo = Tag.T_ERRO;
        }

        return noType;
    }

    //expression’ → logop simple-expr expression’  | ε 
    private No expressionLinha() throws Exception {

        No noType = new No();

        if (this.token.getNome() == Tag.KW_OR || this.token.getNome() == Tag.KW_AND) {
            this.logop();
            No noSimpleExpr = this.simpleExpr();
            No noExpressionLinha = this.expressionLinha();

            if(noExpressionLinha.tipo == Tag.T_VOID && noSimpleExpr.tipo == Tag.T_BOOL) {
                noType.tipo = Tag.T_BOOL;
            }
            else if (noExpressionLinha.tipo == noSimpleExpr.tipo && noSimpleExpr.tipo == Tag.T_BOOL) {
                noType.tipo = Tag.T_BOOL;
            }
            else {
                noType.tipo = Tag.T_ERRO;
            }
    
            return noType;

        }
        else if (this.token.getNome() == Tag.SMB_CPA) {
            noType.tipo = Tag.T_VOID;
            return noType;
        }
        else {
            throw new Exception("Esperado \"or | and | )\", encontrado: " + token.getLexema());
        }
    }

    //simple-expr → term simple-exp’
    private No simpleExpr() throws Exception {

        No noType = new No();

        No noTerm = this.term();
        No noSimpleExprLinha = this.simpleExprLinha();

        if (noSimpleExprLinha.tipo == Tag.T_VOID) {
            noType.tipo = noTerm.tipo;
        }
        else if(noSimpleExprLinha.tipo == noTerm.tipo && noTerm.tipo == Tag.T_NUM) {
            noType.tipo = Tag.T_NUM;
        }
        else {
            noType.tipo = Tag.T_ERRO;
        }

        return noType;
    }

    //simple-exp’ → relop term simple-exp’  | ε
    private No simpleExprLinha() throws Exception {

        No noType = new No();

        if (this.token.getNome() == Tag.OP_EQ || this.token.getNome() == Tag.OP_GT || this.token.getNome() == Tag.OP_GE 
        || this.token.getNome() == Tag.OP_LT || this.token.getNome() == Tag.OP_LE || this.token.getNome() == Tag.OP_NE) {
            this.relop();
            No noTerm = this.term();
            No noSimpleExprLinha = this.simpleExprLinha();

            if(noSimpleExprLinha.tipo == Tag.T_VOID && noTerm.tipo == Tag.T_NUM) {
                noType.tipo = Tag.T_NUM;
            }
            else if (noSimpleExprLinha.tipo == noTerm.tipo && noTerm.tipo == Tag.T_NUM) {
                noType.tipo = Tag.T_NUM;
            }
            else {
                noType.tipo = Tag.T_ERRO;
            }

            return noType;
        }
        else if (this.token.getNome() == Tag.KW_OR || this.token.getNome() == Tag.KW_AND || this.token.getNome() == Tag.SMB_CPA || this.token.getNome() == Tag.SMB_SEM) {
            noType.tipo = Tag.T_VOID;
            return noType;
        }
        else {
            throw new Exception("Esperado \"== | > | >= | < | <= | != | or | and | ) | ;\", encontrado: " + token.getLexema());
        }
    }
    //term → factor-b term’
    private No term() throws Exception {

        No noTypo = new No();

        No noFactorB = this.factorB();
        No noTermLinha = this.termLinha();

        if(noTermLinha.tipo == Tag.T_VOID) {
            noTypo.tipo = noFactorB.tipo;
        }
        else if(noTermLinha.tipo == noFactorB.tipo && noTermLinha.tipo == Tag.T_NUM) {
            noTypo.tipo = Tag.T_NUM;
        }
        else {
            noTypo.tipo = Tag.T_ERRO;
        }

        return noTypo;
    }

    //term’ → addop factor-b term’  | ε 
    private No termLinha() throws Exception {

        No noType = new No();

        if (this.token.getNome() == Tag.OP_AD || this.token.getNome() == Tag.OP_MIN) {
            this.addop();
            No noFactorB = this.factorB();
            No noTermLinha = this.termLinha();

            if (noTermLinha.tipo == Tag.T_VOID && noFactorB.tipo == Tag.T_NUM) {
                noType.tipo = Tag.T_NUM;
            }
            else if (noTermLinha.tipo == noFactorB.tipo && noFactorB.tipo == Tag.T_NUM) {
                noType.tipo = Tag.T_NUM;
            }
            else {
                noType.tipo = Tag.T_ERRO;
            }

            return noType;
        }
        else if (this.token.getNome() == Tag.OP_EQ || this.token.getNome() == Tag.OP_GT || this.token.getNome() == Tag.OP_GE 
        || this.token.getNome() == Tag.OP_LT || this.token.getNome() == Tag.OP_LE || this.token.getNome() == Tag.OP_NE
        || this.token.getNome() == Tag.KW_OR || this.token.getNome() == Tag.KW_AND || this.token.getNome() == Tag.SMB_CPA || this.token.getNome() == Tag.SMB_SEM) {
            noType.tipo = Tag.T_VOID;
            return noType;
        }
        else {
            throw new Exception("Esperado \"+ | - | == | > | >= | < | <= | != | or | and | ;\", encontrado: " + token.getLexema());
        }
    }

    //factor-b → factor-a factor-b’
    private No factorB() throws Exception {

        No noType = new No();

        No noFactorA = this.factorA();
        No noFactorBLinha = this.factorBLinha();

        if(noFactorBLinha.tipo == Tag.T_VOID) {
            noType.tipo = noFactorA.tipo;
        }
        else if (noFactorBLinha.tipo == noFactorA.tipo && noFactorBLinha.tipo == Tag.T_NUM) {
            noType.tipo = Tag.T_NUM;
        }
        else {
            noType.tipo = Tag.T_ERRO;
        }

        return noType;

    }

    //factor-b’ → mulop factor-a factor-b’  | ε 
    private No factorBLinha() throws Exception {

        No noType = new No();

        if (this.token.getNome() == Tag.OP_MUL || this.token.getNome() == Tag.OP_DIV) {
            this.mulop();
            No noFactorA = this.factorA();
            No nofactorBLinha = this.factorBLinha();

            if (nofactorBLinha.tipo == Tag.T_VOID && noFactorA.tipo == Tag.T_NUM) {
                noType.tipo = Tag.T_NUM;
            }
            else if (nofactorBLinha.tipo == noFactorA.tipo && noFactorA.tipo == Tag.T_NUM) {
                noType.tipo = Tag.T_NUM;
            }
            else {
                noType.tipo = Tag.T_ERRO;
            }

            return noType;
        }
        else if (this.token.getNome() == Tag.OP_AD || this.token.getNome() == Tag.OP_MIN || this.token.getNome() == Tag.OP_EQ || this.token.getNome() == Tag.OP_GT || this.token.getNome() == Tag.OP_GE || this.token.getNome() == Tag.OP_LT || this.token.getNome() == Tag.OP_LE || this.token.getNome() == Tag.OP_NE || this.token.getNome() == Tag.KW_OR || this.token.getNome() == Tag.KW_AND || this.token.getNome() == Tag.SMB_CPA || this.token.getNome() == Tag.SMB_SEM) {
            noType.tipo = Tag.T_VOID;
            return noType;
        }
        else {
            throw new Exception("Esperado \"* | / | + | - | == | > | >= | < | <= | != | or | and | ) | ;\", encontrado: " + token.getLexema());
        }
    }

    //factor-a → factor | "not" factor
    private No factorA() throws Exception {

        No noType = new No();

        if (this.token.getNome() == Tag.ID || this.token.getNome() == Tag.NUM_CONST || this.token.getNome() == Tag.CHAR_CONST 
        || this.token.getNome() == Tag.SMB_OPA) {
            No noFactor = this.factor();
            noType.tipo = noFactor.tipo;
            return noType;
        }
        else if(eat(Tag.KW_NOT)) {
            No noFactor = this.factor();

            if (noFactor.tipo != Tag.T_BOOL) {
                noType.tipo = Tag.T_ERRO;
                throw new Exception("Expressão lógica mal formada");
            }
            else {
                noType.tipo = Tag.T_BOOL;
            }
            return noType;
        }
        else {
            throw new Exception("Esperado \"identificador | num_const | char_const | ( | not\", encontrado: " + token.getLexema());
        }
    }

    //factor → “id”  | constant  | “(“ expression “)” 
    private No factor() throws Exception {

        No noType = new No();

        Token tempToken = token;
        if (this.eat(Tag.ID)) {
            noType.tipo = tempToken.getTipo();
            return noType;
        }
        else if (this.token.getNome() == Tag.NUM_CONST || this.token.getNome() == Tag.CHAR_CONST) {
            No noConstant = this.constant();
            noType.tipo = noConstant.tipo;
            return noType;
        }
        else if (this.eat(Tag.SMB_OPA)) {
            No noExpression = this.expression();

            if (this.eat(Tag.SMB_CPA)) {
                noType.tipo = noExpression.tipo;
            }
            else {
                throw new Exception("Esperado \")\", encontrado: " + token.getLexema());
            }
            return noType;
        }
        else {
            throw new Exception("Esperado \"identificador | num_const | char_const | (\", encontrado: " + token.getLexema());
        }
    }

    //logop → “or”  | “and” 
    private void logop() throws Exception {
        if (!this.eat(Tag.KW_OR) && !this.eat(Tag.KW_AND)) {
            throw new Exception("Esperado \"or | and\", encontrado: " + token.getLexema());
        }
    }

    //relop → “==” | “>”  | “>=”  | “<”  | “<=”  | “!=” 
    private void relop() throws Exception {
        if (!this.eat(Tag.OP_EQ) && !this.eat(Tag.OP_GT) && !this.eat(Tag.OP_GE) && !this.eat(Tag.OP_LT)
        && !this.eat(Tag.OP_LE) && !this.eat(Tag.OP_NE)) {
            throw new Exception("Esperado \"== | > | >= | < | <= | !=\", encontrado: " + token.getLexema());
        }
    }

    //addop → “+”  | “-” 
    private void addop() throws Exception{
        if (!this.eat(Tag.OP_AD) && !this.eat(Tag.OP_MIN)) {
            throw new Exception("Esperado \"+ | -\", encontrado: " + token.getLexema());
        }
    }

    //mulop → “*”  | “/” 
    private void mulop() throws Exception {
        if (!this.eat(Tag.OP_MUL) && !this.eat(Tag.OP_DIV)) {
            throw new Exception("Esperado \"* | /\", encontrado: " + token.getLexema());
        }
    }

    //constant → “num_const”  | “char_const” 
    private No constant() throws Exception {

        No noType = new No();

        if (this.eat(Tag.NUM_CONST)) {
            noType.tipo = Tag.T_NUM;
        }
        else if (this.eat(Tag.CHAR_CONST)) {
            noType.tipo = Tag.T_CHAR;
        }
        else {
            throw new Exception("Esperado \"num_const | char_const\", encontrado: " + token.getLexema());
        }

        return noType;
    }	
	
}
