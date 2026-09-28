import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ProdutoPerecivel extends Produto {

	private static final double DESCONTO = 0.25;
	private static final int PRAZO_DESCONTO = 7;
	private LocalDate dataDeValidade;

	public ProdutoPerecivel(String desc, double precoCusto, double margemLucro, LocalDate validade) {
		super(desc, precoCusto, margemLucro);
		validarData(validade);
		this.dataDeValidade = validade;
	}

	public ProdutoPerecivel(String desc, double precoCusto, LocalDate validade) {
		super(desc, precoCusto);
		validarData(validade);
		this.dataDeValidade = validade;
	}

	private static void validarData(LocalDate validade) {
		if (validade == null || validade.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("A data de validade não pode ser anterior à data atual.");
		}
	}

	@Override
	public double valorDeVenda() {
		LocalDate hoje = LocalDate.now();
		if (dataDeValidade.isBefore(hoje)) {
			throw new IllegalStateException("Não é possível vender um produto vencido.");
		}

		double valor = precoCusto * (1.0 + margemLucro);
		long diasParaVencer = ChronoUnit.DAYS.between(hoje, dataDeValidade);
		if (diasParaVencer <= PRAZO_DESCONTO) {
			valor = valor * (1.0 - DESCONTO);
		}
		return valor;
	}

	@Override
	public String toString() {
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return super.toString() + "\nVálido até " + formato.format(dataDeValidade);
	}

	@Override
	public String gerarDadosTexto() {
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return String.format(Locale.US, "2;%s;%.2f;%.2f;%s", descricao, precoCusto, margemLucro,
				formato.format(dataDeValidade));
	}
}
