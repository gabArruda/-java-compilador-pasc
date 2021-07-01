public class Main {

	public static void main(String[] args) {

		try {
			Lexer lexer = new Lexer("prog1.txt");
			Parser parser = new Parser(lexer);
			
			parser.prog();
			System.out.println("Compilado corretamente");
			lexer.printTS();

		}
		catch(Exception ex) {
			System.out.println("Exceção não tratada - " + ex.getMessage());
		}

	}
}