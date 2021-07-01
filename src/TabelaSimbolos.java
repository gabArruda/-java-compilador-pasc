import java.util.LinkedHashMap;
import java.util.Map;

public class TabelaSimbolos {
		
	// Classe para a tabela de simbolos representada por um dicionario: {'chave' : 'valor'}
	Map<String, Token> ts = new LinkedHashMap<String, Token>();
	{
		ts.put("program", new Token(Tag.KW_PROGRAM, "program", 0, 0));
		ts.put("if", new Token(Tag.KW_IF, "if", 0, 0));
		ts.put("else", new Token(Tag.KW_ELSE, "else", 0, 0));
		ts.put("while", new Token(Tag.KW_WHILE, "while", 0, 0));
		ts.put("write", new Token(Tag.KW_WRITE, "write", 0, 0));
		ts.put("read", new Token(Tag.KW_READ, "read", 0, 0));
		ts.put("num", new Token(Tag.KW_NUM, "num", 0, 0));
		ts.put("char", new Token(Tag.KW_CHAR, "char", 0, 0));
		ts.put("not", new Token(Tag.KW_NOT, "not", 0, 0));
		ts.put("or", new Token(Tag.KW_OR, "or", 0, 0));
		ts.put("and", new Token(Tag.KW_AND, "and", 0, 0));
	}

	public Token getToken(String lexema){
		Token token = ts.get(lexema.toLowerCase());
		return token;
	}
	public void addToken(String key,Token value){
		ts.put(key.toLowerCase(), value);
	}
	public void printTS() {
		ts.forEach((k,v) -> {
		    System.out.println(v.toString());
		});
	}
}