package controle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import dao.IncidenteBD;
import dao.UsuarioBD;
import entities.Incidente;
import entities.Usuario;
import ui.TelaServer;
import validacao.ValidarAtualizacaoCadastro;
import validacao.ValidarCadastro;
import validacao.ValidarExclusaoIncidente;
import validacao.ValidarLogin;
import validacao.ValidarLogout;
import validacao.ValidarRemocaoCadastro;
import validacao.ValidarReportarIncidente;
import validacao.ValidarSolicitarLista;
import validacao.VerificarDados;

public class GsonServidor {

	private Gson gson;
	private UsuarioBD userBD;
	private IncidenteBD incBD;

	public GsonServidor() throws SQLException, IOException {
		this.gson = new Gson();
		this.userBD = new UsuarioBD();
		this.incBD = new IncidenteBD();
	}

	public String cadastro(JsonObject jsonObject, Connection conexao) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException {
		ValidarCadastro vc = new ValidarCadastro();
		JsonObject cadastro = new JsonObject();
		VerificarDados verificar = new VerificarDados();

		if (!vc.validarJsonObject(jsonObject)) {
			cadastro.addProperty("codigo", 500);
			cadastro.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(cadastro);
		}

		if (!vc.validarNome(jsonObject) || !vc.validarEmail(jsonObject) || !vc.validarSenha(jsonObject)) {
			cadastro.addProperty("codigo", 500);
			cadastro.addProperty("mensagem", "Dados nulos ou vazios no cadastro!");
			gson.toJson(cadastro);
			return gson.toJson(cadastro);
		}

		String nome = jsonObject.get("nome").getAsString();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		
		if (!verificar.validarNome(nome) || !verificar.validarEmail(email) || !verificar.validarSenha(senha)) {
			cadastro.addProperty("codigo", 500);
			cadastro.addProperty("mensagem", "Seus dados não estão de acordo com o protocolo (tamanho ou conteúdo estão errados)");
			gson.toJson(cadastro);
			return gson.toJson(cadastro);
		}
		
		Usuario usuario1 = new Usuario(nome, email, senha);
		boolean verificationEmail = userBD.verificarExistenciaEmail(email);

		if (verificationEmail) {
			cadastro.addProperty("codigo", 500);
			cadastro.addProperty("mensagem", "Este email já está cadastrado!");
		} else if (verificar.verificarCadastro(usuario1)) {
			cadastro.addProperty("codigo", 200);
			userBD.cadastrar(usuario1);
		} else {
			cadastro.addProperty("codigo", 500);
			cadastro.addProperty("mensagem", "Os dados não estão no tamanho certo");
		}

		return gson.toJson(cadastro);
	}

	public String login(JsonObject jsonObject, Connection conexao, TelaServer telaServer) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException {
		ValidarLogin vl = new ValidarLogin();
		JsonObject login = new JsonObject();
		VerificarDados verificar = new VerificarDados();

		if (!vl.validarJsonObject(jsonObject)) {
			login.addProperty("codigo", 500);
			login.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(login);
		}

		if (!vl.validarEmail(jsonObject) || !vl.validarSenha(jsonObject)) {
			login.addProperty("codigo", 500);
			login.addProperty("mensagem", "Dados nulos ou vazios no login!");
			return gson.toJson(login);
		}
		

		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		int id_usuario = userBD.pesquisarLogin(email, senha);
		String nome = userBD.pesquisarNomeUsuario(email, senha);
		
		List<Usuario> listaUsers = telaServer.getUsuariosList();
		
		for (Usuario usuario : listaUsers) {
			if (usuario.getId_usuario() == id_usuario) {
				login.addProperty("codigo", 500);
				login.addProperty("mensagem", "Este usuário já está logado!");
				return gson.toJson(login);
			}
		}
		
		
		if (id_usuario != 0 && verificar.verificarLogin(email, senha)) {
			String token = this.gerarToken();
			userBD.alterarToken(token, id_usuario);
			login.addProperty("codigo", 200);
			login.addProperty("token", token);
			login.addProperty("id_usuario", id_usuario);
			telaServer.getUsuariosList().add(new Usuario(id_usuario, nome, email));
			telaServer.atualizarTabelaUsuarios(telaServer.getUsuariosList());
		} else {
			login.addProperty("codigo", 500);
			login.addProperty("mensagem", "Erro: e-mail ou senha inválidos!");
		}

		return gson.toJson(login);
	}

	public String logout(JsonObject jsonObject, Connection conexao, TelaServer telaServer) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException {

		ValidarLogout vl = new ValidarLogout();
		JsonObject logout = new JsonObject();
		VerificarDados verificar = new VerificarDados();

		if (!vl.validarJsonObject(jsonObject)) {
			logout.addProperty("codigo", 500);
			logout.addProperty("mensagem", "Formato do Gson inválido!");
		}

		if (!vl.validarToken(jsonObject) || !vl.validarId(jsonObject)) {
			logout.addProperty("codigo", 500);
			logout.addProperty("mensagem", "Dados nulos ou vazios");
		}

		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");

		if (verificar.verificarLogout(tokenBanco, id_usuario)) {
			if (tokenBanco.equals(token)) {

				userBD.alterarToken("", id_usuario);
				int index = -1;
				for (int i = 0; i < telaServer.getUsuariosList().size(); i++) {
					Usuario usuario = telaServer.getUsuariosList().get(i);
					if (usuario.getId_usuario() == id_usuario) {
						index = i;
						break;
					}
				}
				
				telaServer.getUsuariosList().remove(index);
				telaServer.atualizarTabelaUsuarios(telaServer.getUsuariosList());

				logout.addProperty("codigo", 200);
			} else {
				logout.addProperty("codigo", 500);
				logout.addProperty("Mensagem", "Erro no logout: usuário não está logado.");

			}
		} else {
			logout.addProperty("codigo", 500);
			logout.addProperty("Mensagem", "Erro no logout: usuário inexistente");

		}

		return gson.toJson(logout);

	}

	public String reportarIncidente(JsonObject jsonObject, Connection conexao) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException {

		ValidarReportarIncidente vri = new ValidarReportarIncidente();
		JsonObject reportar = new JsonObject();
		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);
		VerificarDados verificar = new VerificarDados();

		if (!vri.validarJsonObject(jsonObject, "reportar")) {
			mensagemErro.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(mensagemErro);
		}

		if (!vri.validarToken(jsonObject) || !vri.validarIdUsuario(jsonObject) || !vri.validarData(jsonObject)
				|| !vri.validarKm(jsonObject) || !vri.validarRodovia(jsonObject)
				|| !vri.validarTipoIncidente(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Dados nulos ou vazios");
			return gson.toJson(mensagemErro);
		}

		String rodovia = jsonObject.get("rodovia").getAsString();
		int km = jsonObject.get("km").getAsInt();
		int tipo_incidente = jsonObject.get("tipo_incidente").getAsInt();
		String data = jsonObject.get("data").getAsString();
		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();

		if (!verificar.validarRodovia(rodovia)) {
			mensagemErro.addProperty("mensagem", "Rodovia inválida");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarData(data)) {
			mensagemErro.addProperty("mensagem", "Data inválida");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarTipoIncidente(tipo_incidente)) {
			mensagemErro.addProperty("mensagem", "Período inválido!");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarKm(km)) {
			mensagemErro.addProperty("mensagem", "Km inválido!");
			return gson.toJson(mensagemErro);
		}

		String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");

		if (tokenBanco.equals(token)) {
			Incidente e = new Incidente(data, rodovia, km, tipo_incidente, id_usuario);

			if (verificar.validarIncidente(e)) {

				incBD.inserirIncidente(e);
				reportar.addProperty("codigo", 200);
			} else {
				mensagemErro.addProperty("mensagem", "Dados inválidos!");
				return gson.toJson(mensagemErro);
			}
		} else {
			mensagemErro.addProperty("mensagem", "Token inválido!");
			return gson.toJson(mensagemErro);
		}

		return gson.toJson(reportar);

	}

	public String solicitarListaIncidentes(JsonObject jsonObject) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException, IOException, ArrayIndexOutOfBoundsException {

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);
		JsonObject mensagemSucesso = new JsonObject();
		mensagemSucesso.addProperty("codigo", 200);
		ValidarSolicitarLista vsl = new ValidarSolicitarLista();
		VerificarDados verificar = new VerificarDados();

		if (!vsl.validarJsonObject(jsonObject)) {

			mensagemErro.addProperty("mensagem", "Formato Gson inválido!");
			return gson.toJson(mensagemErro);
		}

		if (!vsl.validarData(jsonObject) || !vsl.validarPeriodo(jsonObject) || !vsl.validarRodovia(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Dados nulos ou vazios");
			return gson.toJson(mensagemErro);
		}

		String rodovia = jsonObject.get("rodovia").getAsString();
		JsonElement faixaKmElement = jsonObject.get("faixa_km");
		String faixa_km = (faixaKmElement != null && !faixaKmElement.isJsonNull()) ? faixaKmElement.getAsString() : "";
		int km_inicial = -1;
		int km_final = -1;

		if (!faixa_km.equals("")) {
			String[] partes = faixa_km.split("-");

			km_inicial = Integer.parseInt(partes[0]);
			km_final = Integer.parseInt(partes[1]);

			if (!verificar.validarKm(km_inicial) || !verificar.validarKm(km_final)) {
				mensagemErro.addProperty("mensagem", "Faixa de km inválida!");
				return gson.toJson(mensagemErro);
			}

		}

		String data = jsonObject.get("data").getAsString();
		int periodo = jsonObject.get("periodo").getAsInt();

		if (data.equals("") || data.equals("  -  -    ")) {
			mensagemErro.addProperty("mensagem", "Data vazia!");
			return gson.toJson(mensagemErro);
		}

		if (!verificar.validarRodovia(rodovia)) {
			mensagemErro.addProperty("mensagem", "Rodovia inválida");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarData(data)) {
			mensagemErro.addProperty("mensagem", "Data inválida");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarPeriodo(periodo)) {
			mensagemErro.addProperty("mensagem", "Período inválido!");
			return gson.toJson(mensagemErro);

		} else {
			JsonArray lista = incBD.criarListaIncidentes(rodovia, data, periodo, km_inicial, km_final);
			mensagemSucesso.addProperty("codigo", 200);
			mensagemSucesso.add("lista_incidentes", lista);
			return gson.toJson(mensagemSucesso);
		}

	}

	public String solicitarMeusIncidentes(JsonObject jsonObject) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException, IOException {

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);
		JsonObject mensagemSucesso = new JsonObject();
		mensagemSucesso.addProperty("codigo", 200);
		ValidarLogout vl = new ValidarLogout(); // são os mesmos dados, então só reutilizei o vl

		if (!vl.validarJsonObject(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(mensagemErro);
		}

		if (!vl.validarToken(jsonObject) || !vl.validarId(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Dados nulos ou vazios");
			return gson.toJson(mensagemErro);
		}

		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();

		String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");

		if (tokenBanco.equals(token)) { // se o token estiver correto, pega o id_usuario e faz uma query no banco

			JsonArray listaJson = incBD.criarMinhaLista(id_usuario);
			mensagemSucesso.add("lista_incidentes", listaJson);
			return gson.toJson(mensagemSucesso);

		} else {

			mensagemErro.addProperty("mensagem", "Token inválido!");
			return gson.toJson(mensagemErro);
		}

	}

	public String atualizarCadastro(JsonObject jsonObject, Connection conexao, TelaServer telaServer) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException, IOException {

		ValidarAtualizacaoCadastro vac = new ValidarAtualizacaoCadastro();
		JsonObject atualizar = new JsonObject();
		VerificarDados verificar = new VerificarDados();

		if (!vac.validarJsonObject(jsonObject)) {
			atualizar.addProperty("codigo", 500);
			atualizar.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(atualizar);
		}

		if (!vac.validarNome(jsonObject) || !vac.validarEmail(jsonObject) || !vac.validarSenha(jsonObject)
				|| !vac.validarToken(jsonObject) || !vac.validarId(jsonObject)) {
			atualizar.addProperty("codigo", 500);
			atualizar.addProperty("mensagem", "Dados nulos ou vazios no login!");
			return gson.toJson(atualizar);
		}
		

		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		String nome = jsonObject.get("nome").getAsString();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		String token = jsonObject.get("token").getAsString();
		
		if (!verificar.validarNome(nome) || !verificar.validarEmail(email) || !verificar.validarSenha(senha)) {
			atualizar.addProperty("codigo", 500);
			atualizar.addProperty("mensagem", "Seus dados não estão de acordo com o protocolo (tamanho ou conteúdo estão errados)");
			gson.toJson(atualizar);
			return gson.toJson(atualizar);
		}

		String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");

		if (tokenBanco.equals(token)) {
			Usuario usuario = new Usuario(nome, email, senha);
			Usuario e = new Usuario(id_usuario, nome, email);
			int index = -1;
			if (verificar.verificarCadastro(usuario)) {

				String novoToken = this.gerarToken();
				userBD.alterarCadastro(usuario, novoToken, id_usuario);
				for (int i = 0; i < telaServer.getUsuariosList().size(); i++) {
					Usuario usuario2 = telaServer.getUsuariosList().get(i);
					if (usuario2.getId_usuario() == id_usuario) {
						index = i;
						break;
					}
				}
				
				telaServer.getUsuariosList().remove(index);
				telaServer.getUsuariosList().add(e);
				telaServer.atualizarTabelaUsuarios(telaServer.getUsuariosList());
				atualizar.addProperty("codigo", 200);
				atualizar.addProperty("token", novoToken);
			} else {
				atualizar.addProperty("codigo", 500);
				atualizar.addProperty("mensagem", "Os dados não estão no tamanho certo");
			}
		} else {
			atualizar.addProperty("codigo", 500);
			atualizar.addProperty("mensagem", "Erro no token - diferente do token do banco");
		}

		return gson.toJson(atualizar);

	}

	public String excluirIncidente(JsonObject jsonObject) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException, IOException {

		ValidarExclusaoIncidente vei = new ValidarExclusaoIncidente();

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);

		if (!vei.validarJsonObject(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(mensagemErro);
		}

		if (!vei.validarId(jsonObject) || !vei.validarIdIncidente(jsonObject) || !vei.validarToken(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Dados nulos ou vazios");
			return gson.toJson(mensagemErro);
		}

		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		int id_incidente = jsonObject.get("id_incidente").getAsInt();

		String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");

		if (tokenBanco.equals(token)) {
			incBD.deletarIncidente(id_incidente);
			JsonObject mensagemSucesso = new JsonObject();
			mensagemSucesso.addProperty("codigo", 200);
			return gson.toJson(mensagemSucesso);

		} else {

			mensagemErro.addProperty("mensagem", "Token inválido");
			return gson.toJson(mensagemErro);
		}

	}

	public String editarIncidente(JsonObject jsonObject) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException, IOException {

		ValidarReportarIncidente vri = new ValidarReportarIncidente();
		VerificarDados verificar = new VerificarDados();

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);

		if (!vri.validarJsonObject(jsonObject, "editar")) {
			mensagemErro.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(mensagemErro);
		}

		if (!vri.validarToken(jsonObject) || !vri.validarIdUsuario(jsonObject) || !vri.validarData(jsonObject)
				|| !vri.validarKm(jsonObject) || !vri.validarRodovia(jsonObject)
				|| !vri.validarTipoIncidente(jsonObject) || !vri.validarIdIncidente(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Dados nulos ou vazios");
			return gson.toJson(mensagemErro);
		}

		String rodovia = jsonObject.get("rodovia").getAsString();
		int km = jsonObject.get("km").getAsInt();
		int tipo_incidente = jsonObject.get("tipo_incidente").getAsInt();
		String data = jsonObject.get("data").getAsString();
		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		int id_incidente = jsonObject.get("id_incidente").getAsInt();

		if (!verificar.validarRodovia(rodovia)) {
			mensagemErro.addProperty("mensagem", "Rodovia inválida");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarData(data)) {
			mensagemErro.addProperty("mensagem", "Data inválida");
			return gson.toJson(mensagemErro);
		} else if (!verificar.validarKm(km)) {
			mensagemErro.addProperty("mensagem", "Km inválido!");
			return gson.toJson(mensagemErro);

		} else {
			JsonObject mensagemSucesso = new JsonObject();
			String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");

			if (tokenBanco.equals(token)) {

				incBD.editarIncidente(rodovia, km, tipo_incidente, data, id_usuario, id_incidente);
				mensagemSucesso.addProperty("codigo", 200);
				return gson.toJson(mensagemSucesso);

			} else {
				mensagemErro.addProperty("mensagem", "Token inválido!");
				return gson.toJson(mensagemErro);
			}

		}

	}

	public String removerCadastro(JsonObject jsonObject, TelaServer telaServer) throws SQLException, NullPointerException, JsonParseException,
	NumberFormatException, IllegalStateException, IOException {

		ValidarRemocaoCadastro vrc = new ValidarRemocaoCadastro();

		JsonObject mensagemErro = new JsonObject();
		mensagemErro.addProperty("codigo", 500);

		if (!vrc.validarJsonObject(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Formato do Gson inválido!");
			return gson.toJson(mensagemErro);
		}
		
		if (!vrc.validarEmail(jsonObject) || !vrc.validarIdUsuario(jsonObject) || !vrc.validarSenha(jsonObject) || !vrc.validarToken(jsonObject)) {
			mensagemErro.addProperty("mensagem", "Dados nulos ou vazios");
			return gson.toJson(mensagemErro);
		}

		String token = jsonObject.get("token").getAsString();
		int id_usuario = jsonObject.get("id_usuario").getAsInt();
		String email = jsonObject.get("email").getAsString();
		String senha = jsonObject.get("senha").getAsString();
		String tokenBanco = userBD.pesquisarUsuario(token, id_usuario, "token");
		String emailBanco = userBD.pesquisarUsuario(token, id_usuario, "email");
		String senhaBanco = userBD.pesquisarUsuario(token, id_usuario, "senha");

		if (tokenBanco.equals(token) && emailBanco.equals(email) && senhaBanco.equals(senha)) { // existe token

			userBD.deletarUsuario(id_usuario);
			incBD.removerIncidentesUsuario(id_usuario);

			JsonObject mensagemSucesso = new JsonObject();
			int index = -1;
			
			for (int i = 0; i < telaServer.getUsuariosList().size(); i++) {
				Usuario usuario = telaServer.getUsuariosList().get(i);
				if (usuario.getId_usuario() == id_usuario) {
					index = i;
					break;
				}
			}
			
			telaServer.getUsuariosList().remove(index);
			telaServer.atualizarTabelaUsuarios(telaServer.getUsuariosList());
			mensagemSucesso.addProperty("codigo", 200);
			return gson.toJson(mensagemSucesso);

		} else {

			mensagemErro.addProperty("mensagem", "Token inválido");
			return gson.toJson(mensagemErro);
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

}
