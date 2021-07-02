import java.util.Map;
import java.util.Set;

public class Main {

	public static void main(String[] args) {

		

		try {
			Lexer lexer = new Lexer("prog1.txt");
			PreditivoNaoRecursivo pnr = new PreditivoNaoRecursivo(lexer);

			pnr.start();

			// System.out.println(pnr.actions.remove(pnr.actions.size()-1));
			// System.out.println(pnr.actions);

			// pnr.actions.add("body");
			// pnr.actions.add("id");
			// pnr.actions.add("program");

			// System.out.println(pnr.actions.remove(pnr.actions.size()-1));
			// System.out.println(pnr.actions);


			//Parser parser = new Parser(lexer);			
			//parser.prog();

			//System.out.println("Compilado corretamente");
			
			//lexer.printTS();

		}
		catch(Exception ex) {
			System.out.println("Exceção não tratada - " + ex.getMessage());
		}

	}
}