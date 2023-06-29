package validacao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ValidarLogout {
	
	public ValidarLogout() {}

	public boolean validarToken(JsonObject jsonObject) {
		JsonElement tokenElement = jsonObject.get("token");
		return (tokenElement != null && !tokenElement.isJsonNull()) ? (!tokenElement.getAsString().equals("")) : false;
	}

	public boolean validarId(JsonObject jsonObject) {
		JsonElement idElement = jsonObject.get("id_usuario");
		return (idElement != null && !idElement.isJsonNull()) ? (!idElement.getAsString().equals("")) : false;
	}
	
	public boolean validarJsonObject(JsonObject jsonObject) {
		boolean temToken = jsonObject.has("token") && !jsonObject.get("token").isJsonNull();
	    boolean temId = jsonObject.has("id_usuario") && !jsonObject.get("id_usuario").isJsonNull();
	    
	    return temToken && temId;
	}

}
