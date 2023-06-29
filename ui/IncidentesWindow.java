package ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import entities.IncidenteSemId;

public class IncidentesWindow extends JFrame {
	
	private JButton voltar;

    public IncidentesWindow(List<IncidenteSemId> listaIncidentes) {
        setTitle("Home Page");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Lista de Incidentes");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<IncidenteSemId> listModel = new DefaultListModel<>();

        for (IncidenteSemId incidente : listaIncidentes) {
            listModel.addElement(incidente);
        }

        JList<IncidenteSemId> incidentesList = new JList<>(listModel);

        incidentesList.setCellRenderer(new IncidenteRenderer());

        JScrollPane scrollPane = new JScrollPane(incidentesList);
        add(scrollPane, BorderLayout.CENTER);

        voltar = new JButton("Voltar");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(voltar);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
    
    public class IncidenteRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof IncidenteSemId) {
                IncidenteSemId incidente = (IncidenteSemId) value;
                String texto = "Rodovia: " + incidente.getRodovia() + ", KM: " + incidente.getKm() + ", Tipo: " + incidente.getTipo_incidente() + ", Data: " + incidente.getData();
                return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
    
    public JButton getVoltar() {
    	return voltar;
    }


    public static void main(String[] args) {
    	
    	List<IncidenteSemId> listaIncidentes = new ArrayList<IncidenteSemId>();
    	IncidenteSemId incidente1 = new IncidenteSemId("2023-05-26 14:50:31", "BR-277", 41, 7, 1);
    	listaIncidentes.add(incidente1);
        SwingUtilities.invokeLater(() -> {
            new IncidentesWindow(listaIncidentes);
        });
    }
}