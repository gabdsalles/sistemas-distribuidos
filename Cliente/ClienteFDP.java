package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import entities.Usuario;

public class ClienteFDP {
	public static void main(String[] args) throws IOException {

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Insira o IP do servidor que você quer se conectar:");
		String serverHostname = new String(stdIn.readLine());
		System.out.println("Agora, o número da porta: ");
		int numeroPorta = Integer.parseInt(stdIn.readLine());
		
		

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
		Gson gson = new Gson();

		while (control) {

			System.out.println("Escolha sua operação:");
			System.out.println("1-Cadastro");
			System.out.println("2 - Null");
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
				//gsonString = usuario1.cadastro(stdIn);
				JsonObject jsonobject = new JsonObject();
				jsonobject.addProperty("codigo", 200);
				jsonobject.addProperty("id_operacao", 1);
				gsonString = gson.toJson(jsonobject);
				System.out.println("Enviando para o servidor: " + gsonString);

				out.println(gsonString);

				break;

			case 2:
				String teste2 = null;
				out.println(teste2);
				break;
			
			case 3: // login
				//gsonString = usuario1.login(stdIn);
				gsonString = "teste";
				System.out.println("Enviando para o servidor: " + gsonString);
				out.println(gsonString);
				break;

			case 9: // logout
				
				System.out.println("Digite o que vc quiser");
				String teste = stdIn.readLine();
				out.println(teste);
			}

			String servidor = in.readLine();
			System.out.println("Vindo do servidor: " + servidor);


		}

		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
	}
}
