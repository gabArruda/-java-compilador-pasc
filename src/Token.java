public class Token {

	private Tag  nome, tipo;
	private String lexema;
	private int linha, coluna;
	
	public Token(Tag nome, String lexema, int linha, int coluna) {
		super();
		this.nome = nome;
		this.lexema = lexema;
		this.linha = linha;
		this.coluna = coluna;
		this.tipo = Tag.T_VOID;
	}
	
	public Token() {
		super();
	}
	
	public Tag getNome() {
		return nome;
	}
	
	public String getLexema() {
		return lexema;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	public int getColuna() {
		return coluna;
	}

	public void setColuna(int coluna) {
		this.coluna = coluna;
	}

	public Tag getTipo() {
		return tipo;
	}

	public void setTipo(Tag tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "<" + nome + ", \"" + lexema + "\">";
	}
	
}
