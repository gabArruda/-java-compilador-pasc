import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		System.out.println("----------");
		System.out.println("Alunos:");
		System.out.println("Gabriel Arruda Ribeiro - 119122665");
		System.out.println("Isabella Dutra Vieira Salgueiro - 11826958");
		System.out.println("----------");

		
		Scanner sc = new Scanner(System.in);
		char c;
		do {
			System.out.println("(1) - Parser recursivo com análise sintática e semântica, sem modo pânico (lança exceção e fecha programa em erros sintáticos e semânticos)");
			System.out.println("(2) - Parser não recursivo (Tabela Preditiva e Pilha) sem análise semântica e sem modo pânico.");
			System.out.print("Escolha: ");
			c = sc.next().charAt(0);
		} while(c != '1' && c != '2');
		sc.close();

		try {
			Lexer lexer = new Lexer("prog1.txt");
			
			if (c == '1' ) {
				Parser parser = new Parser(lexer);			
				parser.prog();
			}
			else if (c == '2') {
				PreditivoNaoRecursivo pnr = new PreditivoNaoRecursivo(lexer);
				pnr.start();
			}

			System.out.println("Compilação finalizada.");
		}
		catch(Exception ex) {
			System.out.println("Exceção não tratada - " + ex.getMessage());
		}

	}
}