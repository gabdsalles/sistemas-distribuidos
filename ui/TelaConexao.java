package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.*;

public class TelaConexao extends JFrame {
	private JTextField ip;
	private JTextField porta;
	private JButton conectar;
	private JLabel labelIp;
	private JLabel labelPorta;
	
	public TelaConexao() {
		
		setTitle("Conectar ao servidor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        TelaConexao.this.setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("CONEXÃO COM O SERVIDOR");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
        add(titleLabel, BorderLayout.NORTH);

        JPanel textFieldPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        ip = new JTextField("127.0.0.1");
        porta = new JTextField("24001");
        labelIp = new JLabel("IP");
        labelPorta = new JLabel("Porta");
        ip.setPreferredSize(new Dimension(200, 30));
        porta.setPreferredSize(new Dimension(200, 30));
        conectar = new JButton("Conectar");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        textFieldPanel.add(labelIp, gbc);

        gbc.gridy = 1;
        textFieldPanel.add(ip, gbc);
        
        gbc.gridy = 2;
        textFieldPanel.add(labelPorta, gbc);
        
        gbc.gridy = 3;
        textFieldPanel.add(porta, gbc);
        
        gbc.gridy = 4;
        textFieldPanel.add(conectar, gbc);

        add(textFieldPanel, BorderLayout.CENTER);

    }
	
    public String getIp() {
    	return ip.getText();
    }
    
    public int getPorta() {
        try {
            return Integer.parseInt(porta.getText());
        } catch (NumberFormatException e) {
            // Tratamento da exceção, se necessário
            return 0; // Valor padrão em caso de exceção
        }
    }
    
    public JButton getConectarButton() {
    	
    	return conectar;
    }
    
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	TelaConexao initialScreen = new TelaConexao();
            	
                initialScreen.setVisible(true);
            }
        });
    }

}

