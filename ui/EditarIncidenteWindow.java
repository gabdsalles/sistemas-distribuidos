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

import entities.TipoIncidente;

public class EditarIncidenteWindow extends JFrame {

    private JLabel labelRodovia;
    private JLabel labelKm;
    private JLabel labelData;
    private JLabel labelHora;
    private JLabel labelTipo;
    private JTextField rodovia;
    private JTextField km;
    private JFormattedTextField data;
    private JFormattedTextField hora;
    private JComboBox tipoIncidenteBox;
    private JButton confirmar;
    private JButton voltar;

    public EditarIncidenteWindow() {

        setTitle("Editar Incidente");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Editar Incidente");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        JPanel textFieldPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        try {
            MaskFormatter mascaraData = new MaskFormatter("##-##-####");
            MaskFormatter mascaraHora = new MaskFormatter("##:##");
            km = new JTextField();
            data = new JFormattedTextField(mascaraData);
            hora = new JFormattedTextField(mascaraHora);
            MaskFormatter mascaraRodovia = new MaskFormatter("UU-###");
            km = new JTextField();
            rodovia = new JFormattedTextField(mascaraRodovia);
            rodovia.setHorizontalAlignment(JTextField.CENTER);
            km.setHorizontalAlignment(JTextField.CENTER);
            data.setHorizontalAlignment(JTextField.CENTER);
            hora.setHorizontalAlignment(JTextField.CENTER);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        labelRodovia = new JLabel("Rodovia");
        labelKm = new JLabel("KM");
        labelData = new JLabel("Data (DD-MM-YYYY)");
        labelHora = new JLabel("Hora (HH:MM)");
        confirmar = new JButton("Confirmar");
        labelTipo = new JLabel("Tipo Incidente");
        voltar = new JButton("Voltar");
        rodovia.setPreferredSize(new Dimension(100, 30));
        km.setPreferredSize(new Dimension(100, 30));
        data.setPreferredSize(new Dimension(100, 30));
        hora.setPreferredSize(new Dimension(100, 30));
        
        tipoIncidenteBox = new JComboBox<String>();
		
		for (TipoIncidente tipo : TipoIncidente.values()) {
			tipoIncidenteBox.addItem(tipo.getDescricao());
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
        textFieldPanel.add(labelData, gbc);

        gbc.gridy = 4;
        textFieldPanel.add(data, gbc);

        gbc.gridy = 3;
        gbc.gridx = 2;
        textFieldPanel.add(labelHora, gbc);

        gbc.gridy = 4;
        textFieldPanel.add(hora, gbc);
        
        gbc.gridy = 5;
        gbc.gridx = 1;
        
        textFieldPanel.add(labelTipo, gbc);
        
        gbc.gridy = 6;
        textFieldPanel.add(tipoIncidenteBox, gbc);
        
        gbc.gridy = 7;
        gbc.gridx = 1;
        textFieldPanel.add(confirmar, gbc);

        gbc.gridy = 8;
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

    public String getData() {
        return data.getText();
    }

    public String getHora() {
        return hora.getText();
    }
    
    public int getTipoIncidente() {
		String itemSelecionado = (String) tipoIncidenteBox.getSelectedItem();
		for (TipoIncidente tipo : TipoIncidente.values()) {
		    if (tipo.getDescricao().equals(itemSelecionado)) {
		        return tipo.getNumero();
		    }
		}
		return 0;
	}

}
