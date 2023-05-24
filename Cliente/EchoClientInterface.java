package Cliente;

import Interface.*;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class EchoClientInterface {

	private static PrintWriter out;
	private static BufferedReader in;
	static TelaInicial telaInicial = new TelaInicial();
	static LoginWindow loginWindow = new LoginWindow();
	static CadastroWindow cadastroWindow = new CadastroWindow();
	static HomePage homePage = new HomePage();
	static Usuario usuario1 = new Usuario();
	static int id_usuario_conectado = -1;
	static String token_usuario_conectado = null;
	static Socket echoSocket;

	public static void main(String[] args) {

		TelaConexao telaConexao = new TelaConexao();
		telaConexao.setVisible(true);
		telaConexao.getConectarButton().addActionListener(e -> {
			String serverHostname = telaConexao.getIp();
			int numeroPorta = telaConexao.getPorta();
			try {
				echoSocket = connectToServer(serverHostname, numeroPorta);
				out = new PrintWriter(echoSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				telaConexao.setVisible(false);
				abrirTelaInicial();
			} catch (IOException e1) {
				System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
				System.exit(1);
			}

		});

	}

	public static Socket connectToServer(String serverHostname, int numeroPorta) throws IOException {

		System.out.println("Attemping to connect to host " + serverHostname + " on port " + numeroPorta + ". ");

		Socket echoSocket = null;

		echoSocket = new Socket(serverHostname, numeroPorta);
		return echoSocket;
	}

	public static void abrirTelaInicial() {
		telaInicial.setVisible(true);
		telaInicial.getLoginButton().addActionListener(e -> {

			telaInicial.setVisible(false);
			abrirLoginWindow();
		});

		telaInicial.getCadastroButton().addActionListener(e -> {

			telaInicial.setVisible(false);
			abrirCadastroWindow();

		});
	}

	public static void abrirLoginWindow() {

		loginWindow.setVisible(true);
		loginWindow.getConfirmar().addActionListener(e -> {
			String email = loginWindow.getEmail();
			String senha = loginWindow.getSenha();
			String gsonString = usuario1.login(email, senha);
			System.out.println("Enviando para o servidor: " + gsonString);

			out.println(gsonString);

			String servidor;
			try {
				servidor = in.readLine();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigoLogin = jsonObject.get("codigo").getAsInt();

				if (codigoLogin == 200) {
					id_usuario_conectado = jsonObject.get("id_usuario").getAsInt();
					token_usuario_conectado = jsonObject.get("token").getAsString();
					loginWindow.setVisible(false);
					abrirHomePage();

				} else if (codigoLogin == 500) {
					String mensagem = jsonObject.get("mensagem").getAsString();
					JOptionPane.showMessageDialog(loginWindow, mensagem);
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		cadastroWindow.getVoltar().addActionListener(e -> {
			cadastroWindow.setVisible(false);
			abrirTelaInicial();
		});
	}

	public static void abrirCadastroWindow() {

		cadastroWindow.setVisible(true);
		cadastroWindow.getConfirmar().addActionListener(e -> {
			String nome = cadastroWindow.getNome();
			String email = cadastroWindow.getEmail();
			String senha = cadastroWindow.getSenha();

			String gsonString = usuario1.cadastro(nome, email, senha);

			System.out.println("Enviando para o servidor: " + gsonString);

			out.println(gsonString);

			String servidor;
			try {
				servidor = in.readLine();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					JOptionPane.showMessageDialog(cadastroWindow, "Cadastro realizado com sucesso!");
					cadastroWindow.setVisible(false);
					abrirTelaInicial();

				} else if (codigo == 500) {
					String mensagem = jsonObject.get("mensagem").getAsString();
					JOptionPane.showMessageDialog(cadastroWindow, mensagem);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		cadastroWindow.getVoltar().addActionListener(e -> {
			cadastroWindow.setVisible(false);
			abrirTelaInicial();
		});

	}

	public static void abrirHomePage() {

		homePage.setVisible(true);
		homePage.getLogout().addActionListener(e -> {
			String gsonString = usuario1.logout(id_usuario_conectado, token_usuario_conectado);
			System.out.println("Enviando para o servidor: " + gsonString);
			out.println(gsonString);
			
			String servidor;
			try {
				servidor = in.readLine();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					JOptionPane.showMessageDialog(cadastroWindow, "Logout realizado com sucesso");
					id_usuario_conectado = -1;
					token_usuario_conectado = "0";
					homePage.setVisible(false);
					abrirTelaInicial();

				} else if (codigo == 500) {
					String mensagem = jsonObject.get("mensagem").getAsString();
					JOptionPane.showMessageDialog(cadastroWindow, mensagem);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			

		});

	}

}
