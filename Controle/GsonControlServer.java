package Controle;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class GsonControlServer {

	private Gson gson;
	ArrayList<Usuario> listaUsuarios;
	ArrayList<Incidente> listaIncidentes;
	ArrayList<Usuario> listaUsuariosConectados;
	int contIncidentes = 2;

	public GsonControlServer() {
		this.gson = new Gson();
		this.listaUsuarios = new ArrayList<Usuario>();
		this.listaIncidentes = new ArrayList<Incidente>();
		this.listaUsuariosConectados = new ArrayList<Usuario>();
		Usuario user1 = new Usuario("Gabriel", "teste@testando.com", "|nwqj:;<="); // senha1234
		Usuario user2 = new Usuario("Lukas", "lukaskenji@gmail.com", "t}si{9:;");
		Incidente incidente1 = new Incidente("2023-05-26 14:50:31", "BR-277", 41, 7, 0, 1);
		user1.setId_usuario(0);
		user2.setId_usuario(1);
		listaUsuarios.add(user1);
		listaUsuarios.add(user2);
		listaIncidentes.add(incidente1);
	}

	public String cadastro(JsonObject jsonObject, int contUsuarios) {

		String nome = jsonObject.get("nome").getAsString();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		Usuario usuario1 = new Usuario(nome, email, senha);
		JsonObject cadastro = new JsonObject();
		boolean verification = false;
		int codigo;
		String gsonString;

		for (Usuario usuario : listaUsuarios) {
			if (usuario.getEmail() == email) {
				verification = true;
			}
		}

		if (verification) { // já tem um cadastro na lista
			codigo = 500;
			cadastro.addProperty("codigo", codigo);
			cadastro.addProperty("mensagem", "Este email ja esta cadastrado!");
			gsonString = gson.toJson(cadastro);
		} else {
			if (this.verificarCadastro(usuario1)) {

				codigo = 200;
				cadastro.addProperty("codigo", codigo);
				gsonString = gson.toJson(cadastro);
				usuario1.setId_usuario(contUsuarios);
				listaUsuarios.add(usuario1);
				contUsuarios++;
			} else {
				codigo = 500;
				cadastro.addProperty("codigo", codigo);
				cadastro.addProperty("mensagem", "Cadastro invalido!");
				gsonString = gson.toJson(cadastro);
			}
		}

		return gsonString;
	}

	public String login(JsonObject jsonObject) {
		String email2 = jsonObject.get("email").getAsString();
		String senha2 = jsonObject.get("senha").getAsString();
		int id_usuario = -1;
		String token = "";
		boolean verificacao = false;

		for (Usuario usuario11 : listaUsuarios) {

			if (usuario11.getEmail().equals(email2) && usuario11.getSenha().equals(senha2)) {
				token = this.gerarToken();
				usuario11.setToken(token);
				id_usuario = usuario11.getId_usuario();
				verificacao = true;
			}

		}

		String gsonString;
		int codigo;
		JsonObject login = new JsonObject();

		if (verificacao) {
			login.addProperty("codigo", 200);
			login.addProperty("token", listaUsuarios.get(id_usuario).getToken());
			login.addProperty("id_usuario", id_usuario);
			gsonString = gson.toJson(login);
			listaUsuariosConectados.add(listaUsuarios.get(id_usuario));
		} else {

			codigo = 500;
			login.addProperty("codigo", codigo);
			login.addProperty("mensagem", "Erro: e-mail ou senha invalidos!");
			gsonString = gson.toJson(login);
		}

		return gsonString;
	}

	public boolean checkLogin(String gsonString) {
		JsonObject jsonObject = JsonParser.parseString(gsonString).getAsJsonObject();
		int codigo = jsonObject.get("codigo").getAsInt();
		if (codigo == 200)
			return true;
		else
			return false;

	}

	public String logout(JsonObject jsonObject) {
		JsonObject logout = new JsonObject();
		String token;
		int id_usuario;
		String gsonString = "";

		try {
			JsonElement tokenElement = jsonObject.get("token");
			token = (tokenElement != null && !tokenElement.isJsonNull()) ? tokenElement.getAsString() : "";
			JsonElement id_usuario_Element = jsonObject.get("id_usuario");
			id_usuario = (id_usuario_Element != null && !id_usuario_Element.isJsonNull())
					? id_usuario_Element.getAsInt()
					: -1;

			if (id_usuario <= listaUsuarios.size() && id_usuario >= 0) { // id usuario valido?

				if (listaUsuarios.get(id_usuario).getToken().equals(token)) { // token valido
					listaUsuarios.get(id_usuario).setToken(null);
					logout.addProperty("codigo", 200);
					listaUsuariosConectados.remove(listaUsuarios.get(id_usuario));
					gsonString = gson.toJson(logout);
				} else {
					logout.addProperty("codigo", 500);
					logout.addProperty("Mensagem", "Erro no logout: usuário não está logado.");
					gsonString = gson.toJson(logout);
				}

			} else {
				logout.addProperty("codigo", 500);
				logout.addProperty("Mensagem", "Erro no logout: usuário não está logado.");
				gsonString = gson.toJson(logout);
			}
		} catch (NullPointerException e) {
			logout.addProperty("codigo", 500);
			logout.addProperty("Mensagem",
					"Erro no logout: token ou usuário são nulos. Logo, não é possível deslogar o usuário.");
			gsonString = gson.toJson(logout);
		}

		return gsonString;

	}

	public String reportarIncidente(JsonObject jsonObject) {

		String rodovia = jsonObject.get("rodovia").getAsString();
		int km = jsonObject.get("km").getAsInt();
		int tipo_incidente = jsonObject.get("tipo_incidente").getAsInt();
		String data = jsonObject.get("data").getAsString();
		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();

		JsonObject mensagem = new JsonObject();
		String gsonString = "";

		if (jsonObject.get("rodovia").isJsonNull() || jsonObject.get("km").isJsonNull()
				|| jsonObject.get("tipo_incidente").isJsonNull() || jsonObject.get("data").isJsonNull()) {
			mensagem.addProperty("codigo", 500);
			mensagem.addProperty("mensagem", "Todos os campos são obrigatórios!");
		} else {

			boolean verificar = false;
			for (Usuario usuario : listaUsuarios) {

				if (usuario.getId_usuario() == id_usuario && usuario.getToken().equals(token)) {
					Incidente e = new Incidente(data, rodovia, km, tipo_incidente, id_usuario, contIncidentes);
					listaIncidentes.add(e);
					contIncidentes++;
					mensagem.addProperty("codigo", 200);
					verificar = true;
				}
			}

			if (!verificar) {
				mensagem.addProperty("codigo", 500);
				mensagem.addProperty("mensagem", "Token inválido!");
			}

		}

		gsonString = gson.toJson(mensagem);
		return gsonString;
	}

	public String solicitarListaIncidentes(JsonObject jsonObject) {

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);
		String gsonString;

		String rodovia = jsonObject.get("rodovia").getAsString();
		JsonElement tokenElement = jsonObject.get("faixa_km");
		String faixa_km = (tokenElement != null && !tokenElement.isJsonNull()) ? tokenElement.getAsString() : "";
		int km_inicial = -1;
		int km_final = -1;

		if (!faixa_km.equals("")) {
			//faixa_km = jsonObject.get("faixa_km").getAsString();
			String[] partes = faixa_km.split("-");

			km_inicial = Integer.parseInt(partes[0]);
			km_final = Integer.parseInt(partes[1]);

			if (!validarKm(km_inicial) || !validarKm(km_final)) {
				mensagemErro.addProperty("mensagem", "Faixa de km inválida!");
				gsonString = gson.toJson(mensagemErro);
				return gsonString;
			}

		}
		

		String data = jsonObject.get("data").getAsString();
		int periodo = jsonObject.get("periodo").getAsInt();
		
		if (data.equals("") || data.equals("  -  -    ")) {
			mensagemErro.addProperty("mensagem", "Data vazia!");
			gsonString = gson.toJson(mensagemErro);
			return gsonString;
		}

		if (!validarRodovia(rodovia)) {
			mensagemErro.addProperty("mensagem", "Rodovia inválida");
			gsonString = gson.toJson(mensagemErro);
			return gsonString;
		} else if (!validarData(data)) {
			mensagemErro.addProperty("mensagem", "Data inválida");
			gsonString = gson.toJson(mensagemErro);
			return gsonString;
		} else if (!validarPeriodo(periodo)) {
			mensagemErro.addProperty("mensagem", "Período inválido!");
			gsonString = gson.toJson(mensagemErro);
			return gsonString;

		} else {
			JsonArray lista = criarListaIncidentes(rodovia, data, periodo, km_inicial, km_final);
			JsonObject mensagemSucesso = new JsonObject();
			mensagemSucesso.addProperty("codigo", 200);
			if (lista.isEmpty()) System.out.println("lista vazia");
			mensagemSucesso.add("lista_incidentes", lista);
			gsonString = gson.toJson(mensagemSucesso);
			return gsonString;
		}

	}

	public JsonArray criarListaIncidentes(String rodovia, String data, int periodo, int km_inicial,
			int km_final) {

		JsonArray listaJson = new JsonArray();
		
		Periodo[] periodos = Periodo.values();

		Periodo periodoSelecionado = null;
		for (Periodo p : periodos) {
		    if (p.getNumPeriodo() == periodo) {
		        periodoSelecionado = p;
		        break;
		    }
		}

		LocalTime inicioPeriodo = periodoSelecionado.getHoraInicio();
		LocalTime finalPeriodo = periodoSelecionado.getHoraFim();

		String[] partes = data.split(" ");
		String dataFormatada = partes[0];

		// busca com faixa_km
		if (km_inicial != -1 && km_final != -1) { // usuário inseriu a faixa

			for (Incidente incidente : listaIncidentes) {

				String parteHora[] = incidente.getData().split(" ");
				String dataIncidente = parteHora[0];
				String horaIncidente = parteHora[1];
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				LocalTime timeIncidente = LocalTime.parse(horaIncidente, formatter);
				//System.out.println("Km inicial: " + km_inicial + " /km final: " + km_final);

				if (incidente.getRodovia().equals(rodovia) && incidente.getKm() >= km_inicial
						&& incidente.getKm() <= km_final && dataIncidente.equals(dataFormatada)
						&& timeIncidente.isAfter(inicioPeriodo) && timeIncidente.isBefore(finalPeriodo)) {
					IncidenteSemId novoIncidente = new IncidenteSemId(incidente.getData(), incidente.getRodovia(), incidente.getKm(), incidente.getTipo_incidente(), incidente.getId_incidente());
					String incidenteJson = gson.toJson(novoIncidente);
			        listaJson.add(gson.fromJson(incidenteJson, JsonObject.class));
				} else {
				}
			}
		} else {
			for (Incidente incidente : listaIncidentes) {

				String parteHora[] = incidente.getData().split(" ");
				String dataIncidente = parteHora[0];
				String horaIncidente = parteHora[1];
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				LocalTime timeIncidente = LocalTime.parse(horaIncidente, formatter);

				if (incidente.getRodovia().equals(rodovia) && dataIncidente.equals(dataFormatada)
						&& timeIncidente.isAfter(inicioPeriodo) && timeIncidente.isBefore(finalPeriodo)) {
					//lista.add(incidente);
					
					IncidenteSemId novoIncidente = new IncidenteSemId(incidente.getData(), incidente.getRodovia(), incidente.getKm(), incidente.getTipo_incidente(), incidente.getId_incidente());
					String incidenteJson = gson.toJson(novoIncidente);
			        listaJson.add(gson.fromJson(incidenteJson, JsonObject.class));
				}
			}

		}
		

		return listaJson;
	}

	public String atualizarCadastro(JsonObject jsonObject) {

		String gsonString = "";
		JsonObject mensagem = new JsonObject();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		String nome = jsonObject.get("nome").getAsString();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		String token = jsonObject.get("token").getAsString();

		if (listaUsuarios.get(id_usuario).getToken().equals(token)) {

			boolean verification = false;
			Usuario usuario1 = new Usuario(nome, email, senha);
			String novoToken = this.gerarToken();

			for (Usuario usuario : listaUsuarios) {
				if (usuario.getEmail() == email && usuario.getId_usuario() != id_usuario) {
					verification = true;
				}
			}

			if (verification) { // já tem um cadastro na lista
				mensagem.addProperty("codigo", 500);
				mensagem.addProperty("mensagem", "Este email ja esta cadastrado!");
				gsonString = gson.toJson(mensagem);
			} else {

				if (this.verificarCadastro(usuario1)) {
					listaUsuarios.get(id_usuario).setNome(nome);
					listaUsuarios.get(id_usuario).setEmail(email);
					listaUsuarios.get(id_usuario).setSenha(senha);
					listaUsuarios.get(id_usuario).setToken(novoToken);
					mensagem.addProperty("codigo", 200);
					mensagem.addProperty("token", novoToken);
					gsonString = gson.toJson(mensagem);
				} else {
					mensagem.addProperty("codigo", 500);
					mensagem.addProperty("mensagem", "Atualização de cadastro inválida!");
					gsonString = gson.toJson(mensagem);
				}
			}

		}

		return gsonString;
	}

	public String gerarToken() {
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < 36; i++) {
			int indice = random.nextInt(caracteres.length());
			char caractereAleatorio = caracteres.charAt(indice);
			sb.append(caractereAleatorio);
		}

		return sb.toString();
	}

	public boolean verificarCadastro(Usuario usuario) {

		if (validarNome(usuario.getNome()) && validarEmail(usuario.getEmail()) && validarSenha(usuario.getSenha())) {
			return true;

		} else
			return false;

	}

	public boolean validarNome(String nome) {

		if (nome.length() >= 3 && nome.length() <= 32) {
			// return nome.matches("[a-zA-Z ]+");
			return nome.matches("^[\\P{Digit}\\p{all}]+$");

		} else
			return false;
	}

	public boolean validarEmail(String email) {

		if (email.length() >= 16 && email.length() <= 50 && email.contains("@")) {
			return true;
		} else
			return false;
	}

	public boolean validarSenha(String senha) {
		if (senha.length() >= 8 && senha.length() <= 32) {
			return true;
		} else
			return false;

	}

	public boolean validarRodovia(String rodovia) {
		return rodovia.matches("^[A-Za-z]{2}-[0-9]{1,3}$");

	}

	public boolean validarKm(int km) {
		if (km >= 0 && km <= 999)
			return true;
		else
			return false;
	}

	public boolean validarData(String data) {

		String[] partes = data.split(" ");

		String dataFormatada = partes[0];

		String[] partesData = dataFormatada.split("-");

		int ano = Integer.parseInt(partesData[0]);
		int mes = Integer.parseInt(partesData[1]);
		int dia = Integer.parseInt(partesData[2]);

		if (ano >= 0 && ano <= 2023 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31) {

			return true;
		} else
			return false;

	}

	public boolean validarPeriodo(int periodo) {

		if (periodo >= 1 && periodo <= 4) return true; else return false;

	}

}
