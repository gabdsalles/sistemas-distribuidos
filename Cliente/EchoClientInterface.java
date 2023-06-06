package Cliente;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import Controle.GsonControlClient;
import Controle.IncidenteSemId;
import Controle.Usuario;
import ui.*;

public class EchoClientInterface {

	// 10.40.11.114 - ip edu
	// 10.20.8.81 - ip lukas

	private static PrintWriter out;
	private static BufferedReader in;
	static TelaInicial telaInicial = new TelaInicial();
	static LoginWindow loginWindow = new LoginWindow();
	private static GsonControlClient gsonControlClient = new GsonControlClient();
	static CadastroWindow cadastroWindow = new CadastroWindow();
	static HomePage homePage = new HomePage();
	static RepIncWindow incidentePage = new RepIncWindow();
	static AtualizarCadastroWindow atualizarPage = new AtualizarCadastroWindow();
	static FiltrosIncidenteWindow filtrosPage = new FiltrosIncidenteWindow();
	static EditarIncidenteWindow editarPage = new EditarIncidenteWindow();
	static RemoverCadastroWindow removerPage = new RemoverCadastroWindow();
	static Usuario usuario1 = new Usuario();
	static int id_usuario_conectado = -1;
	static String token_usuario_conectado = null;
	static Socket echoSocket;

	public static void main(String[] args) {

		TelaConexao telaConexao = new TelaConexao();
		telaConexao.setVisible(true);
		ActionListener conectarButtonActionListener = e -> {
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

		};

		telaConexao.getConectarButton().addActionListener(conectarButtonActionListener);

	}

	public static Socket connectToServer(String serverHostname, int numeroPorta) throws IOException {

		System.out.println("Attemping to connect to host " + serverHostname + " on port " + numeroPorta + ". ");

		Socket echoSocket = null;

		echoSocket = new Socket(serverHostname, numeroPorta);
		return echoSocket;
	}

	public static void abrirTelaInicial() {
		telaInicial.setVisible(true);

		ActionListener loginButtonAL = e -> {
			telaInicial.setVisible(false);
			abrirLoginWindow();
		};

		ActionListener cadastroButtonAL = e -> {
			telaInicial.setVisible(false);
			abrirCadastroWindow();
		};

		telaInicial.getLoginButton().removeActionListener(loginButtonAL);
		telaInicial.getCadastroButton().removeActionListener(cadastroButtonAL);

		telaInicial.getLoginButton().addActionListener(loginButtonAL);
		telaInicial.getCadastroButton().addActionListener(cadastroButtonAL);
	}

	public static void abrirLoginWindow() {

		loginWindow.setVisible(true);

		ActionListener confirmarAL = e -> {
			String email = loginWindow.getEmail();
			String senha = loginWindow.getSenha();
			String gsonString = gsonControlClient.login(email, senha);
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
					// JOptionPane.showMessageDialog(loginWindow, mensagem);
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		};

		ActionListener voltarButtonAL = e -> {
			loginWindow.setVisible(false);
			abrirTelaInicial();
		};
		
		loginWindow.getConfirmar().removeActionListener(confirmarAL);
		loginWindow.getVoltar().removeActionListener(voltarButtonAL);

		loginWindow.getConfirmar().addActionListener(confirmarAL);
		loginWindow.getVoltar().addActionListener(voltarButtonAL);
	}

	public static void abrirCadastroWindow() {

		cadastroWindow.setVisible(true);
		
		ActionListener confirmarAL = e -> {
			String nome = cadastroWindow.getNome();
			String email = cadastroWindow.getEmail();
			String senha = cadastroWindow.getSenha();

			String gsonString = gsonControlClient.cadastro(nome, email, senha);

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
					cadastroWindow.setVisible(false);
					abrirTelaInicial();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		};
		
		ActionListener voltarAL = e -> {
			cadastroWindow.setVisible(false);
			abrirTelaInicial();
		};
		
		cadastroWindow.getConfirmar().removeActionListener(confirmarAL);
		cadastroWindow.getVoltar().removeActionListener(voltarAL);
		
		cadastroWindow.getConfirmar().addActionListener(confirmarAL);
		cadastroWindow.getVoltar().addActionListener(voltarAL);

	}

	public static void abrirHomePage() {

		homePage.setVisible(true);
		
		ActionListener reportarIncidentesAL = e -> {
			homePage.setVisible(false);
			abrirReportarIncidentes();
		};
		
		ActionListener solicitarListaAL = e -> {
			homePage.setVisible(false);
			abrirListaIncidentes();
		};
		
		ActionListener meusIncidentesAL = e -> {
			homePage.setVisible(false);
			abrirMeusIncidentes();
		};
		
		ActionListener atualizarCadastroAL = e -> {
			homePage.setVisible(false);
			abrirAtualizarCadastro();
		};
		
		ActionListener removerCadastroAL = e -> {
			
			homePage.setVisible(false);
			abrirRemoverCadastro();
		};
		
		ActionListener logoutAL = e -> {
			String gsonString = gsonControlClient.logout(id_usuario_conectado, token_usuario_conectado);
			System.out.println("Enviando para o servidor: " + gsonString);
			out.println(gsonString);

			String servidor;
			try {
				servidor = in.readLine();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					// JOptionPane.showMessageDialog(cadastroWindow, "Logout realizado com
					// sucesso");
					id_usuario_conectado = -1;
					token_usuario_conectado = "0";
					homePage.setVisible(false);
					abrirTelaInicial();

				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		};
		
		homePage.getReportarIncidentes().removeActionListener(reportarIncidentesAL);
		homePage.getSolicitarLista().removeActionListener(solicitarListaAL);
		homePage.getMeusIncidentes().removeActionListener(meusIncidentesAL);
		homePage.getAtualizarCadastro().removeActionListener(atualizarCadastroAL);
		homePage.getRemoverCadastro().removeActionListener(removerCadastroAL);
		homePage.getLogout().removeActionListener(logoutAL);

		homePage.getReportarIncidentes().addActionListener(reportarIncidentesAL);
		homePage.getSolicitarLista().addActionListener(solicitarListaAL);
		homePage.getMeusIncidentes().addActionListener(meusIncidentesAL);
		homePage.getAtualizarCadastro().addActionListener(atualizarCadastroAL);
		homePage.getRemoverCadastro().addActionListener(removerCadastroAL);
		homePage.getLogout().addActionListener(logoutAL);

	}
	
	private static void abrirMeusIncidentes() {
		String gsonString = gsonControlClient.solicitarMeusIncidentes(token_usuario_conectado, id_usuario_conectado);
		System.out.println("Enviando para o servidor: " + gsonString);
		out.println(gsonString);
		
		String servidor;
		try {
			servidor = in.readLine();
			Gson gson = new Gson();
			System.out.println("Vindo do servidor: " + servidor);
			JsonObject jsonObject = gson.fromJson(servidor, JsonObject.class);
			int codigo = jsonObject.get("codigo").getAsInt();

			if (codigo == 200) {
				JsonArray listaIncidentesJson = jsonObject.getAsJsonArray("lista_incidentes");

				Type incidenteListType = new TypeToken<List<IncidenteSemId>>() {}.getType();
				List<IncidenteSemId> listaIncidentes = gson.fromJson(listaIncidentesJson, incidenteListType);
				MeusIncidentesWindow meusWindow = new MeusIncidentesWindow(listaIncidentes);
				abrirMeusIncidentesWindow(meusWindow);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	private static void abrirReportarIncidentes() {

		incidentePage.setVisible(true);
		
		ActionListener confirmarAL = e -> {
			String rodovia = incidentePage.getRodovia();
			int km = incidentePage.getKm();
			int tipo_incidente = incidentePage.getTipoIncidente();

			String gsonString = gsonControlClient.reportarIncidente(rodovia, km, tipo_incidente,
					token_usuario_conectado, id_usuario_conectado);
			System.out.println("Enviando para o servidor: " + gsonString);
			out.println(gsonString);

			String servidor;

			try {
				servidor = in.readLine();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					incidentePage.setVisible(false);
					abrirHomePage();

				} else if (codigo == 500) {
					String mensagem = jsonObject.get("mensagem").getAsString();
					JOptionPane.showMessageDialog(incidentePage, mensagem);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		};
		
		ActionListener voltarAL = e -> {
			incidentePage.setVisible(false);
			abrirHomePage();
		};

		incidentePage.getConfirmar().removeActionListener(confirmarAL);
		incidentePage.getVoltar().removeActionListener(voltarAL);
		
		incidentePage.getConfirmar().addActionListener(confirmarAL);
		incidentePage.getVoltar().addActionListener(voltarAL);
	}

	private static void abrirAtualizarCadastro() {

		atualizarPage.setVisible(true);
		
		ActionListener confirmarAL = e -> {
			String nome = atualizarPage.getNome();
			String email = atualizarPage.getEmail();
			String senha = atualizarPage.getSenha();

			String gsonString = gsonControlClient.atualizarCadastro(nome, email, senha, token_usuario_conectado,
					id_usuario_conectado);

			System.out.println("Enviando para o servidor: " + gsonString);

			out.println(gsonString);

			String servidor;
			try {
				servidor = in.readLine();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = JsonParser.parseString(servidor).getAsJsonObject();
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					// JOptionPane.showMessageDialog(cadastroWindow, "Cadastro realizado com
					// sucesso!");
					atualizarPage.setVisible(false);
					abrirTelaInicial();

				} else if (codigo == 500) {
					String mensagem = jsonObject.get("mensagem").getAsString();
					// JOptionPane.showMessageDialog(cadastroWindow, mensagem);
					atualizarPage.setVisible(false);
					abrirTelaInicial();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		};
		
		ActionListener voltarAL = e -> {
			atualizarPage.setVisible(false);
			abrirHomePage();
		};

		atualizarPage.getConfirmar().removeActionListener(confirmarAL);
		atualizarPage.getVoltar().removeActionListener(voltarAL);
		
		atualizarPage.getConfirmar().addActionListener(confirmarAL);
		atualizarPage.getVoltar().addActionListener(voltarAL);
	}

	private static void abrirListaIncidentes() {

		filtrosPage.setVisible(true);
		
		ActionListener confirmarAL = e -> {
			String rodovia = filtrosPage.getRodovia();
			String faixa_km = filtrosPage.getKm();
			String data = filtrosPage.getData();
			int periodo = filtrosPage.getPeriodo();

			String gsonString = gsonControlClient.solicitarListaIncidentes(rodovia, faixa_km, data, periodo);
			System.out.println("Enviando para o servidor: " + gsonString);
			out.println(gsonString);

			String servidor;
			try {
				servidor = in.readLine();
				Gson gson = new Gson();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = gson.fromJson(servidor, JsonObject.class);
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					JsonArray listaIncidentesJson = jsonObject.getAsJsonArray("lista_incidentes");

					Type incidenteListType = new TypeToken<List<IncidenteSemId>>() {}.getType();
					List<IncidenteSemId> listaIncidentes = gson.fromJson(listaIncidentesJson, incidenteListType);

					IncidentesWindow incidenteWindow = new IncidentesWindow(listaIncidentes);
					filtrosPage.setVisible(false);
					abrirIncidentesWindow(incidenteWindow);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		};
		
		ActionListener voltarAL = e -> {
			filtrosPage.setVisible(false);
			abrirHomePage();
		};
		
		filtrosPage.getConfirmar().removeActionListener(confirmarAL);
		filtrosPage.getVoltar().removeActionListener(voltarAL);

		filtrosPage.getConfirmar().addActionListener(confirmarAL);
		filtrosPage.getVoltar().addActionListener(voltarAL);
	}
	
	private static void abrirIncidentesWindow(IncidentesWindow incidenteWindow) {

		ActionListener voltarAL = e -> {
			incidenteWindow.setVisible(false);
			abrirHomePage();
		};
		
		incidenteWindow.getVoltar().removeActionListener(voltarAL);
		
		incidenteWindow.getVoltar().addActionListener(voltarAL);
	}

	private static void abrirMeusIncidentesWindow(MeusIncidentesWindow meusWindow) {

	    ActionListener voltarAL = e -> {
	        meusWindow.setVisible(false);
	        abrirHomePage();
	    };
	    
	    ActionListener excluirAL = e -> {
			int resposta = JOptionPane.showOptionDialog(null, "Deseja excluir este incidente?", "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			int selectedRow = meusWindow.getIncidentesTable().getSelectedRow();
			if (selectedRow != -1) {
				int id_incidente = (int) meusWindow.getIncidentesTable().getValueAt(selectedRow, 0);
				if (resposta == JOptionPane.YES_OPTION) {
	                //System.out.println("Excluindo o item...");
					String gsonString = gsonControlClient.excluirIncidente(token_usuario_conectado, id_incidente, id_usuario_conectado);
					out.println(gsonString);
					System.out.println("Enviando para o servidor: " + gsonString);
					String servidor = "";
						try {
							servidor = in.readLine();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						Gson gson = new Gson();
						System.out.println("Vindo do servidor: " + servidor);
						JsonObject jsonObject = gson.fromJson(servidor, JsonObject.class);
						int codigo = jsonObject.get("codigo").getAsInt();

						if (codigo == 200) {
							meusWindow.setVisible(false);
							abrirHomePage();
					}
	            }
			    
			}
		};
		
		ActionListener editarAL = e -> {
			
			meusWindow.setVisible(false);
			int selectedRow = meusWindow.getIncidentesTable().getSelectedRow();
			int id_incidente = (int) meusWindow.getIncidentesTable().getValueAt(selectedRow, 0);
			
			
			abrirEditarIncidente(id_incidente);
		};
	    

	    meusWindow.getVoltar().removeActionListener(voltarAL);
	    meusWindow.getExcluir().removeActionListener(excluirAL);
	    meusWindow.getEditar().removeActionListener(editarAL);

	    meusWindow.getVoltar().addActionListener(voltarAL);
	    meusWindow.getExcluir().addActionListener(excluirAL);
	    meusWindow.getEditar().addActionListener(editarAL);
	    

	}
	
	private static void abrirEditarIncidente(int id_incidente) {
		
		editarPage.setVisible(true);
		
		ActionListener voltarAL = e -> {
	        editarPage.setVisible(false);
	        abrirHomePage();
	    };
	    
	    ActionListener confirmarAL = e -> {
	    	String rodovia = editarPage.getRodovia();
			int km = editarPage.getKm();
			String data = editarPage.getData();
			String hora = editarPage.getHora();
			int tipo_incidente = editarPage.getTipoIncidente();

			String gsonString = gsonControlClient.editarIncidente(token_usuario_conectado, rodovia, data, hora, km, id_incidente, id_usuario_conectado, tipo_incidente);
			System.out.println("Enviando para o servidor: " + gsonString);
			out.println(gsonString);

			String servidor;
			try {
				servidor = in.readLine();
				Gson gson = new Gson();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = gson.fromJson(servidor, JsonObject.class);
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					
					editarPage.setVisible(false);
					abrirHomePage();
					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	    };
	    
	    editarPage.getVoltar().removeActionListener(voltarAL);
	    editarPage.getConfirmar().removeActionListener(confirmarAL);

	    editarPage.getVoltar().addActionListener(voltarAL);
	    editarPage.getConfirmar().addActionListener(confirmarAL);
		
	}
	
	private static void abrirRemoverCadastro() {
		
		removerPage.setVisible(true);
		
		ActionListener voltarAL = e -> {
			removerPage.setVisible(false);
			abrirHomePage();
		};
		
		ActionListener confirmarAL = e -> {
			
			int resposta = JOptionPane.showOptionDialog(null, "Deseja mesmo excluir este usuário?", "Confirmação", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			if (resposta == JOptionPane.YES_OPTION) {
				String email = removerPage.getEmail();
				String senha = removerPage.getSenha();
				String gsonString = gsonControlClient.removerCadastro(email, senha, token_usuario_conectado, id_usuario_conectado);
				System.out.println("Enviando para o servidor: " + gsonString);

				out.println(gsonString);
				
				String servidor = "";
				try {
					servidor = in.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Gson gson = new Gson();
				System.out.println("Vindo do servidor: " + servidor);
				JsonObject jsonObject = gson.fromJson(servidor, JsonObject.class);
				int codigo = jsonObject.get("codigo").getAsInt();

				if (codigo == 200) {
					removerPage.setVisible(false);
					abrirTelaInicial();
			}
				
				
			}
			
		};
		
		removerPage.getConfirmar().removeActionListener(confirmarAL);
		removerPage.getVoltar().removeActionListener(voltarAL);

		removerPage.getConfirmar().addActionListener(confirmarAL);
		removerPage.getVoltar().addActionListener(voltarAL);
	}


}
