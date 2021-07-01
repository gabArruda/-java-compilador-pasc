import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lexer {
	
	private char[] conteudo;
	private char caractere_atual;
	private int err_count = 0;
	private int estado;
	private int pos;
	private int linha, linha_anterior;
	private int coluna, coluna_anterior;
	private boolean erro_aspas_abertas = false;
	private boolean aspas_abertas = false;
	protected TabelaSimbolos ts;
	
	public Lexer(String nomeArquivo) {
		try  {
			String txtConteudo;
			txtConteudo = new String(Files.readAllBytes(Paths.get(nomeArquivo)), StandardCharsets.UTF_8);
			txtConteudo = txtConteudo + '\n';
			conteudo = txtConteudo.toCharArray();
			pos = 0;
			linha = 1;
			coluna = 1;
			ts = new TabelaSimbolos();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Token proxToken() throws Exception {
		String lexema = "";
		estado = 0;
		
		while (true) {
			
			if (isEOF()) {
				if (aspas_abertas)
					sinalizaErroLexico("Aspas não fechadas antes do fim do arquivo");
				ts.addToken("eof", new Token(Tag.EOF, "EOF", linha, coluna));
				return ts.getToken("eof");
				
			}
			
			proxCaractere();
			
			switch(estado) {
			
				case 0:
					if(isWhiteSpace(caractere_atual)) {
						estado = 0;
					}
					else if (isNewLine(caractere_atual)){
						estado = 0;
					}
					else if (isDigit(caractere_atual)) {
						lexema += caractere_atual;
						estado = 1;
					}
					else if (caractere_atual == '\"') {
						lexema += caractere_atual;
						erro_aspas_abertas = true;
						aspas_abertas = true;
						estado = 9;
					}
					else if (isLetter(caractere_atual)) {
						lexema += caractere_atual;
						estado = 12;
					}
					else if (caractere_atual == '=') {
						lexema += caractere_atual;
						estado = 14;
					}
					else if (caractere_atual == '>') {
						lexema += caractere_atual;
						estado = 17;
					}
					else if (caractere_atual == '<') {
						lexema += caractere_atual;
						estado = 20;
					}
					else if (caractere_atual == '!') {
						lexema += caractere_atual;
						estado = 23;
					}
					else if (caractere_atual == '/') {
						lexema += caractere_atual;
						estado = 25;
					}
					else if (caractere_atual == '*') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_MUL, lexema, linha, coluna);
					}
					else if (caractere_atual == '+') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_AD, lexema, linha, coluna);
					}
					else if (caractere_atual == '-') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_MIN, lexema, linha, coluna);
					}
					else if (caractere_atual == '{') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.SMB_OBC, lexema, linha, coluna);
					}
					else if (caractere_atual == '}') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.SMB_CBC, lexema, linha, coluna);
					}
					else if (caractere_atual == '(') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.SMB_OPA, lexema, linha, coluna);
					}
					else if (caractere_atual == ')') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.SMB_CPA, lexema, linha, coluna);
					}
					else if (caractere_atual == ',') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.SMB_COM, lexema, linha, coluna);
					}
					else if (caractere_atual == ';') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.SMB_SEM, lexema, linha, coluna);
					}
					else {
						sinalizaErroLexico("Caractere invalido [" + caractere_atual + "] na linha " + linha + " e coluna " + coluna);
					}
					break;
				case 1:
					if (isDigit(caractere_atual)) {
						lexema += caractere_atual;
						estado = 1;
					}
					else if (caractere_atual == '.') {
						lexema += caractere_atual;
						estado = 3;
					}
					else {
						retornaPonteiro();
						return new Token(Tag.NUM_CONST, lexema, linha, coluna);
					}
					break;
				case 3:
					if (isDigit(caractere_atual)) {
						lexema += caractere_atual;
						estado = 4;
					}
					else {
						sinalizaErroLexico("Caractere invalido [" + caractere_atual + "] na linha " + linha + " e coluna " + coluna);
					}
					break;
				case 4:
					if (isDigit(caractere_atual)) {
						lexema += caractere_atual;
						estado = 4;
					}
					else {
						retornaPonteiro();
						return new Token(Tag.NUM_CONST, lexema, linha, coluna);
					}
					break;
				case 9:
					if (caractere_atual == '\"') {
						lexema += caractere_atual;
						erro_aspas_abertas = false;
						aspas_abertas = false;
						estado = 0;
						return new Token(Tag.CHAR_CONST, lexema, linha, coluna);
					}
					else if (isNewLine(caractere_atual) && erro_aspas_abertas == true) {
						estado = 9;
						erro_aspas_abertas = false;
						sinalizaErroLexico("Aspas não fechadas na linha " + (linha-1));
					}
					else if (isASCII(caractere_atual)) {
						lexema += caractere_atual;
						estado = 9;
					}
					else {
						sinalizaErroLexico("Caractere invalido [" + caractere_atual + "] na linha " + linha + " e coluna " + coluna);
					}
					break;
				case 12:
					if (isLetter(caractere_atual) || isDigit(caractere_atual)) {
						lexema += caractere_atual;
						estado = 12;
					}
					else {
						retornaPonteiro();
						if (ts.getToken(lexema) != null) {
							return ts.getToken(lexema);
						}
						else {
							ts.addToken(lexema, new Token(Tag.ID, lexema, linha, coluna));
							return ts.getToken(lexema);
						}
					}
					break;
				case 14:
					if (caractere_atual == '=') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_EQ, lexema, linha, coluna);
					}
					else {
						retornaPonteiro();
						return new Token (Tag.OP_ATRIB, lexema, linha, coluna);
					}
				case 17:
					if (caractere_atual == '=') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_GE, lexema, linha, coluna);
					}
					else {
						retornaPonteiro();
						return new Token(Tag.OP_GT, lexema, linha, coluna);
					}
				case 20:
					if (caractere_atual == '=') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_LE, lexema, linha, coluna);
					}
					else {
						retornaPonteiro();
						return new Token(Tag.OP_LT, lexema, linha, coluna);
					}
				case 23:
					if (caractere_atual == '=') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.OP_NE, lexema, linha, coluna);
					}
					else {
						estado = 0;
						sinalizaErroLexico("Token incompleto para o simbolo '" + caractere_atual + "' na linha " + linha + " e coluna " + coluna);
					}
					break;
				case 25:
					if (caractere_atual == '*' ) {
						lexema += caractere_atual;
						estado = 27;
					}
					else if (caractere_atual == '/') {
						lexema += caractere_atual;
						estado = 26;
					}
					else {
						retornaPonteiro();
						return new Token(Tag.OP_DIV, lexema, linha, coluna);
					}
					break;
				case 26:
					if (isNewLine(caractere_atual)) {
						retornaPonteiro();
						estado = 0;
						return new Token(Tag.COM_ONE_LINE, "", linha, coluna);
					}
					if (isASCII(caractere_atual)) {
						lexema += caractere_atual;
						estado = 26;
					}
					else {
						estado = 0;
						return new Token(Tag.COM_ONE_LINE, "", linha, coluna);
					}
					break;
				case 27:
					if (caractere_atual == '*') {
						lexema += caractere_atual;
						estado = 28;
					}
					else if (isASCII(caractere_atual)) {
						lexema += caractere_atual;
						estado = 27;
					}
					break;
				case 28:
					if(caractere_atual == '/') {
						lexema += caractere_atual;
						estado = 0;
						return new Token(Tag.COM_MULT_LINES, "", linha, coluna);
					}
					else if (caractere_atual == '*') {
						lexema += caractere_atual;
						estado = 28;
					}
					else if (isASCII(caractere_atual)) {
						lexema += caractere_atual;
						estado = 27;
					}
					break;
					
			
			}
		}
		
		
	}
	
	private void proxCaractere() {
		caractere_atual = '\u0000';

		caractere_atual = conteudo[pos++];
		
		linha_anterior = linha;
		coluna_anterior = coluna;
		
		if(isNewLine(caractere_atual)) {
			linha++;
			coluna = 1;
		}
		else if (caractere_atual == '\t') {
			coluna+=3;
		}
		else {
			coluna++;
		}
	}
	
	private void sinalizaErroLexico(String message) throws Exception {
		err_count++;
		if (err_count <= 3) {
			System.out.println("[Erro Lexico]: " + message);
		}
		else {
			throw new Exception("Mais que 3 erros léxicos foram encontrados, abortando execução.");
		}
	}
	
	private void retornaPonteiro() {
		pos--;
		linha = linha_anterior;
		coluna = coluna_anterior;
	}
	
	public void printTS() {
		ts.printTS();
	}	
	
	// Padrões para números, caracteres, strings e identificadores do PasC
	
	private boolean isASCII(char c) {
		return Character.toString(c).matches("\\p{ASCII}*");
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	private boolean isNewLine(char c) {
		return c == '\n';
	}
	
	private boolean isWhiteSpace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
	
	private boolean isEOF() {
		return pos == conteudo.length;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}	
	
}
