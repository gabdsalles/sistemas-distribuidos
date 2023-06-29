package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import entities.Periodo;
import entities.TipoIncidente;

public class FiltrosIncidenteWindow extends JFrame {

	private JLabel labelRodovia;
	private JLabel labelKm;
	private JLabel labelPeriodo;
	private JLabel labelData;
	private JFormattedTextField rodovia;
	private JTextField km;
	private JFormattedTextField data;
	private JComboBox periodoBox;
	private JButton confirmar;
	private JButton voltar;
	
	public FiltrosIncidenteWindow() {

		setTitle("Selecionar incidente");
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FiltrosIncidenteWindow.this.setLocationRelativeTo(null);

		JLabel titleLabel = new JLabel("FILTROS");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 40));
		add(titleLabel, BorderLayout.NORTH);
		
		JPanel textFieldPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		
		try {
            MaskFormatter mascaraRodovia = new MaskFormatter("UU-###");
            MaskFormatter mascaraData = new MaskFormatter("##-##-####");
            km = new JTextField();
            rodovia = new JFormattedTextField(mascaraRodovia);
            data = new JFormattedTextField(mascaraData);
            rodovia.setHorizontalAlignment(JTextField.CENTER);
            data.setHorizontalAlignment(JTextField.CENTER);

        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		periodoBox = new JComboBox<String>();
		labelRodovia = new JLabel("Rodovia");
		labelKm = new JLabel("Faixa KM");
		labelData = new JLabel("Data (DD-MM-YYYY)");
		labelPeriodo = new JLabel("Período");
		confirmar = new JButton("Confirmar");
		voltar = new JButton("Voltar");
		rodovia.setPreferredSize(new Dimension(100, 30));
		km.setPreferredSize(new Dimension(60, 30));
		data.setPreferredSize(new Dimension(150, 30));
		
		for (Periodo periodo : Periodo.values()) {
			periodoBox.addItem(periodo.getDescricao());
		}
		
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
		gbc.gridx = 0;
		textFieldPanel.add(labelPeriodo, gbc);

		gbc.gridy = 4;
		textFieldPanel.add(periodoBox, gbc);
		
		gbc.gridy = 3;
		gbc.gridx = 2;
		textFieldPanel.add(labelData, gbc);
		
		gbc.gridy = 4;
		textFieldPanel.add(data, gbc);

		gbc.gridy = 5;
		gbc.gridx = 1;
		textFieldPanel.add(confirmar, gbc);

		gbc.gridy = 6;
		textFieldPanel.add(voltar, gbc);

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
	
	public String getData() {
		return data.getText();
	}
	
	public String getKm() {
	    return km.getText();
	}

	
	public int getPeriodo() {
		String itemSelecionado = (String) periodoBox.getSelectedItem();
		for (Periodo p : Periodo.values()) {
		    if (p.getDescricao().equals(itemSelecionado)) {
		        return p.getNumPeriodo();
		    }
		}
		return 0;
	}

	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FiltrosIncidenteWindow initialScreen = new FiltrosIncidenteWindow();

				initialScreen.setVisible(true);
			}
		});
	}
}