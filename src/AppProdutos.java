import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
