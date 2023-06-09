package Interface;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;


public class HomePage extends JFrame {
	
	private JButton reportarIncidentes;
	private JButton atualizarCadastro;
	private JButton logout;
	
	
	public HomePage() {
		
		setTitle("Home Page");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("HOME");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        add(titleLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        
        reportarIncidentes = new JButton("Reportar Incidentes");
        atualizarCadastro = new JButton ("Atualizar Cadastro");
        logout = new JButton ("Logout");
        
        gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 10, 0);
		panel.add(reportarIncidentes, gbc);
		
		gbc.gridy = 1;
		panel.add(atualizarCadastro, gbc);
		
		gbc.gridy = 2;
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

}
