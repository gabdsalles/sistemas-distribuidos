package ui;

import java.awt.*;
import javax.swing.*;

public class RemoverCadastroWindow extends JFrame {
    
    private JTextField email;
    private JPasswordField senha;
    private JButton confirmar;
    private JButton voltar;
    private JLabel labelEmail;
    private JLabel labelSenha;
    
    
    public RemoverCadastroWindow() {
        
        setTitle("Remover Cadastro");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        RemoverCadastroWindow.this.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("REMOVER CADASTRO");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 25));
        add(titleLabel, BorderLayout.NORTH);

        JPanel textFieldPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        email = new JTextField();
        senha = new JPasswordField();
        labelEmail = new JLabel("E-mail");
        labelSenha = new JLabel("Senha");
        email.setPreferredSize(new Dimension(200, 30));
        senha.setPreferredSize(new Dimension(200, 30));
        confirmar = new JButton("Confimar");
        voltar = new JButton("Voltar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        textFieldPanel.add(labelEmail, gbc);

        gbc.gridy = 1;
        textFieldPanel.add(email, gbc);
        
        gbc.gridy = 2;
        textFieldPanel.add(labelSenha, gbc);
        
        gbc.gridy = 3;
        textFieldPanel.add(senha, gbc);
        
        gbc.gridy = 4;
        textFieldPanel.add(confirmar, gbc);
        
        gbc.gridy = 5;
        textFieldPanel.add(voltar, gbc);

        add(textFieldPanel, BorderLayout.CENTER);
    }


    public JButton getConfirmar() {
        return confirmar;
    }
    
    public JButton getVoltar() {
        return voltar;
    }
    
    public String getEmail() {
        return email.getText();
    }
    
    public String getSenha() {
        char[] password = senha.getPassword();
        return new String(password);
    }
}
