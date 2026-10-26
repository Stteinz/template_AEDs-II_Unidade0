import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

	private final Celula<E> frente;
	private Celula<E> tras;

	public Fila() {
		Celula<E> sentinela = new Celula<E>();
		frente = tras = sentinela;
	}

	public boolean vazia() {
		return frente == tras;
	}

	public void enfileirar(E item) {
		Celula<E> novaCelula = new Celula<E>(item);
		tras.setProximo(novaCelula);
		tras = novaCelula;
	}

	public E desenfileirar() {
		E item = consultarPrimeiro();
		Celula<E> primeiro = frente.getProximo();
		frente.setProximo(primeiro.getProximo());
		primeiro.setProximo(null);
		if (primeiro == tras) {
			tras = frente;
		}
		return item;
	}

	public E consultarPrimeiro() {
		if (vazia()) {
			throw new NoSuchElementException("Não há nenhum item na fila!");
		}
		return frente.getProximo().getItem();
	}

	private void validarQuantidade(int quantidade) {
		if (quantidade < 0) {
			throw new IllegalArgumentException("A quantidade não pode ser negativa.");
		}
		Celula<E> atual = frente.getProximo();
		for (int i = 0; i < quantidade; i++) {
			if (atual == null) {
				throw new IllegalArgumentException("A quantidade solicitada supera o tamanho da fila.");
			}
			atual = atual.getProximo();
		}
	}

	/**
	 * Calcula a média dos valores extraídos dos primeiros elementos, sem remover itens.
	 * @throws IllegalArgumentException se quantidade não estiver entre 1 e o tamanho da fila
	 * @throws NullPointerException se extrator for nulo ou retornar um valor nulo
	 */
	public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
		Objects.requireNonNull(extrator, "A função de extração deve ser informada.");
		validarQuantidade(quantidade);
		if (quantidade == 0) {
			throw new IllegalArgumentException("Informe pelo menos um elemento para calcular a média.");
		}
		double soma = 0.0;
		Celula<E> atual = frente.getProximo();
		for (int i = 0; i < quantidade; i++) {
			soma += extrator.apply(atual.getItem());
			atual = atual.getProximo();
		}
		return soma / quantidade;
	}

	/**
	 * Testa somente os primeiros quantidade elementos e copia os aprovados em sua ordem original.
	 * A fila original não é alterada; os itens são compartilhados, mas as células são independentes.
	 * Zero retorna uma fila vazia.
	 * @throws IllegalArgumentException se quantidade for negativa ou superar o tamanho da fila
	 * @throws NullPointerException se condicional for nulo
	 */
	public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
		Objects.requireNonNull(condicional, "A condição deve ser informada.");
		validarQuantidade(quantidade);
		Fila<E> resultado = new Fila<E>();
		Celula<E> atual = frente.getProximo();
		for (int i = 0; i < quantidade; i++) {
			if (condicional.test(atual.getItem())) {
				resultado.enfileirar(atual.getItem());
			}
			atual = atual.getProximo();
		}
		return resultado;
	}

	@Override
	public String toString() {
		StringBuilder filaTexto = new StringBuilder();
		Celula<E> atual = frente.getProximo();
		while (atual != null) {
			filaTexto.append(atual.getItem()).append('\n');
			atual = atual.getProximo();
		}
		return filaTexto.toString();
	}
}
