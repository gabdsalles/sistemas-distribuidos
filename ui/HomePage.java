package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;


public class HomePage extends JFrame {
	
	private JButton reportarIncidentes;
	private JButton solicitarLista;
	private JButton meusIncidentes;
	private JButton atualizarCadastro;
	private JButton removerCadastro;
	private JButton logout;
	
	
	public HomePage() {
		
		setTitle("Home Page");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HomePage.this.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("HOME");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        
        reportarIncidentes = new JButton("Reportar Incidentes");
        solicitarLista = new JButton("Solicitar Lista de Incidentes");
        meusIncidentes = new JButton("Meus Incidentes");
        atualizarCadastro = new JButton ("Atualizar Cadastro");
        removerCadastro = new JButton ("Remover Cadastro");
        logout = new JButton ("Logout");
        
        gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 10, 0);
		panel.add(reportarIncidentes, gbc);
		
		gbc.gridy = 1;
		panel.add(solicitarLista, gbc);
		
		gbc.gridy = 2;
		panel.add(meusIncidentes, gbc);
		
		gbc.gridy = 3;
		panel.add(atualizarCadastro, gbc);
		
		gbc.gridy = 4;
		panel.add(removerCadastro, gbc);
		
		gbc.gridy = 5;
		panel.add(logout, gbc);
		
		add(panel, BorderLayout.CENTER);
        
        
	}


	public JButton getReportarIncidentes() {
		return reportarIncidentes;
	}


	public JButton getAtualizarCadastro() {
		return atualizarCadastro;
	}


	public JButton getLogout() {
		return logout;
	}
	
	public JButton getSolicitarLista() {
		return solicitarLista;
	}
	
	public JButton getMeusIncidentes() {
		return meusIncidentes;
	}


	public JButton getRemoverCadastro() {
		return removerCadastro;
	}

}
