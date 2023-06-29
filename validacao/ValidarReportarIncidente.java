package validacao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ValidarReportarIncidente {
	
	public ValidarReportarIncidente() {
		
	}
	
	public boolean validarRodovia(JsonObject jsonObject) {
		JsonElement rodoviaElement = jsonObject.get("rodovia");
		return (rodoviaElement != null && !rodoviaElement.isJsonNull()) ? (!rodoviaElement.getAsString().equals("")) : false;
	}
	
	public boolean validarKm(JsonObject jsonObject) {
		JsonElement kmElement = jsonObject.get("km");
		return (kmElement != null && !kmElement.isJsonNull()) ? (kmElement.getAsInt() > 0) : false;
	}
	
	public boolean validarTipoIncidente(JsonObject jsonObject) {
		JsonElement tipoIncidenteElement = jsonObject.get("tipo_incidente");
		return (tipoIncidenteElement != null && !tipoIncidenteElement.isJsonNull()) ? (!tipoIncidenteElement.getAsString().equals("")) : false;
	}
	
	public boolean validarData(JsonObject jsonObject) {
		JsonElement dataElement = jsonObject.get("data");
		return (dataElement != null && !dataElement.isJsonNull()) ? (!dataElement.getAsString().equals("")) : false;
	}
	
	public boolean validarToken(JsonObject jsonObject) {
		JsonElement tokenElement = jsonObject.get("token");
		return (tokenElement != null && !tokenElement.isJsonNull()) ? (!tokenElement.getAsString().equals("")) : false;
	}
	
	public boolean validarIdUsuario(JsonObject jsonObject) {
		JsonElement idUsuarioElement = jsonObject.get("id_usuario");
		return (idUsuarioElement != null && !idUsuarioElement.isJsonNull()) ? (idUsuarioElement.getAsInt() > 0) : false;
	}
	
	public boolean validarIdIncidente(JsonObject jsonObject) {
		JsonElement idIncidenteElement = jsonObject.get("id_incidente");
		return (idIncidenteElement != null && !idIncidenteElement.isJsonNull()) ? (idIncidenteElement.getAsInt() > 0) : false;
	}
	
	public boolean validarJsonObject(JsonObject jsonObject, String tipo) {
		
		boolean temRodovia = jsonObject.has("rodovia") && !jsonObject.get("rodovia").isJsonNull();
		boolean temKm = jsonObject.has("km") && !jsonObject.get("km").isJsonNull();
		boolean temTipoIncidente = jsonObject.has("tipo_incidente") && !jsonObject.get("tipo_incidente").isJsonNull();
		boolean temData = jsonObject.has("data") && !jsonObject.get("data").isJsonNull();
		boolean temToken = jsonObject.has("token") && !jsonObject.get("token").isJsonNull();
		boolean temIdUsuario = jsonObject.has("id_usuario") && !jsonObject.get("id_usuario").isJsonNull();
		boolean temIdIncidente = jsonObject.has("id_incidente") && !jsonObject.get("id_incidente").isJsonNull();
		
		if (tipo.equals("reportar")) {
			
			return temRodovia && temKm && temTipoIncidente && temData && temToken && temIdUsuario;
		}
		
		if (tipo.equals("editar")) {
			
			return temRodovia && temKm && temTipoIncidente && temData && temToken && temIdUsuario && temIdIncidente;
		}
		
		return false;
	}
}
