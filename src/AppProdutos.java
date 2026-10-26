import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class AppProdutos {

	static final int MAX_NOVOS_PRODUTOS = 10;
	static final String NOME_ARQUIVO = "dadosProdutos.csv";

	static Scanner teclado;
	static Produto[] produtosCadastrados;
	static int quantosProdutos;
	static boolean dadosCarregados;
	static Pilha<Pedido> pedidos = new Pilha<Pedido>();
	static Fila<Pedido> pedidosEmOrdemCadastro = new Fila<Pedido>();
	static Pedido pedidoAtual = new Pedido();

	static void pausa() {
		System.out.println("Digite enter para continuar...");
		teclado.nextLine();
	}

	static void cabecalho() {
		System.out.println("AEDs II COMÉRCIO DE COISINHAS");
		System.out.println("=============================");
	}

	static int menu() {
		cabecalho();
		System.out.println("1 - Listar todos os produtos");
		System.out.println("2 - Procurar e imprimir os dados de um produto");
		System.out.println("3 - Cadastrar novo produto");
		System.out.println("4 - Incluir produto no pedido atual");
		System.out.println("5 - Finalizar pedido");
		System.out.println("6 - Listar produtos dos pedidos mais recentes");
		System.out.println("7 - Exibir o valor total médio dos N primeiros pedidos");
		System.out.println("8 - Exibir os primeiros pedidos com valor total acima de um determinado valor");
		System.out.println("9 - Exibir os primeiros pedidos que apresentam um determinado produto");
		System.out.println("0 - Sair");
		System.out.print("Digite sua opção: ");
		try {
			return Integer.parseInt(teclado.nextLine().trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	static Produto[] lerProdutos(String nomeArquivoDados) {
		File arquivo = new File(nomeArquivoDados);
		try (Scanner leitor = new Scanner(arquivo, StandardCharsets.UTF_8)) {
			int quantidade = Integer.parseInt(leitor.nextLine().trim());
			Produto[] produtos = new Produto[quantidade + MAX_NOVOS_PRODUTOS];
			for (int i = 0; i < quantidade; i++) {
				produtos[i] = Produto.criarDoTexto(leitor.nextLine());
			}
			quantosProdutos = quantidade;
			dadosCarregados = true;
			return produtos;
		} catch (Exception e) {
			System.out.println("Erro ao ler o arquivo de dados: " + e.getMessage());
			quantosProdutos = 0;
			dadosCarregados = false;
			return new Produto[MAX_NOVOS_PRODUTOS];
		}
	}

	static void salvarProdutos(String nomeArquivo) {
		try (PrintWriter escritor = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(nomeArquivo), StandardCharsets.UTF_8))) {
			escritor.println(quantosProdutos);
			for (int i = 0; i < quantosProdutos; i++) {
				escritor.println(produtosCadastrados[i].gerarDadosTexto());
			}
		} catch (IOException e) {
			System.out.println("Erro ao salvar os produtos: " + e.getMessage());
		}
	}

	static void listarTodosOsProdutos() {
		if (quantosProdutos == 0) {
			System.out.println("Nenhum produto cadastrado.");
			return;
		}
		for (int i = 0; i < quantosProdutos; i++) {
			System.out.println((i + 1) + " - " + produtosCadastrados[i]);
		}
	}

	static void localizarProduto() {
		System.out.print("Digite o nome do produto: ");
		String nome = teclado.nextLine();
		Produto procurado;
		try {
			procurado = new ProdutoNaoPerecivel(nome, 1.0);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
			return;
		}
		for (int i = 0; i < quantosProdutos; i++) {
			if (produtosCadastrados[i].equals(procurado)) {
				System.out.println(produtosCadastrados[i]);
				return;
			}
		}
		System.out.println("Produto não encontrado.");
	}

	static double lerDouble(String mensagem) {
		System.out.print(mensagem);
		String texto = teclado.nextLine().trim().replace(',', '.');
		return Double.parseDouble(texto);
	}

	static void cadastrarProduto() {
		if (quantosProdutos >= produtosCadastrados.length) {
			System.out.println("Não há espaço para novos produtos.");
			return;
		}

		System.out.println("1 - Produto não perecível");
		System.out.println("2 - Produto perecível");
		System.out.print("Digite o tipo do produto: ");
		int tipo;
		try {
			tipo = Integer.parseInt(teclado.nextLine().trim());
		} catch (NumberFormatException e) {
			System.out.println("Tipo de produto inválido.");
			return;
		}

		System.out.print("Descrição: ");
		String descricao = teclado.nextLine();

		try {
			double precoCusto = lerDouble("Preço de custo: ");
			double margemLucro = lerDouble("Margem de lucro: ");
			Produto novoProduto;

			if (tipo == 1) {
				novoProduto = new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
			} else if (tipo == 2) {
				System.out.print("Data de validade (dd/MM/yyyy): ");
				DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate validade = LocalDate.parse(teclado.nextLine().trim(), formato);
				novoProduto = new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
			} else {
				System.out.println("Tipo de produto inválido.");
				return;
			}

			produtosCadastrados[quantosProdutos] = novoProduto;
			quantosProdutos++;
			System.out.println("Produto cadastrado com sucesso.");
		} catch (IllegalArgumentException | DateTimeParseException e) {
			System.out.println("Cadastro recusado: " + e.getMessage());
		}
	}

	static void incluirProdutoPedido() {
		if (quantosProdutos == 0) {
			System.out.println("Nenhum produto cadastrado.");
			return;
		}
		listarTodosOsProdutos();
		System.out.print("Digite o número do produto a incluir no pedido: ");
		try {
			int numero = Integer.parseInt(teclado.nextLine().trim());
			if (numero < 1 || numero > quantosProdutos) {
				System.out.println("Número de produto inválido.");
				return;
			}
			pedidoAtual.adicionarProduto(produtosCadastrados[numero - 1]);
			System.out.println("Produto incluído no pedido.");
		} catch (NumberFormatException e) {
			System.out.println("Número de produto inválido.");
		}
	}

	static void finalizarPedido() {
		if (pedidoAtual.vazio()) {
			System.out.println("Inclua pelo menos um produto antes de finalizar o pedido.");
			return;
		}
		pedidos.empilhar(pedidoAtual);
		pedidosEmOrdemCadastro.enfileirar(pedidoAtual);
		pedidoAtual = new Pedido();
		System.out.println("Pedido finalizado com sucesso.");
	}

	static void listarProdutosPedidosRecentes() {
		if (pedidos.vazia()) {
			System.out.println("Nenhum pedido cadastrado.");
			return;
		}
		System.out.print("Quantos pedidos recentes deseja consultar? ");
		try {
			int numPedidos = Integer.parseInt(teclado.nextLine().trim());
			Pilha<Pedido> recentes = pedidos.subPilha(numPedidos);
			int numero = 1;
			while (!recentes.vazia()) {
				Pedido pedido = recentes.desempilhar();
				System.out.println("Pedido recente " + numero++ + ":");
				for (Produto produto : pedido.getProdutos()) {
					System.out.println(produto);
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Digite uma quantidade inteira de pedidos.");
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	static int lerQuantidadePedidos() {
		System.out.print("Quantos primeiros pedidos deseja consultar? ");
		return Integer.parseInt(teclado.nextLine().trim());
	}

	static void exibirValorMedioPrimeirosPedidos() {
		if (pedidosEmOrdemCadastro.vazia()) {
			System.out.println("Nenhum pedido cadastrado.");
			return;
		}
		try {
			int quantidade = lerQuantidadePedidos();
			double media = pedidosEmOrdemCadastro.calcularValorMedio(Pedido::valorTotal, quantidade);
			System.out.println("Valor total médio: " + NumberFormat.getCurrencyInstance().format(media));
		} catch (NumberFormatException e) {
			System.out.println("Digite uma quantidade inteira de pedidos.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println(e.getMessage());
		}
	}

	static void exibirPedidosFiltrados(Fila<Pedido> filtrados) {
		if (filtrados.vazia()) {
			System.out.println("Nenhum pedido encontrado nos primeiros pedidos consultados.");
		} else {
			System.out.print(filtrados);
		}
	}

	static void exibirPrimeirosPedidosAcimaValor() {
		if (pedidosEmOrdemCadastro.vazia()) {
			System.out.println("Nenhum pedido cadastrado.");
			return;
		}
		try {
			int quantidade = lerQuantidadePedidos();
			double valor = lerDouble("Exibir pedidos com valor total acima de: ");
			if (!Double.isFinite(valor)) {
				throw new IllegalArgumentException("Informe um valor numérico finito.");
			}
			Fila<Pedido> filtrados = pedidosEmOrdemCadastro.filtrar(pedido -> pedido.valorTotal() > valor, quantidade);
			exibirPedidosFiltrados(filtrados);
		} catch (NumberFormatException e) {
			System.out.println("Digite uma quantidade inteira e um valor numérico válidos.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println(e.getMessage());
		}
	}

	static void exibirPrimeirosPedidosComProduto() {
		if (pedidosEmOrdemCadastro.vazia()) {
			System.out.println("Nenhum pedido cadastrado.");
			return;
		}
		try {
			int quantidade = lerQuantidadePedidos();
			System.out.print("Digite o nome do produto: ");
			Produto procurado = new ProdutoNaoPerecivel(teclado.nextLine().trim(), 1.0);
			Fila<Pedido> filtrados = pedidosEmOrdemCadastro.filtrar(pedido -> pedido.contemProduto(procurado), quantidade);
			exibirPedidosFiltrados(filtrados);
		} catch (NumberFormatException e) {
			System.out.println("Digite uma quantidade inteira de pedidos.");
		} catch (IllegalArgumentException | IllegalStateException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.forLanguageTag("pt-BR"));
		teclado = new Scanner(System.in, StandardCharsets.UTF_8);
		produtosCadastrados = lerProdutos(NOME_ARQUIVO);

		int opcao;
		do {
			opcao = menu();
			switch (opcao) {
				case 1 -> listarTodosOsProdutos();
				case 2 -> localizarProduto();
				case 3 -> cadastrarProduto();
				case 4 -> incluirProdutoPedido();
				case 5 -> finalizarPedido();
				case 6 -> listarProdutosPedidosRecentes();
				case 7 -> exibirValorMedioPrimeirosPedidos();
				case 8 -> exibirPrimeirosPedidosAcimaValor();
				case 9 -> exibirPrimeirosPedidosComProduto();
				case 0 -> System.out.println("Encerrando.");
				default -> System.out.println("Opção inválida.");
			}
			if (opcao != 0) {
				pausa();
			}
		} while (opcao != 0);

		if (dadosCarregados) {
			salvarProdutos(NOME_ARQUIVO);
		}
		teclado.close();
	}
}
