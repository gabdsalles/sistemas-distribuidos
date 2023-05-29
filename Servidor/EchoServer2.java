package Servidor;

import java.net.*;
import java.io.*;
import com.google.gson.*;
import java.util.ArrayList;

import Controle.*;

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
		GsonControlServer gsonControl = new GsonControlServer();
		int contUsuarios = 1;
		
		String token_conectado = "";
		int id_conectado = -1;
		int codigo = 0;

		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;

			while (true) {
				inputLine = in.readLine();
				System.out.println("Vindo do cliente: " + inputLine);
				int id_usuario = -1;
				String token = "";

				JsonObject jsonObject = JsonParser.parseString(inputLine).getAsJsonObject();
				int id_operacao = jsonObject.get("id_operacao").getAsInt();
				String gsonString = "";

				if (inputLine == "0")
					break;
				switch (id_operacao) {

				case 1: // cadastro

					gsonString = gsonControl.cadastro(jsonObject, contUsuarios);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 2: //atualizar cadastro
					gsonString = gsonControl.atualizarCadastro(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;
				
				case 3: // login
					gsonString = gsonControl.login(jsonObject);
					JsonObject checkLogin = JsonParser.parseString(gsonString).getAsJsonObject();
					codigo = checkLogin.get("codigo").getAsInt();
					
					if (codigo == 200) {
						
						token_conectado = checkLogin.get("token").getAsString();
						id_conectado = checkLogin.get("id_usuario").getAsInt();
					}
					
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 4: //reportar incidente
					gsonString = gsonControl.reportarIncidente(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);

					break;
					
				case 5: //solicitar lista de incidentes
					gsonString = gsonControl.solicitarListaIncidentes(jsonObject);
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
					break;

				case 9: // logout
					gsonString = gsonControl.logout(jsonObject);
					
					JsonObject checkLogout = JsonParser.parseString(gsonString).getAsJsonObject();
					codigo = checkLogout.get("codigo").getAsInt();
					
					if (codigo == 200) {
						
						token_conectado = "";
						id_conectado = -1;
					}
					
					out.println(gsonString);
					System.out.println("Enviando para o cliente: " + gsonString);
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