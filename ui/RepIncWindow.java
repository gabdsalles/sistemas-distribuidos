package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.*;

import Controle.*;

public class RepIncWindow extends JFrame {

	private JFormattedTextField rodovia;
	private JTextField km;
	private JComboBox<String> tipo_incidente;
	private JLabel labelRodovia;
	private JLabel labelKm;
	private JLabel labelTipoIncidente;
	private JButton confirmar;
	private JButton voltar;

	public RepIncWindow() {

		RepIncWindow.this.setLocationRelativeTo(null);

		setTitle("Reportar incidente");
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		RepIncWindow.this.setLocationRelativeTo(null);

		JLabel titleLabel = new JLabel("REPORTAR INCIDENTE");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 30));
		add(titleLabel, BorderLayout.NORTH);

		JPanel textFieldPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		
		try {
            MaskFormatter mascaraRodovia = new MaskFormatter("UU-###");
            km = new JTextField();
            rodovia = new JFormattedTextField(mascaraRodovia);
            rodovia.setHorizontalAlignment(JTextField.CENTER);

        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		tipo_incidente = new JComboBox<String>();
		
		for (TipoIncidente tipo : TipoIncidente.values()) {
            tipo_incidente.addItem(tipo.getDescricao());
        }

		rodovia.setPreferredSize(new Dimension(100, 30));
		km.setPreferredSize(new Dimension(60, 30));
		tipo_incidente.setPreferredSize(new Dimension(150, 30));
		labelRodovia = new JLabel("Rodovia");
		labelKm = new JLabel("KM");
		labelTipoIncidente = new JLabel("Tipo Incidente");
		confirmar = new JButton("Confirmar");
		voltar = new JButton("Voltar");

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 10, 0);
		textFieldPanel.add(labelRodovia, gbc);

		gbc.gridy = 1;
		textFieldPanel.add(rodovia, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		textFieldPanel.add(labelKm, gbc);

		gbc.gridy = 1;
		textFieldPanel.add(km, gbc);

		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		textFieldPanel.add(tipo_incidente, gbc);

		gbc.gridy = 4;
		gbc.gridx = 1;
		textFieldPanel.add(confirmar, gbc);

		gbc.gridy = 5;
		textFieldPanel.add(voltar, gbc);
		
		gbc.gridy = 2;
		textFieldPanel.add(labelTipoIncidente, gbc);
		
		gbc.gridy = 3;
		textFieldPanel.add(tipo_incidente, gbc);

		add(textFieldPanel, BorderLayout.CENTER);
	}
	
	public JButton getConfirmar() {
		return confirmar;
	}
	
	public JButton getVoltar() {
		return voltar;
	}
	
	public String getRodovia() {
		return rodovia.getText();
	}
	
	public int getKm() {
	    String kmText = km.getText();
	    
	    if (kmText.isEmpty()) {
	        return 0; 
	    }
	    
	    try {
	        return Integer.parseInt(kmText);
	    } catch (NumberFormatException e) {
	        return 0;
	    }
	}

	
	public int getTipoIncidente() {
		String itemSelecionado = (String) tipo_incidente.getSelectedItem();
		for (TipoIncidente tipo : TipoIncidente.values()) {
		    if (tipo.getDescricao().equals(itemSelecionado)) {
		        return tipo.getNumero();
		    }
		}
		return 0;
	}

	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				RepIncWindow initialScreen = new RepIncWindow();

				initialScreen.setVisible(true);
			}
		});
	}
}
