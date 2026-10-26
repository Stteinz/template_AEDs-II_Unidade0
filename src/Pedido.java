import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Pedido {

	private final List<Produto> produtos = new ArrayList<Produto>();

	public void adicionarProduto(Produto produto) {
		produtos.add(Objects.requireNonNull(produto, "O produto deve ser informado."));
	}

	public boolean vazio() {
		return produtos.isEmpty();
	}

	public List<Produto> getProdutos() {
		return Collections.unmodifiableList(produtos);
	}

	public double valorTotal() {
		double total = 0.0;
		for (Produto produto : produtos) {
			total += produto.valorDeVenda();
		}
		return total;
	}

	public boolean contemProduto(Produto produto) {
		return produtos.contains(produto);
	}

	@Override
	public String toString() {
		StringBuilder texto = new StringBuilder("Pedido:\n");
		for (Produto produto : produtos) {
			texto.append(produto).append('\n');
		}
		return texto.append("Valor total: ").append(NumberFormat.getCurrencyInstance().format(valorTotal()))
				.append('\n').toString();
	}
}
