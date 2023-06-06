package Controle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dao.BancoDeDados;

import com.google.gson.JsonArray;

public class GsonControlServer {

	private Gson gson;
	ArrayList<Usuario> listaUsuarios;
	ArrayList<Incidente> listaIncidentes;
	ArrayList<Usuario> listaUsuariosConectados;
	// int contIncidentes = 2;

	public GsonControlServer() {
		this.gson = new Gson();
		this.listaUsuarios = new ArrayList<Usuario>();
		this.listaIncidentes = new ArrayList<Incidente>();
		this.listaUsuariosConectados = new ArrayList<Usuario>();
	}

	public boolean verificarExistenciaEmail(String email, Connection conexao) throws SQLException {

		PreparedStatement st = null;
		ResultSet rs = null;

		st = conexao.prepareStatement("SELECT * FROM usuario WHERE email = ?");
		st.setString(1, email);
		rs = st.executeQuery();

		if (rs.next()) {
			return true; // se já tem um email
		}

		BancoDeDados.finalizarStatement(st);
		BancoDeDados.finalizarResultSet(rs);

		return false; // se não tem
	}

	public String cadastro(JsonObject jsonObject, int contUsuarios, Connection conexao) throws SQLException {

		String nome = jsonObject.get("nome").getAsString();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();

		PreparedStatement st = null;

		Usuario usuario1 = new Usuario(nome, email, senha);
		JsonObject cadastro = new JsonObject();
		boolean verification = verificarExistenciaEmail(email, conexao);
		String gsonString;

		if (verification) { // já tem um cadastro na lista

			cadastro.addProperty("codigo", 500);
			cadastro.addProperty("mensagem", "Este email ja esta cadastrado!");
			gsonString = gson.toJson(cadastro);

		} else {
			if (this.verificarCadastro(usuario1)) {

				cadastro.addProperty("codigo", 200);
				gsonString = gson.toJson(cadastro);

				try {

					st = conexao.prepareStatement("insert into usuario (nome, email, senha, token) values (?,?,?,?)");
					st.setString(1, nome);
					st.setString(2, email);
					st.setString(3, senha);
					st.setString(4, "");

					st.executeUpdate();

				} finally {

					BancoDeDados.finalizarStatement(st);
					//BancoDeDados.desconectar();

				}
			} else {
				cadastro.addProperty("codigo", 500);
				cadastro.addProperty("mensagem", "Cadastro invalido!");
				gsonString = gson.toJson(cadastro);
			}
		}

		return gsonString;
	}

	public String login(JsonObject jsonObject, Connection conexao) throws SQLException {

		String email2 = jsonObject.get("email").getAsString();
		String senha2 = jsonObject.get("senha").getAsString();
		int id_usuario = -1;
		String token = "";

		PreparedStatement st = null;
		PreparedStatement st2 = null;
		ResultSet rs = null;
		JsonObject login = new JsonObject();
		String gsonString;

		try {
			st = conexao.prepareStatement("SELECT * FROM usuario WHERE email = ? AND senha = ?");
			st.setString(1, email2);
			st.setString(2, senha2);
			rs = st.executeQuery();

			if (rs.next()) {
				id_usuario = rs.getInt("id_usuario");
				token = this.gerarToken();
				st2 = conexao.prepareStatement("UPDATE usuario SET token = ? WHERE id_usuario = ?");
				st2.setString(1, token);
				st2.setInt(2, id_usuario);
				st2.executeUpdate();
				login.addProperty("codigo", 200);
				login.addProperty("token", token);
				login.addProperty("id_usuario", id_usuario);
				gsonString = gson.toJson(login);
			} else {

				login.addProperty("codigo", 500);
				login.addProperty("mensagem", "Erro: e-mail ou senha invalidos!");
				gsonString = gson.toJson(login);

			}

		} finally {
			BancoDeDados.finalizarStatement(st);
			BancoDeDados.finalizarStatement(st2);
			BancoDeDados.finalizarResultSet(rs);
		}

		return gsonString;
	}

	public String logout(JsonObject jsonObject, Connection conexao) throws SQLException {
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

			PreparedStatement st = null;
			ResultSet rs = null;

				st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? and token = ?");
				st.setInt(1, id_usuario);
				st.setString(2, token);
				rs = st.executeQuery();

				if (rs.next()) {
					String tokenBanco = rs.getString("token");
					if (tokenBanco.equals(token)) {
						st = conexao.prepareStatement("UPDATE usuario SET token = ? WHERE id_usuario = ?");
						st.setString(1, "");
						st.setInt(2, id_usuario);
						st.executeUpdate();
						logout.addProperty("codigo", 200);
						gsonString = gson.toJson(logout);
					} else {
						logout.addProperty("codigo", 500);
						logout.addProperty("Mensagem", "Erro no logout: usuário não está logado.");
						gsonString = gson.toJson(logout);
					}
				} else {
					logout.addProperty("codigo", 500);
					logout.addProperty("Mensagem", "Erro no logout: usuário inexistente");
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

	public String reportarIncidente(JsonObject jsonObject, Connection conexao) throws SQLException {

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

			PreparedStatement st = null;
			ResultSet rs = null;

			try {

				st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?"); // verificar
																											// usuário e
																											// token
				st.setInt(1, id_usuario);
				st.setString(2, token);

				rs = st.executeQuery();

				if (rs.next()) {
					Incidente e = new Incidente(data, rodovia, km, tipo_incidente, id_usuario);
					inserirIncidenteBD(e, conexao); // inserir no banco
					mensagem.addProperty("codigo", 200);
					verificar = true;
				}
			} finally {
				if (!verificar) {
					mensagem.addProperty("codigo", 500);
					mensagem.addProperty("mensagem", "Token inválido!");
				}

				gsonString = gson.toJson(mensagem);
			}

		}

		return gsonString;
	}

	public void inserirIncidenteBD(Incidente e, Connection conexao) throws SQLException {

		PreparedStatement st = null;

		st = conexao.prepareStatement(
				"insert into incidente (data, rodovia, km, tipo_incidente, id_usuario) values (?,?,?,?,?)");
		st.setString(1, e.getData());
		st.setString(2, e.getRodovia());
		st.setInt(3, e.getKm());
		st.setInt(4, e.getTipo_incidente());
		st.setInt(5, e.getId_usuario());

		st.executeUpdate();
	}

	public String solicitarListaIncidentes(JsonObject jsonObject) throws SQLException, IOException {

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);
		String gsonString;

		String rodovia = jsonObject.get("rodovia").getAsString();
		JsonElement faixaKmElement = jsonObject.get("faixa_km");
		String faixa_km = (faixaKmElement != null && !faixaKmElement.isJsonNull()) ? faixaKmElement.getAsString() : "";
		int km_inicial = -1;
		int km_final = -1;

		if (!faixa_km.equals("")) {
			// faixa_km = jsonObject.get("faixa_km").getAsString();
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
			mensagemSucesso.add("lista_incidentes", lista);
			gsonString = gson.toJson(mensagemSucesso);
			return gsonString;
		}

	}

	public String solicitarMeusIncidentes(JsonObject jsonObject) throws SQLException, IOException {
		
		//depois, verificar NULL
		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		
		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);
		String gsonString;
		JsonArray listaJson = new JsonArray();
		
		//verificar token
		PreparedStatement st = null;
		ResultSet rs = null, rs2 = null;
		Connection conexao = BancoDeDados.conectar();

			st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?");																						// token
			st.setInt(1, id_usuario);
			st.setString(2, token);

			rs = st.executeQuery();

			if (rs.next()) { //se o token estiver correto, pega o id_usuario e faz uma query no banco
				st = conexao.prepareStatement("SELECT * FROM incidente WHERE id_usuario = ?");
				st.setInt(1, id_usuario);
				rs2 = st.executeQuery();
				
				while (rs2.next()) { //montar lista de incidentes
					
					String dataHoraBanco = rs2.getTimestamp("data").toString();
		        	dataHoraBanco = dataHoraBanco.substring(0, dataHoraBanco.length() - 2);
					
					IncidenteSemId novoIncidente = new IncidenteSemId(dataHoraBanco,
							 rs2.getString("rodovia"),
							 rs2.getInt("km"), rs2.getInt("tipo_incidente"), rs2.getInt("id_incidente"));
							 String incidenteJson = gson.toJson(novoIncidente);
							 listaJson.add(gson.fromJson(incidenteJson, JsonObject.class));
				}
				
				//retornar ao usuário
				JsonObject mensagemSucesso = new JsonObject();
				mensagemSucesso.addProperty("codigo", 200);
				mensagemSucesso.add("lista_incidentes", listaJson);
				gsonString = gson.toJson(mensagemSucesso);
				return gsonString;
				
				
				
			} else {
				
				mensagemErro.addProperty("mensagem", "Token inválido!");
				//BancoDeDados.desconectar();
				gsonString = gson.toJson(mensagemErro);
				return gsonString;
			}
		
		}

	public JsonArray criarListaIncidentes(String rodovia, String data, int periodo, int km_inicial, int km_final) throws SQLException, IOException {

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

		
		PreparedStatement stmt = null;
		Connection conexao = BancoDeDados.conectar();
		ResultSet rs = null;
		
		 if (km_inicial != -1 && km_final != -1) { //com faixa km
             stmt = conexao.prepareStatement("SELECT * FROM incidente WHERE rodovia = ? AND km BETWEEN ? AND ?");
             stmt.setString(1, rodovia);
             stmt.setInt(2, km_inicial);
             stmt.setInt(3, km_final);
             
         } else { //sem faixa km
             stmt = conexao.prepareStatement("SELECT * FROM incidente WHERE rodovia = ?");
             stmt.setString(1, rodovia);
         }

         rs = stmt.executeQuery();
         
         while (rs.next()) {
        	 
        	 String dataHoraBanco = rs.getTimestamp("data").toString();
        	 dataHoraBanco = dataHoraBanco.substring(0, dataHoraBanco.length() - 2);
        	 
        	 String partesBanco[] = dataHoraBanco.split(" ");
        	 
        	 String dataBanco = partesBanco[0];
        	 String horaBanco = partesBanco[1];
        	 
             
        	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        	 LocalTime timeIncidente = LocalTime.parse(horaBanco, formatter);
        	 
        	 
        	 if (dataBanco.equals(dataFormatada) && timeIncidente.isAfter(inicioPeriodo) && timeIncidente.isBefore(finalPeriodo)) {
        		 IncidenteSemId novoIncidente = new IncidenteSemId(dataHoraBanco,
					 rs.getString("rodovia"),
					 rs.getInt("km"), rs.getInt("tipo_incidente"), rs.getInt("id_incidente"));
					 String incidenteJson = gson.toJson(novoIncidente);
					 listaJson.add(gson.fromJson(incidenteJson, JsonObject.class));
        	 }
        	 
         }


		return listaJson;
	}

	public String atualizarCadastro(JsonObject jsonObject, Connection conexao) throws SQLException {

		String gsonString = "";
		JsonObject mensagem = new JsonObject();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		String nome = jsonObject.get("nome").getAsString();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		String token = jsonObject.get("token").getAsString();

		PreparedStatement st = null;
		ResultSet rs = null;

		st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?");
		st.setInt(1, id_usuario);
		st.setString(2, token);
		rs = st.executeQuery();

		if (rs.next()) {

			Usuario usuario1 = new Usuario(nome, email, senha);
			if (this.verificarCadastro(usuario1)) {
				
				String novoToken = this.gerarToken();
				st = conexao.prepareStatement("UPDATE usuario SET nome = ?, email = ?, senha = ?, token = ? WHERE id_usuario = ?");
				st.setString(1, nome);
				st.setString(2, email);
				st.setString(3, senha);
				st.setString(4, novoToken);
				st.setInt(5, id_usuario);
				st.executeUpdate();

				mensagem.addProperty("codigo", 200);
				mensagem.addProperty("token", novoToken);
				gsonString = gson.toJson(mensagem);
			}
		}

		return gsonString;
	}

	public String excluirIncidente(JsonObject jsonObject) throws SQLException, IOException {
		
		String gsonString;
		
		//depois, verificar NULL
				String token = jsonObject.get("token").getAsString();
				int id_usuario = jsonObject.get("id_usuario").getAsInt();
				int id_incidente = jsonObject.get("id_incidente").getAsInt();
				
				JsonObject mensagemErro = new JsonObject();
				mensagemErro.addProperty("codigo", 500);
		
		//verificar token
				PreparedStatement st = null;
				ResultSet rs = null, rs2 = null;
				Connection conexao = BancoDeDados.conectar();

					st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?");																						// token
					st.setInt(1, id_usuario);
					st.setString(2, token);

					rs = st.executeQuery();

					if (rs.next()) { //existe token
						st = conexao.prepareStatement("DELETE FROM incidente WHERE id_incidente = ?");
						st.setInt(1, id_incidente);
						st.executeUpdate();
						
						JsonObject mensagemSucesso = new JsonObject();
						mensagemSucesso.addProperty("codigo", 200);
						gsonString = gson.toJson(mensagemSucesso);
						//BancoDeDados.desconectar();
						return gsonString;
						
					} else {
						
						mensagemErro.addProperty("mensagem", "Token inválido");
						gsonString = gson.toJson(mensagemErro);
						//BancoDeDados.conectar();
						return gsonString;
					}
		
	}
	
	public String editarIncidente(JsonObject jsonObject) throws SQLException, IOException {
		
		//depois, verificar NULL
		
		String rodovia = jsonObject.get("rodovia").getAsString();
		int km = jsonObject.get("km").getAsInt();
		int tipo_incidente = jsonObject.get("tipo_incidente").getAsInt();
		String data = jsonObject.get("data").getAsString();
		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		int id_incidente = jsonObject.get("id_incidente").getAsInt();

		JsonObject mensagem = new JsonObject();
		JsonObject erro = new JsonObject();
		String gsonString = "";
		
		//validar rodovia, km, tipo_incidente, data, id_incidente
		
		//banco de dados
		
		PreparedStatement st = null;
		ResultSet rs = null, rs2 = null;
		Connection conexao = BancoDeDados.conectar();

			st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?"); //verificar se token é válido																					// token
			st.setInt(1, id_usuario);
			st.setString(2, token);

			rs = st.executeQuery();

			if (rs.next()) { //existe token
				st = conexao.prepareStatement("UPDATE incidente SET rodovia = ?, km = ?, tipo_incidente = ?, data = ? WHERE id_usuario = ? AND id_incidente = ?");
				st.setString(1, rodovia);
				st.setInt(2, km);
				st.setInt(3, tipo_incidente);
				st.setString(4, data);
				st.setInt(5, id_usuario);
				st.setInt(6, id_incidente);
				st.executeUpdate();
				
				JsonObject mensagemSucesso = new JsonObject();
				mensagemSucesso.addProperty("codigo", 200);
				gsonString = gson.toJson(mensagemSucesso);
				//BancoDeDados.desconectar();
				return gsonString;
			} else {
				
				erro.addProperty("codigo", 500);
				erro.addProperty("mensagem", "Token inválido!");
				gsonString = gson.toJson(erro);
				return gsonString;
				
			}
		
		
		
	}

	public String removerCadastro(JsonObject jsonObject) throws SQLException, IOException {
		
		String gsonString;
		
		//depois, verificar NULL
				String token = jsonObject.get("token").getAsString();
				int id_usuario = jsonObject.get("id_usuario").getAsInt();
				String email = jsonObject.get("email").getAsString();
				String senha = jsonObject.get("senha").getAsString();
				
				JsonObject mensagemErro = new JsonObject();
				mensagemErro.addProperty("codigo", 500);
		
		//verificar token
				PreparedStatement st = null;
				ResultSet rs = null, rs2 = null;
				Connection conexao = BancoDeDados.conectar();

					st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? AND token = ?");																						// token
					st.setInt(1, id_usuario);
					st.setString(2, token);

					rs = st.executeQuery();

					if (rs.next()) { //existe token
						st = conexao.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?"); //deletar usuário
						st.setInt(1, id_usuario);
						st.executeUpdate();
						
						//tornar o id_usuario dos incidentes desse usuário nulo
						st = conexao.prepareStatement("UPDATE incidente SET id_usuario = NULL WHERE id_usuario = ?");
						st.setInt(1, id_usuario);
						st.executeUpdate();
						
						JsonObject mensagemSucesso = new JsonObject();
						mensagemSucesso.addProperty("codigo", 200);
						gsonString = gson.toJson(mensagemSucesso);
						//BancoDeDados.desconectar();
						return gsonString;
						
					} else {
						
						mensagemErro.addProperty("mensagem", "Token inválido");
						gsonString = gson.toJson(mensagemErro);
						//BancoDeDados.conectar();
						return gsonString;
					}
		
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

		if (periodo >= 1 && periodo <= 4)
			return true;
		else
			return false;

	}

}
