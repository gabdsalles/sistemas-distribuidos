package Servidor;

import java.net.*;
import java.io.*;
import com.google.gson.*;
import java.util.ArrayList;

import Cliente.Usuario;

public class EchoServer2 extends Thread {

	protected Socket clientSocket;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Insira a porta que você quer se conectar: ");
		int numeroPorta = Integer.parseInt(stdIn.readLine());

		try {
			serverSocket = new ServerSocket(numeroPorta);
			System.out.println("Connection Socket Created");
			try {
				while (true) {
					System.out.println("Waiting for Connection");
					new EchoServer2(serverSocket.accept());
				}
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: 24001.");
			System.exit(1);
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Could not close port: 24001.");
				System.exit(1);
			}
		}
	}

	private EchoServer2(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public void run() {
		System.out.println("New Communication Thread Started");

		Gson gson = new Gson();
		ArrayList<Usuario> listaUsuarios = new ArrayList<Usuario>();
		Usuario user1 = new Usuario("Gabriel", "teste@testando.com", "|nwqj:;<="); // senha1234
		user1.setId_usuario(0);
		listaUsuarios.add(user1);
		int contUsuarios = 1;

		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			while (true) {
				inputLine = in.readLine();
				System.out.println("Vindo do cliente: " + inputLine);
				int codigo = 0;
				int id_usuario = -1;
				String token = "";

				JsonObject jsonObject = JsonParser.parseString(inputLine).getAsJsonObject();
				int id_operacao = jsonObject.get("id_operacao").getAsInt();
				String gsonString = "";

				if (inputLine == "0")
					break;
				switch (id_operacao) {

				case 1: // cadastro
					String nome = jsonObject.get("nome").getAsString();
					String email = jsonObject.get("email").getAsString();
					String senha = jsonObject.get("senha").getAsString();
					Usuario usuario1 = new Usuario(nome, email, senha);
					JsonObject cadastro = new JsonObject();
					boolean verification = false;

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
						out.println(gsonString);
					} else {
						if (usuario1.verificarCadastro(usuario1)) {

							codigo = 200;
							cadastro.addProperty("codigo", codigo);
							gsonString = gson.toJson(cadastro);
							out.println(gsonString);
							usuario1.setId_usuario(contUsuarios);
							listaUsuarios.add(usuario1);
							contUsuarios++;
						} else {
							codigo = 500;
							cadastro.addProperty("codigo", codigo);
							cadastro.addProperty("mensagem", "Cadastro invalido!");
							gsonString = gson.toJson(cadastro);
							out.println(gsonString);
						}
					}

					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 3: // login
					String email2 = jsonObject.get("email").getAsString();
					String senha2 = jsonObject.get("senha").getAsString();
					boolean verificacao = false;

					for (Usuario usuario11 : listaUsuarios) {

						if (usuario11.getEmail().equals(email2) && usuario11.getSenha().equals(senha2)) {
							token = usuario11.gerarToken();
							usuario11.setToken(token);
							id_usuario = usuario11.getId_usuario();
							verificacao = true;
						}

					}

					String gsonString2 = "";
					JsonObject login = new JsonObject();

					if (verificacao) {
						login.addProperty("codigo", 200);
						login.addProperty("token", listaUsuarios.get(id_usuario).getToken());
						login.addProperty("id_usuario", id_usuario);
						gsonString2 = gson.toJson(login);
						out.println(gsonString2);
					} else {

						codigo = 500;
						login.addProperty("codigo", codigo);
						login.addProperty("mensagem", "Erro: e-mail ou senha invalidos!");
						gsonString2 = gson.toJson(login);
						out.println(gsonString2);
					}

					System.out.println("Enviando para o cliente: " + gsonString2);
					break;

				case 9: // logout

					JsonObject logout = new JsonObject();
					try {
						JsonElement tokenElement = jsonObject.get("token");
						token = (tokenElement != null && !tokenElement.isJsonNull()) ? tokenElement.getAsString() : "";
						JsonElement id_usuario_Element = jsonObject.get("id_usuario");
						token = (id_usuario_Element != null && !id_usuario_Element.isJsonNull()) ? id_usuario_Element.getAsString() : "";
						
						if (id_usuario <= listaUsuarios.size() && id_usuario >= 0) { // id usuario valido?

							if (listaUsuarios.get(id_usuario).getToken().equals(token)) { // token valido
								listaUsuarios.get(id_usuario).setToken(null);
								logout.addProperty("codigo", 200);
								String gsonLogout = gson.toJson(logout);
								out.println(gsonLogout);
								System.out.println("Enviando para o cliente: " + gsonLogout);
							} else {
								logout.addProperty("codigo", 500);
								logout.addProperty("Mensagem", "Erro no logout: usuário não está logado.");
								String gsonLogout = gson.toJson(logout);
								out.println(gsonLogout);
								System.out.println("Enviando para o cliente: " + gsonLogout);
							}

						} else {
							logout.addProperty("codigo", 500);
							logout.addProperty("Mensagem", "Erro no logout: usuário não está cadastrado.");
							String gsonLogout = gson.toJson(logout);
							out.println(gsonLogout);
							System.out.println("Enviando para o cliente: " + gsonLogout);
						}
					} catch (NullPointerException e) {
						logout.addProperty("codigo", 500);
						logout.addProperty("Mensagem",
								"Erro no logout: token ou usuário são nulos. Logo, não é possível deslogar o usuário.");
						String gsonLogout = gson.toJson(logout);
						out.println(gsonLogout);
						System.out.println("Enviando para o cliente: " + gsonLogout);
						System.out.println();
						break;
					}

					break;
				}
			}
			out.close();
			in.close();
			clientSocket.close();
		}

		catch (IOException e) {
			System.err.println("Problem with Communication Server");
		}
	}
}