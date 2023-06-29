package validacao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ValidarCadastro {
	
	public ValidarCadastro() {
		
	}
	
	public boolean validarNome(JsonObject jsonObject) {
		
		JsonElement nomeElement = jsonObject.get("nome");
		
		return (nomeElement != null && !nomeElement.isJsonNull()) ? (!nomeElement.getAsString().equals("")) : false;

	}
	
	public boolean verificarEmail(String email) {
		return email.contains("@");
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
	    boolean temNome = jsonObject.has("nome") && !jsonObject.get("nome").isJsonNull();
	    boolean temEmail = jsonObject.has("email") && !jsonObject.get("email").isJsonNull();
	    boolean temSenha = jsonObject.has("senha") && !jsonObject.get("senha").isJsonNull();
	    
	    return temNome && temEmail && temSenha;
	}



}
