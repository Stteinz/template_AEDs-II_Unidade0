import java.time.LocalDate;
import java.util.Locale;

public class App {

	public static void main(String[] args) {
		Locale.setDefault(Locale.forLanguageTag("pt-BR"));

		Produto caderno = new ProdutoNaoPerecivel("Caderno", 10.0);
		Produto caneta = new ProdutoNaoPerecivel("Caneta", 5.0, 0.5);
		Produto arroz = new ProdutoPerecivel("Arroz", 100.0, 0.1, LocalDate.now().plusDays(10));
		Produto iogurte = new ProdutoPerecivel("Iogurte", 100.0, 0.1, LocalDate.now().plusDays(2));

		System.out.println(caderno);
		System.out.println(caneta);
		System.out.println(arroz);
		System.out.println(iogurte);

		try {
			new ProdutoPerecivel("Leite", 4.0, 0.2, LocalDate.now().minusDays(1));
		} catch (IllegalArgumentException e) {
			System.out.println("Cadastro recusado: " + e.getMessage());
		}
	}
}
