package entities;

public class IncidenteSemId {
	
	private String data;
	private String rodovia;
	private int km;
	private int tipo_incidente;
	private int id_incidente;
	
	public IncidenteSemId(String data, String rodovia, int km, int tipo_incidente, int id_incidente) {
		this.data = data;
		this.rodovia = rodovia;
		this.km = km;
		this.tipo_incidente = tipo_incidente;
		this.id_incidente = id_incidente;
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
	
	public int getId_incidente() {
		return id_incidente;
	}

}
