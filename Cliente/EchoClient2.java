package Cliente;

import java.io.*;
import java.net.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EchoClient2 {
	public static void main(String[] args) throws IOException {

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Insira o IP do servidor que você quer se conectar:");
		String serverHostname = new String(stdIn.readLine());
		System.out.println("Agora, o número da porta: ");
		int numeroPorta = Integer.parseInt(stdIn.readLine());
		
		Usuario usuario1 = new Usuario();

		if (args.length > 0)
			serverHostname = args[0];
		System.out.println("Attemping to connect to host " + serverHostname + " on port 24001.");

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			echoSocket = new Socket(serverHostname, numeroPorta);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + serverHostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
			System.exit(1);
		}

		boolean control = true;
		int id_usuario_conectado = -1;
		String token_usuario_conectado = null;

		while (control) {

			System.out.println("Escolha sua operação:");
			System.out.println("1-Cadastro");
			System.out.println("3-Login");
			System.out.println("9-Logout");
			System.out.println("0-Sair");

			int op = Integer.parseInt(stdIn.readLine());
			String gsonString;

			switch (op) {

			case 0:
				System.out.println("Matando os programas");
				control = false;
				out.println(0);
				break;

			case 1: // cadastro
				gsonString = usuario1.cadastro(stdIn);
				System.out.println("Enviando para o servidor: " + gsonString);

				out.println(gsonString);

				break;

			case 3: // login
				gsonString = usuario1.login(stdIn);
				System.out.println("Enviando para o servidor: " + gsonString);
				out.println(gsonString);
				break;

			case 9: // logout
				gsonString = usuario1.logout(stdIn, id_usuario_conectado, token_usuario_conectado);
				System.out.println("Enviando para o servidor: " + gsonString);
				out.println(gsonString);
				break;
			}

			String servidor = in.readLine();
			System.out.println("Vindo do servidor: " + servidor);

			if (op == 3) {
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigoLogin = jsonObject.get("codigo").getAsInt();
				if (codigoLogin == 200) {
					id_usuario_conectado = jsonObject.get("id_usuario").getAsInt();
					token_usuario_conectado = jsonObject.get("token").getAsString();
				}
			}

			if (op == 9) {

				JsonObject codigo = JsonParser.parseString(servidor).getAsJsonObject();
				int codigoLogout = codigo.get("codigo").getAsInt();
				if (codigoLogout == 200) {
					id_usuario_conectado = -1;
					token_usuario_conectado = "0";
				}
			}

		}

		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
	}
}
