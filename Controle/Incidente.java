package Controle;

public class Incidente {
	
	private String data;
	private String rodovia;
	private int km;
	private int tipo_incidente;
	private int id_usuario;
	
	public Incidente(String data, String rodovia, int km, int tipo_incidente, int id_usuario) {
		this.data = data;
		this.rodovia = rodovia;
		this.km = km;
		this.tipo_incidente = tipo_incidente;
		this.id_usuario = id_usuario;
	}

	public String getData() {
		return data;
	}

	public String getRodovia() {
		return rodovia;
	}

	public int getKm() {
		return km;
	}

	public int getTipo_incidente() {
		return tipo_incidente;
	}
	

	public int getId_usuario() {
		return id_usuario;
	}
	
}
