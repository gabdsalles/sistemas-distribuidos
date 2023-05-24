package Interface;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CadastroWindow extends JFrame {

	private JTextField textFieldNome;
	private JTextField textFieldEmail;
	private JTextField textFieldSenha;
	private JLabel labelNome;
	private JLabel labelEmail;
	private JLabel labelSenha;
	private JButton confirmar;
	private JButton voltar;

	public CadastroWindow() {

		setTitle("Tela de Cadastro");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel("CADASTRO");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(titleLabel, BorderLayout.NORTH);

		JPanel textFieldPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;

		textFieldNome = new JTextField();
		textFieldEmail = new JTextField();
		textFieldSenha = new JTextField();
		textFieldNome.setPreferredSize(new Dimension(200, 30));
		textFieldEmail.setPreferredSize(new Dimension(200, 30));
		textFieldSenha.setPreferredSize(new Dimension(200, 30));
		confirmar = new JButton("Confimar");
		voltar = new JButton("Voltar");
		labelNome = new JLabel("Nome");
		labelEmail = new JLabel("E-mail");
		labelSenha = new JLabel("Senha");

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 10, 0);
		textFieldPanel.add(labelNome, gbc);

		gbc.gridy = 1;
		textFieldPanel.add(textFieldNome, gbc);

		gbc.gridy = 2;
		textFieldPanel.add(labelEmail, gbc);

		gbc.gridy = 3;
		textFieldPanel.add(textFieldEmail, gbc);

		gbc.gridy = 4;
		textFieldPanel.add(labelSenha, gbc);

		gbc.gridy = 5;
		textFieldPanel.add(textFieldSenha, gbc);

		gbc.gridy = 6;
		textFieldPanel.add(confirmar, gbc);

		gbc.gridy = 7;
		textFieldPanel.add(voltar, gbc);

		add(textFieldPanel, BorderLayout.CENTER);
	}

//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				CadastroWindow initialScreen = new CadastroWindow();
//
//				initialScreen.setVisible(true);
//			}
//		});
//	}

	public JButton getConfirmar() {
		return confirmar;
	}

	public String getNome() {
		return textFieldNome.getText();
	}

	public String getEmail() {
		return textFieldEmail.getText();
	}

	public String getSenha() {
		return textFieldSenha.getText();
	}
	
	public JButton getVoltar() {
		return voltar;
	}

}
