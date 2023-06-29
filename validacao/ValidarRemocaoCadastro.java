package validacao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ValidarRemocaoCadastro {
	
	public ValidarRemocaoCadastro() {}
	
	public boolean validarToken(JsonObject jsonObject) {
		JsonElement tokenElement = jsonObject.get("token");
		return (tokenElement != null && !tokenElement.isJsonNull()) ? (!tokenElement.getAsString().equals("")) : false;
	}
	
	public boolean validarIdUsuario(JsonObject jsonObject) {
		JsonElement idUsuarioElement = jsonObject.get("id_usuario");
		return (idUsuarioElement != null && !idUsuarioElement.isJsonNull()) ? (idUsuarioElement.getAsInt() > 0) : false;
	}
	
	public boolean validarEmail(JsonObject jsonObject) {
	    JsonElement emailElement = jsonObject.get("email");
	    return (emailElement != null && !emailElement.isJsonNull()) ? (!emailElement.getAsString().equals("")) : false;
	}

	public boolean validarSenha(JsonObject jsonObject) {
	    JsonElement senhaElement = jsonObject.get("senha");
	    return (senhaElement != null && !senhaElement.isJsonNull()) ? (!senhaElement.getAsString().equals("")) : false;
	}
	
	public boolean validarJsonObject(JsonObject jsonObject) {
	    boolean temEmail = jsonObject.has("email") && !jsonObject.get("email").isJsonNull();
	    boolean temSenha = jsonObject.has("senha") && !jsonObject.get("senha").isJsonNull();
	    boolean temToken = jsonObject.has("token") && !jsonObject.get("token").isJsonNull();
	    boolean temId = jsonObject.has("id_usuario") && !jsonObject.get("id_usuario").isJsonNull();
	    
	    return temEmail && temSenha && temToken && temId;
	}

}
