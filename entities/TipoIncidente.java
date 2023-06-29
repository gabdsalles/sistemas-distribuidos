package entities;

public enum TipoIncidente {
	VENTO(1, "Vento"), 
	CHUVA(2, "Chuva"), 
	NEVOEIRO(3, "Nevoeiro (neblina)"), 
	NEVE(4, "Neve"), 
	GELO(5, "Gelo na pista"),
	GRANIZO(6, "Granizo"), 
	TRANSITO_PARADO(7, "Trânsito parado"), 
	FILAS_TRANSITO(8, "Filas de trânsito"),
	TRANSITO_LENTO(9, "Trânsito lento"), 
	ACIDENTE_DESCONHECIDO(10, "Acidente desconhecido"),
	INCIDENTE_DESCONHECIDO(11, "Incidente desconhecido"), 
	TRABALHOS_ESTRADA(12, "Trabalhos na estrada"),
	BLOQUEIO_PISTA(13, "Bloqueio de pista"),
	BLOQUEIO_ESTRADA(14, "Bloqueio de estrada");

	private int numero;
	private String descricao;

	TipoIncidente(int numero, String descricao) {
		this.numero = numero;
		this.descricao = descricao;
	}

	public int getNumero() {
		return numero;
	}

	public String getDescricao() {
		return descricao;
	}
}
