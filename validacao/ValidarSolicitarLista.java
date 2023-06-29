package validacao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ValidarSolicitarLista {
	
	public ValidarSolicitarLista() {}
	
	public boolean validarRodovia(JsonObject jsonObject) {
		JsonElement rodoviaElement = jsonObject.get("rodovia");
		return (rodoviaElement != null && !rodoviaElement.isJsonNull()) ? (!rodoviaElement.getAsString().equals("")) : false;
	}
	
	public boolean validarData(JsonObject jsonObject) {
		JsonElement dataElement = jsonObject.get("data");
		return (dataElement != null && !dataElement.isJsonNull()) ? (!dataElement.getAsString().equals("")) : false;
	}
	
	public boolean validarPeriodo(JsonObject jsonObject) {
		JsonElement periodoElement = jsonObject.get("periodo");
		return (periodoElement != null && !periodoElement.isJsonNull()) ? (!periodoElement.getAsString().equals("")) : false;
	}
	
	public boolean validarJsonObject(JsonObject jsonObject) {
		
		boolean temRodovia = jsonObject.has("rodovia") && !jsonObject.get("rodovia").isJsonNull();
		boolean temData = jsonObject.has("data") && !jsonObject.get("data").isJsonNull();
		boolean temPeriodo = jsonObject.has("periodo") && !jsonObject.get("periodo").isJsonNull();
		
		return temRodovia && temData && temPeriodo;
	}
	
	

}
