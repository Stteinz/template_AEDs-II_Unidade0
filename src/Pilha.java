import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private final Celula<E> fundo;

	public Pilha() {
		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;
	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {
		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {
		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;
	}

	public E consultarTopo() {
		if (vazia()) {
			throw new NoSuchElementException("Não há nenhum item na pilha!");
		}
		return topo.getItem();
	}

	/**
	 * Copia os primeiros elementos a partir do topo, mantendo sua ordem.
	 * A pilha original permanece inalterada e as células da cópia são independentes.
	 * Os itens armazenados são compartilhados entre as duas pilhas.
	 *
	 * @param numItens quantidade de elementos a copiar; zero retorna uma pilha vazia
	 * @return nova pilha com os elementos solicitados
	 * @throws IllegalArgumentException se a quantidade for negativa ou superar o tamanho da pilha
	 */
	public Pilha<E> subPilha(int numItens) {
		if (numItens < 0) {
			throw new IllegalArgumentException("A quantidade de itens não pode ser negativa.");
		}

		Pilha<E> auxiliar = new Pilha<E>();
		Celula<E> atual = topo;
		for (int i = 0; i < numItens; i++) {
			if (atual == fundo) {
				throw new IllegalArgumentException("A quantidade solicitada supera o tamanho da pilha.");
			}
			auxiliar.empilhar(atual.getItem());
			atual = atual.getProximo();
		}

		Pilha<E> resultado = new Pilha<E>();
		while (!auxiliar.vazia()) {
			resultado.empilhar(auxiliar.desempilhar());
		}
		return resultado;
	}
}
