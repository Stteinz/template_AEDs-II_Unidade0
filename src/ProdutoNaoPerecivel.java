import java.util.Locale;

public class ProdutoNaoPerecivel extends Produto {

	public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
		super(desc, precoCusto, margemLucro);
	}

	public ProdutoNaoPerecivel(String desc, double precoCusto) {
		super(desc, precoCusto);
	}

	@Override
	public double valorDeVenda() {
		return precoCusto * (1.0 + margemLucro);
	}

	@Override
	public String gerarDadosTexto() {
		return String.format(Locale.US, "1;%s;%.2f;%.2f", descricao, precoCusto, margemLucro);
	}
}
