package Interface;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class TelaInicial extends JFrame {
	private JButton loginButton;
	private JButton cadastroButton;

	public TelaInicial() {
		setTitle("Tela Inicial");
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel("Seja bem-vindo!");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		add(titleLabel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;

		loginButton = new JButton("Login");
		cadastroButton = new JButton("Cadastro");

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 30, 0);
		buttonPanel.add(loginButton, gbc);

		gbc.gridy = 1;
		buttonPanel.add(cadastroButton, gbc);

		add(buttonPanel, BorderLayout.CENTER);

	}

	public JButton getLoginButton() {
		return loginButton;
	}

	public JButton getCadastroButton() {
		return cadastroButton;
	}

}
