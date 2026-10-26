import java.util.NoSuchElementException;

public class PilhaTest {

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
		Pilha<Integer> original = new Pilha<Integer>();
		verificar(original.subPilha(0).vazia(), "Cópia de zero itens deve ser vazia.");
		verificarErro(NoSuchElementException.class, () -> original.desempilhar());
		verificarErro(IllegalArgumentException.class, () -> original.subPilha(1));

		original.empilhar(10);
		original.empilhar(20);
		original.empilhar(30);
		Pilha<Integer> recentes = original.subPilha(2);
		verificar(recentes.desempilhar() == 30, "O item mais recente deve sair primeiro.");
		verificar(recentes.desempilhar() == 20, "A cópia deve preservar a ordem original.");
		verificar(recentes.vazia(), "A cópia deve conter somente dois itens.");
		recentes.empilhar(99);
		verificar(original.consultarTopo() == 30, "Alterar a cópia não deve alterar a original.");

		verificarErro(IllegalArgumentException.class, () -> original.subPilha(-1));
		verificarErro(IllegalArgumentException.class, () -> original.subPilha(4));
		verificar(original.subPilha(0).vazia(), "Zero itens de uma pilha preenchida deve retornar vazio.");
		Pilha<Integer> completa = original.subPilha(3);
		for (int esperado : new int[] { 30, 20, 10 }) {
			verificar(completa.desempilhar() == esperado, "Ordem incorreta na cópia completa.");
			verificar(original.desempilhar() == esperado, "A original foi alterada pela cópia ou pelo erro.");
		}
		verificar(original.vazia() && completa.vazia(), "As pilhas devem estar vazias após as remoções.");
		System.out.println("PilhaTest: OK");
	}
}
