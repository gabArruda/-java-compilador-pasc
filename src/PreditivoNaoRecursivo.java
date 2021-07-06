import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreditivoNaoRecursivo {

  Lexer lexer;
	Token token;
  Map<Tuple, String> map;
  ArrayList<String> actions = new ArrayList<String>();

  ArrayList<String> terminais = new ArrayList<String>();
  ArrayList<String> naoTerminais = new ArrayList<String>();

  public PreditivoNaoRecursivo(Lexer lexer) throws Exception {

    this.lexer = lexer;
		this.token = this.lexer.proxToken();
		
		if (this.token == null) {
			throw new Exception("Nenhum token reconhecido");
		}

    map = new HashMap<>();
    //prog
    this.set("prog", "program", "program id body");
    //body
		this.set("body", "{", "decl-list { stmt-list }");
		this.set("body", "num", "decl-list { stmt-list }");
		this.set("body", "char", "decl-list { stmt-list }");
    //decl-list
    this.set("decl-list", "{", "&");
    this.set("decl-list", "num", "decl ; decl-list");
    this.set("decl-list", "char", "decl ; decl-list");
    //decl
    this.set("decl", "num", "type id-list");
    this.set("decl", "char", "type id-list");
    //type
    this.set("type", "num", "num");
    this.set("type", "char", "char");
    //id-list
    this.set("id-list", "id", "id id-list-linha");
    //id-list-linha
    this.set("id-list-linha", ";", "&");
    this.set("id-list-linha", ",", ", id-list");
    //stmt-list
    this.set("stmt-list", "id", "stmt ; stmt-list");
    this.set("stmt-list", "}", "&");
    this.set("stmt-list", "if", "stmt ; stmt-list");
    this.set("stmt-list", "read", "stmt ; stmt-list");
    this.set("stmt-list", "write", "stmt ; stmt-list");
    this.set("stmt-list", "while", "stmt ; stmt-list");
    //stmt
    this.set("stmt", "id", "assign-stmt");
    this.set("stmt", "if", "if-stmt");
    this.set("stmt", "read", "read-stmt");
    this.set("stmt", "write", "write-stmt");
    this.set("stmt", "while", "while-stmt");
    //assign-stmt
    this.set("assign-stmt", "id", "id = simple-expr");
    //if-stmt
    this.set("if-stmt", "if", "if ( expression ) { stmt-list } if-stmt-linha");
    //if-stmt-linha
    this.set("if-stmt-linha", ";", "&");
    this.set("if-stmt-linha", "else", "else { stmt-list }");
    //while-stmt
    this.set("while-stmt", "while", "stmt-prefix { stmt-list }");
    //stmt-prefix
    this.set("stmt-prefix", "while", "while ( expression )");
    //read-stmt
    this.set("read-stmt", "read", "read id");
    //write-stmt
    this.set("write-stmt", "write", "write simple-expr");
    //expression
    this.set("expression", "id", "simple-expr expression-linha");
    this.set("expression", "(", "simple-expr expression-linha");
    this.set("expression", "not", "simple-expr expression-linha");
    this.set("expression", "num_const", "simple-expr expression-linha");
    this.set("expression", "char_const", "simple-expr expression-linha");
    //expression-linha
    this.set("expression-linha", ")", "&");
    this.set("expression-linha", "and", "logop simple-expr expression-linha");
    this.set("expression-linha", "or", "logop simple-expr expression-linha");
    //simple-expr
    this.set("simple-expr", "id", "term simple-expr-linha");
    this.set("simple-expr", "(", "term simple-expr-linha");
    this.set("simple-expr", "not", "term simple-expr-linha");
    this.set("simple-expr", "num_const", "term simple-expr-linha");
    this.set("simple-expr", "char_const", "term simple-expr-linha");
    //simple-expr-linha
    this.set("simple-expr-linha", ";", "&");
    this.set("simple-expr-linha", ")", "&");
    this.set("simple-expr-linha", "and", "&");
    this.set("simple-expr-linha", "or", "&");
    this.set("simple-expr-linha", "==", "relop term simple-expr-linha");
    this.set("simple-expr-linha", ">", "relop term simple-expr-linha");
    this.set("simple-expr-linha", ">=", "relop term simple-expr-linha");
    this.set("simple-expr-linha", "<", "relop term simple-expr-linha");
    this.set("simple-expr-linha", "<=", "relop term simple-expr-linha");
    this.set("simple-expr-linha", "!=", "relop term simple-expr-linha");
    //term
    this.set("term", "id", "factor-b term-linha");
    this.set("term", "(", "factor-b term-linha");
    this.set("term", "not", "factor-b term-linha");
    this.set("term", "num_const", "factor-b term-linha");
    this.set("term", "char_const", "factor-b term-linha");
    //term-linha
    this.set("term-linha", ";", "&");
    this.set("term-linha", ")", "&");
    this.set("term-linha", "and", "&");
    this.set("term-linha", "or", "&");
    this.set("term-linha", "==", "&");
    this.set("term-linha", ">", "&");
    this.set("term-linha", ">=", "&");
    this.set("term-linha", "<", "&");
    this.set("term-linha", "<=", "&");
    this.set("term-linha", "!=", "&");
    this.set("term-linha", "+", "addop factor-b term-linha");
    this.set("term-linha", "-", "addop factor-b term-linha");
    //factor-b
    this.set("factor-b", "id", "factor-a factor-b-linha");
    this.set("factor-b", "(", "factor-a factor-b-linha");
    this.set("factor-b", "not", "factor-a factor-b-linha");
    this.set("factor-b", "num_const", "factor-a factor-b-linha");
    this.set("factor-b", "char_const", "factor-a factor-b-linha");
    //factor-b-linha
    this.set("factor-b-linha", ";", "&");
    this.set("factor-b-linha", ")", "&");
    this.set("factor-b-linha", "and", "&");
    this.set("factor-b-linha", "or", "&");
    this.set("factor-b-linha", "==", "&");
    this.set("factor-b-linha", ">", "&");
    this.set("factor-b-linha", ">=", "&");
    this.set("factor-b-linha", "<", "&");
    this.set("factor-b-linha", "<=", "&");
    this.set("factor-b-linha", "!=", "&");
    this.set("factor-b-linha", "+", "&");
    this.set("factor-b-linha", "-", "&");
    this.set("factor-b-linha", "*", "mulop factor-a factor-b-linha");
    this.set("factor-b-linha", "/", "mulop factor-a factor-b-linha");
    //factor-a
    this.set("factor-a", "id", "factor");
    this.set("factor-a", "(", "factor");
    this.set("factor-a", "not", "not factor");
    this.set("factor-a", "num_const", "factor");
    this.set("factor-a", "char_const", "factor");
    //factor
    this.set("factor", "id", "id");
    this.set("factor", "(", "( expression )");
    this.set("factor", "num_const", "constant");
    this.set("factor", "char_const", "constant");
    //logop
    this.set("logop", "and", "and");
    this.set("logop", "or", "or");
    //relop
    this.set("relop", "==", "==");
    this.set("relop", ">", ">");
    this.set("relop", ">=", ">=");
    this.set("relop", "<", "<");
    this.set("relop", "<=", "<=");
    this.set("relop", "!=", "!=");
    //addop
    this.set("addop", "+", "+");
    this.set("addop", "-", "-");
    //mulop
    this.set("mulop", "*", "*");
    this.set("mulop", "/", "/");
    //constant
    this.set("constant", "num_const", "num_const");
    this.set("constant", "char_const", "char_const");

    Set<Tuple> keys = map.keySet();

    for(Tuple k : keys) {
      naoTerminais.add(k.getNaoTerminal());
    }
    
  }

  public void start() throws Exception {

    actions.add("$");
    actions.add("prog");	

    while(actions.size() > 0) {
      
      String tk_type = tokenType(this.token);
      String action = this.actions.remove(this.actions.size()-1);

      if (naoTerminais.contains(action)) {
        String actionsToAdd = this.get(action, tk_type);        
        String[] actionsSplit = actionsToAdd.split(" ");
        for (int i = actionsSplit.length-1; i >=0; i--) {
          actions.add(actionsSplit[i]);
        }
      }
      else if (tk_type.equals(action)) {
        this.token = this.lexer.proxToken();
      }
      else if (action.equals("&")) {}
      else {
        throw new Exception("Esperado: \"" + action + "\", encontrado: " + tk_type);
      }

      System.out.println("Ação: [ " + action + " ]. Fila: " + actions);
    }
    
  }

  private String tokenType(Token token) throws Exception {
    while(this.token.getNome() == Tag.COM_MULT_LINES || this.token.getNome() == Tag.COM_ONE_LINE) {
      this.token = this.lexer.proxToken();
    }
    if (this.token.getNome() == Tag.NUM_CONST) {
      return "num_const";
    }
    else if (this.token.getNome() == Tag.CHAR_CONST) {
      return "char_const";
    }
    else if (this.token.getNome() == Tag.ID) {
      return "id";
    }
    else {
      return this.token.getLexema();
    }
  }

  private void set(String naoTerminal, String terminal, String value) {
    map.put(new Tuple(naoTerminal, terminal), value);
  }

  private String get(String naoTerminal, String terminal) throws Exception {      
    String value = map.get(new Tuple(naoTerminal, terminal));
    if (value != null) {
      return value;
    }
    throw new Exception("não encontrado no mapa: " + naoTerminal + " com " + terminal);
  }

}
