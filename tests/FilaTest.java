import java.util.NoSuchElementException;

public class FilaTest {

	private static void verificar(boolean condicao, String mensagem) {
		if (!condicao) {
			throw new AssertionError(mensagem);
		}
	}

	private static void verificarErro(Class<? extends RuntimeException> tipo, Runnable acao) {
		try {
			acao.run();
		} catch (RuntimeException erro) {
			verificar(tipo.isInstance(erro), "Tipo de exceção inesperado: " + erro);
			return;
		}
		throw new AssertionError("Era esperada uma exceção " + tipo.getSimpleName());
	}

	public static void main(String[] args) {
		Fila<Integer> fila = new Fila<Integer>();
		verificar(fila.vazia() && fila.toString().isEmpty(), "A fila inicial deve estar vazia.");
		verificarErro(NoSuchElementException.class, () -> fila.desenfileirar());
		verificarErro(NoSuchElementException.class, () -> fila.consultarPrimeiro());
		verificarErro(IllegalArgumentException.class, () -> fila.calcularValorMedio(n -> n.doubleValue(), 1));
		verificar(fila.filtrar(n -> true, 0).vazia(), "Filtrar zero itens deve retornar uma fila vazia.");
		for (int valor : new int[] { 10, 21, 30, 1000 }) {
			fila.enfileirar(valor);
		}

		int[] chamadas = { 0 };
		double media = fila.calcularValorMedio(n -> {
			chamadas[0]++;
			return n / 2.0;
		}, 2);
		verificar(media == 7.75 && chamadas[0] == 2, "A média deve usar a função apenas nos primeiros N itens.");
		verificar(fila.calcularValorMedio(n -> n.doubleValue(), 4) == 265.25,
				"A média deve aceitar todos os itens da fila.");
		chamadas[0] = 0;
		Fila<Integer> pares = fila.filtrar(n -> {
			chamadas[0]++;
			return n % 2 == 0;
		}, 3);
		verificar(chamadas[0] == 3, "O filtro deve testar N itens, mesmo quando há reprovações.");
		verificar(pares.desenfileirar() == 10 && pares.desenfileirar() == 30 && pares.vazia(),
				"O filtro deve preservar a ordem e excluir correspondências após os primeiros N itens.");
		pares.enfileirar(99);
		verificar(fila.consultarPrimeiro() == 10, "A fila filtrada deve possuir células independentes.");
		verificar(fila.filtrar(n -> false, 4).vazia(), "O filtro pode não encontrar correspondências.");
		verificar(fila.filtrar(n -> true, 4).toString().equals("10\n21\n30\n1000\n"),
				"O filtro deve aceitar todos os itens sem inverter a fila.");
		verificarErro(IllegalArgumentException.class, () -> fila.calcularValorMedio(n -> n.doubleValue(), 0));
		verificarErro(IllegalArgumentException.class, () -> fila.calcularValorMedio(n -> n.doubleValue(), -1));
		verificarErro(IllegalArgumentException.class, () -> fila.calcularValorMedio(n -> n.doubleValue(), 5));
		verificarErro(IllegalArgumentException.class, () -> fila.filtrar(n -> true, -1));
		verificarErro(IllegalArgumentException.class, () -> fila.filtrar(n -> true, 5));
		verificarErro(NullPointerException.class, () -> fila.calcularValorMedio(null, 1));
		verificarErro(NullPointerException.class, () -> fila.filtrar(null, 1));
		verificarErro(IllegalStateException.class, () -> fila.filtrar(n -> {
			throw new IllegalStateException("Falha na condição.");
		}, 1));
		for (int esperado : new int[] { 10, 21, 30, 1000 }) {
			verificar(fila.desenfileirar() == esperado, "As consultas não devem alterar a fila original.");
		}
		verificar(fila.vazia(), "A fila deve estar vazia após remover todos os itens.");
		fila.enfileirar(42);
		verificar(fila.desenfileirar() == 42 && fila.vazia(), "A fila deve permitir reinserção após esvaziar.");

		Fila<String> textos = new Fila<String>();
		textos.enfileirar("abc");
		textos.enfileirar("abcdef");
		verificar(textos.calcularValorMedio(texto -> (double) texto.length(), 2) == 4.5,
				"A extração deve funcionar com elementos de qualquer tipo.");
		System.out.println("FilaTest: OK");
	}
}
