package entities;

import java.time.LocalTime;

public enum Periodo {
	MANHÃ(1, "manhã", "06:00", "11:59"), 
	TARDE(2, "tarde", "12:00", "17:59"), 
	NOITE(3, "noite", "18:00", "23:59"),
	MADRUGADA(4, "madrugada", "00:00", "05:59");

	private int numPeriodo;
	private String descricao;
	private LocalTime horaInicio;
	private LocalTime horaFim;

	Periodo(int numPeriodo, String descricao, String horaInicio, String horaFim) {
		this.numPeriodo = numPeriodo;
		this.descricao = descricao;
		this.horaInicio = LocalTime.parse(horaInicio);
		this.horaFim = LocalTime.parse(horaFim);
	}

	public String getDescricao() {
		return descricao;
	}

	public LocalTime getHoraInicio() {
		return horaInicio;
	}

	public LocalTime getHoraFim() {
		return horaFim;
	}
	
	public int getNumPeriodo() {
		return numPeriodo;
	}
}
