package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entities.IncidenteSemId;

public class MeusIncidentesWindow extends JFrame {

    private JButton voltar;
    private JButton excluir;
    private JButton editar;
    private JTable incidentesTable;

    public MeusIncidentesWindow(List<IncidenteSemId> listaIncidentes) {
        setTitle("Home Page");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Meus Incidentes");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Rodovia", "KM", "Tipo", "Data", "Hora"};
        Object[][] rowData = new Object[listaIncidentes.size()][6];

        for (int i = 0; i < listaIncidentes.size(); i++) {
            IncidenteSemId incidente = listaIncidentes.get(i);
            rowData[i][0] = incidente.getId_incidente();
            rowData[i][1] = incidente.getRodovia();
            rowData[i][2] = incidente.getKm();
            rowData[i][3] = incidente.getTipo_incidente();
            
            //System.out.println(incidente.getData());
            
            String[] partes = incidente.getData().split(" ");
            String data = partes[0];
            String hora = partes[1];
            rowData[i][4] = data;
            rowData[i][5] = hora;
        }

        DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incidentesTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(incidentesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        voltar = new JButton("Voltar");
        excluir = new JButton("Excluir");
        editar = new JButton("Editar");

        buttonPanel.add(voltar);
        buttonPanel.add(excluir);
        buttonPanel.add(editar);

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public JButton getVoltar() {
        return voltar;
    }

    public JButton getExcluir() {
        return excluir;
    }

    public JButton getEditar() {
        return editar;
    }

    public JTable getIncidentesTable() {
        return incidentesTable;
    }

    // Exemplo de uso
    public static void main(String[] args) {
        List<IncidenteSemId> listaIncidentes = new ArrayList<>();
        IncidenteSemId incidente1 = new IncidenteSemId("2023-05-26 14:50:31","BR-277", 41, 7, 1);
        IncidenteSemId incidente2 = new IncidenteSemId("2023-05-26 14:50:31","BR-101", 55, 5, 2);
        listaIncidentes.add(incidente1);
        listaIncidentes.add(incidente2);
        SwingUtilities.invokeLater(() -> {
            new MeusIncidentesWindow(listaIncidentes);
        });
    }

	public void setIncidentesTable(JTable incidentesTable) {
		this.incidentesTable = incidentesTable;
	}
}
