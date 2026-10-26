import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class AppProdutosTest {

	private static void verificar(boolean condicao, String mensagem) {
		if (!condicao) {
			throw new AssertionError(mensagem);
		}
	}

	private static String consultar(String entrada) {
		return executar(entrada, AppProdutos::listarProdutosPedidosRecentes);
	}

	private static String executar(String entrada, Runnable acao) {
		ByteArrayOutputStream saida = new ByteArrayOutputStream();
		PrintStream console = System.out;
		try (Scanner teclado = new Scanner(entrada);
				PrintStream captura = new PrintStream(saida, true, StandardCharsets.UTF_8)) {
			AppProdutos.teclado = teclado;
			System.setOut(captura);
			acao.run();
		} finally {
			System.setOut(console);
		}
		return saida.toString(StandardCharsets.UTF_8);
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.forLanguageTag("pt-BR"));
		AppProdutos.pedidos = new Pilha<Pedido>();
		AppProdutos.pedidosEmOrdemCadastro = new Fila<Pedido>();
		AppProdutos.pedidoAtual = new Pedido();
		verificar(consultar("").contains("Nenhum pedido cadastrado"), "Deve tratar histórico vazio.");
		for (Runnable consulta : new Runnable[] { AppProdutos::exibirValorMedioPrimeirosPedidos,
				AppProdutos::exibirPrimeirosPedidosAcimaValor, AppProdutos::exibirPrimeirosPedidosComProduto }) {
			verificar(executar("", consulta).contains("Nenhum pedido cadastrado"), "Deve tratar fila vazia.");
		}
		AppProdutos.finalizarPedido();
		verificar(AppProdutos.pedidos.vazia(), "Pedido vazio não deve ser cadastrado.");
		verificar(AppProdutos.pedidosEmOrdemCadastro.vazia(), "Pedido vazio não deve entrar na fila.");

		AppProdutos.pedidoAtual.adicionarProduto(new ProdutoNaoPerecivel("Produto antigo", 10));
		Pedido antigo = AppProdutos.pedidoAtual;
		AppProdutos.finalizarPedido();
		verificar(AppProdutos.pedidoAtual != antigo && AppProdutos.pedidoAtual.vazio(),
				"Finalizar deve iniciar um novo pedido independente.");
		AppProdutos.pedidoAtual.adicionarProduto(new ProdutoNaoPerecivel("Produto recente", 20));
		AppProdutos.pedidoAtual.adicionarProduto(new ProdutoNaoPerecivel("Outro produto recente", 30));
		Pedido recente = AppProdutos.pedidoAtual;
		AppProdutos.finalizarPedido();

		String somenteUltimo = consultar("1\n");
		verificar(somenteUltimo.contains("Produto recente") && somenteUltimo.contains("Outro produto recente"),
				"Deve listar todos os produtos do último pedido.");
		verificar(!somenteUltimo.contains("Produto antigo"), "Deve respeitar a quantidade solicitada.");
		String ambos = consultar("2\n");
		verificar(ambos.indexOf("Produto recente") >= 0
				&& ambos.indexOf("Produto antigo") > ambos.indexOf("Produto recente"),
				"Deve listar os pedidos do mais recente para o mais antigo.");
		verificar(ambos.equals(consultar("2\n")), "Repetir a consulta deve preservar o resultado.");
		verificar(consultar("3\n").contains("supera o tamanho"), "Deve tratar quantidade excessiva.");
		verificar(consultar("-1\n").contains("não pode ser negativa"), "Deve tratar quantidade negativa.");
		verificar(consultar("abc\n").contains("quantidade inteira"), "Deve tratar entrada inválida.");
		verificar(!consultar("0\n").contains("NOME:"), "Zero pedidos não deve listar produtos.");
		verificar(AppProdutos.pedidos.desempilhar() == recente, "O pedido recente deve permanecer no histórico.");
		verificar(AppProdutos.pedidos.desempilhar() == antigo, "O pedido antigo deve permanecer no histórico.");
		verificar(AppProdutos.pedidos.vazia(), "O histórico deve conter exatamente dois pedidos.");

		AppProdutos.pedidoAtual.adicionarProduto(new ProdutoNaoPerecivel("Produto antigo", 100));
		Pedido terceiro = AppProdutos.pedidoAtual;
		AppProdutos.finalizarPedido();
		verificar(antigo.valorTotal() == 12.0 && recente.valorTotal() == 60.0, "O total deve somar os valores de venda.");
		String media = executar("2\n", AppProdutos::exibirValorMedioPrimeirosPedidos);
		verificar(media.contains(NumberFormat.getCurrencyInstance().format(36.0)),
				"A média deve considerar os dois primeiros pedidos cadastrados.");
		verificar(media.equals(executar("2\n", AppProdutos::exibirValorMedioPrimeirosPedidos)),
				"A consulta da média deve poder ser repetida.");
		String acima = executar("2\n12,00\n", AppProdutos::exibirPrimeirosPedidosAcimaValor);
		verificar(acima.contains("Produto recente") && acima.contains("Outro produto recente")
				&& !acima.contains("NOME: Produto antigo"),
				"O filtro por valor deve excluir o limite exato e pedidos posteriores aos N primeiros.");
		String comProduto = executar("2\nProduto antigo\n", AppProdutos::exibirPrimeirosPedidosComProduto);
		verificar(comProduto.contains("NOME: Produto antigo") && !comProduto.contains("NOME: Produto recente")
				&& !comProduto.contains(NumberFormat.getCurrencyInstance().format(120.0)),
				"A busca deve usar igualdade pela descrição e considerar somente os N primeiros pedidos.");
		verificar(executar("1\nProduto recente\n", AppProdutos::exibirPrimeirosPedidosComProduto)
				.contains("Nenhum pedido encontrado"), "O filtro deve informar quando não houver resultados.");
		verificar(executar("0\n", AppProdutos::exibirValorMedioPrimeirosPedidos).contains("pelo menos um"),
				"A média deve rejeitar zero pedidos.");
		verificar(executar("4\n", AppProdutos::exibirValorMedioPrimeirosPedidos).contains("supera o tamanho"),
				"A média deve rejeitar quantidade superior ao total.");
		verificar(executar("abc\n", AppProdutos::exibirValorMedioPrimeirosPedidos).contains("quantidade inteira"),
				"A consulta deve tratar entrada inválida.");
		verificar(executar("2\nNaN\n", AppProdutos::exibirPrimeirosPedidosAcimaValor).contains("finito"),
				"O filtro por valor deve rejeitar um limite não numérico.");
		for (Pedido esperado : new Pedido[] { antigo, recente, terceiro }) {
			verificar(AppProdutos.pedidosEmOrdemCadastro.desenfileirar() == esperado,
					"As novas consultas devem preservar os pedidos em ordem de cadastro.");
		}
		verificar(AppProdutos.pedidosEmOrdemCadastro.vazia(), "A fila deve conter exatamente três pedidos.");
		System.out.println("AppProdutosTest: OK");
	}
}
