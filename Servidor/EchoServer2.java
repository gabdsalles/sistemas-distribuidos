package Servidor;

import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.*;
import com.google.gson.*;

import Controle.*;
import dao.BancoDeDados;

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
		Connection conexao;
		GsonControlServer gsonControl = new GsonControlServer();
		int contUsuarios = 2;
		int codigo = 0;

		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;
			conexao = BancoDeDados.conectar();

			while (true) {
				inputLine = in.readLine();
				System.out.println("Vindo do cliente: " + inputLine);

				JsonObject jsonObject = JsonParser.parseString(inputLine).getAsJsonObject();
				int id_operacao = jsonObject.get("id_operacao").getAsInt();
				String gsonString = "";

				if (inputLine == "0")
					break;
				switch (id_operacao) {

				case 1: // cadastro

					gsonString = gsonControl.cadastro(jsonObject, contUsuarios, conexao);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 2: //atualizar cadastro
					gsonString = gsonControl.atualizarCadastro(jsonObject,conexao);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
				
				case 3: // login
					gsonString = gsonControl.login(jsonObject, conexao);		
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 4: //reportar incidente
					gsonString = gsonControl.reportarIncidente(jsonObject, conexao);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);

					break;
					
				case 5: //solicitar lista de incidentes
					gsonString = gsonControl.solicitarListaIncidentes(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
					
				case 6: //solicitar lista de incidentes do usuário
					gsonString = gsonControl.solicitarMeusIncidentes(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
					
				case 7: //excluir um incidente
					gsonString = gsonControl.excluirIncidente(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
					
				case 8: //excluir um cadastro
					gsonString = gsonControl.removerCadastro(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 9: // logout
					gsonString = gsonControl.logout(jsonObject, conexao);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
					
				case 10: //editar incidente
					gsonString = gsonControl.editarIncidente(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
					
				default:
					break;
				}
			}

			out.close();
			in.close();
			BancoDeDados.desconectar();
			clientSocket.close();
		}

		catch (IOException e) {
			System.err.println("Problem with Communication Server");
		} catch (SQLException e) {
			System.out.println("Erro no SQL: " + e.getMessage());
		}
	}
}