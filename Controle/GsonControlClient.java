package Controle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GsonControlClient {

	private Gson gson;
	private JsonObject jsonObject;

	public GsonControlClient() {
		this.gson = new Gson();
	}

	public String login(String email, String senha) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 3);
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("senha", hashed(senha));
		String gsonString = gson.toJson(jsonObject);
		return gsonString;

	}
	
	public String cadastro(String nome, String email, String senha) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 1);
		jsonObject.addProperty("nome", nome);
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("senha", hashed(senha));
		String gsonString = gson.toJson(jsonObject);
		return gsonString;

	}
	
	public String atualizarCadastro(String nome, String email, String senha, String token, int id_usuario) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 2);
		jsonObject.addProperty("nome", nome);
		jsonObject.addProperty("email", email);
		jsonObject.addProperty("senha", hashed(senha));
		jsonObject.addProperty("token", token);
		jsonObject.addProperty("id_usuario", id_usuario);
		String gsonString = gson.toJson(jsonObject);
		return gsonString;

	}

	public String logout(int id_usuario, String token) {

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id_operacao", 9);
		jsonObject.addProperty("token", token);
		jsonObject.addProperty("id_usuario", id_usuario);
		String gsonString = gson.toJson(jsonObject);
		return gsonString;
	}
	

	public String reportarIncidente(String rodovia, int km, int tipo_incidente, String token, int id_usuario) {
		
		JsonObject jsonObject = new JsonObject();
		
		LocalDateTime agora = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dataAtual = formatter.format(agora);

		jsonObject.addProperty("id_operacao", 4);
		jsonObject.addProperty("data", dataAtual);
		jsonObject.addProperty("rodovia", rodovia);
		jsonObject.addProperty("km", km);
		jsonObject.addProperty("tipo_incidente", tipo_incidente);
		jsonObject.addProperty("token", token);
		jsonObject.addProperty("id_usuario", id_usuario);
		String gsonString = gson.toJson(jsonObject);
		return gsonString;
	}
	
	public String solicitarListaIncidentes(String rodovia, String faixa_Km, String data, int periodo) {
		
		jsonObject = new JsonObject();
		
		jsonObject.addProperty("id_operacao", 5);
		jsonObject.addProperty("rodovia", rodovia);
		
		if (data.matches("^\\d{2}-\\d{2}-\\d{4}$")) data = arrumarData(data);
		jsonObject.addProperty("data", data);
		jsonObject.addProperty("faixa_km", faixa_Km);
		jsonObject.addProperty("periodo", periodo);
		
		String gsonString = gson.toJson(jsonObject);
		
		return gsonString;
	}
	
	public String arrumarData(String data) {
		String[] partes = data.split(" ");

		String dataSeparada = partes[0];
		
		String[] partesData = dataSeparada.split("-");
		
		String dia = partesData[0];
		String mes = partesData[1];
		String ano = partesData[2];

		String dataNova = ano + "-" + mes + "-" + dia;

		return dataNova;

	}

	public String hashed(String pswd) {

		String hashed = "";

		for (int i = 0; i < pswd.length(); i++) {
			char c = pswd.charAt(i);
			int asciiValue = (int) c;
			int novoAsciiValue = asciiValue + pswd.length();
			if (novoAsciiValue > 127) {
				novoAsciiValue = novoAsciiValue - 127 + 32;
			}
			char novoCaractere = (char) novoAsciiValue;
			hashed += novoCaractere;
		}
		return hashed;
	}
}