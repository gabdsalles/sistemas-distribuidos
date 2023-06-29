package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import entities.IncidenteSemId;
import entities.Usuario;

public class TelaServer extends JFrame {

	private JButton conectar;
	private JTextField porta;
	private JTable usuariosTable;
	private List<Usuario> usuariosList = new ArrayList<Usuario>();

	public TelaServer() {
		setTitle("Tela Servidor");
		setSize(600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		// usuariosList.add(new Usuario(1, "Gabriel", "gabdsalles@gmail.com"));

		JLabel titleLabel = new JLabel("Servidor");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		add(titleLabel, BorderLayout.NORTH);

		String[] columnNames = { "ID", "Nome", "Email" };
		Object[][] rowData = new Object[usuariosList.size()][6];

		for (int i = 0; i < usuariosList.size(); i++) {
			Usuario usuario = usuariosList.get(i);
			rowData[i][0] = usuario.getId_usuario();
			rowData[i][1] = usuario.getNome();
			rowData[i][2] = usuario.getEmail();
		}

		DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		usuariosTable = new JTable(tableModel);

		JScrollPane scrollPane = new JScrollPane(usuariosTable);
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();

		conectar = new JButton("Conectar");
		porta = new JTextField("24001");

		buttonPanel.add(conectar);
		buttonPanel.add(porta);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void atualizarTabelaUsuarios(List<Usuario> usuariosList) {
		this.usuariosList = usuariosList;

		String[] columnNames = { "ID", "Nome", "Email" };
		Object[][] rowData = new Object[usuariosList.size()][3];

		for (int i = 0; i < usuariosList.size(); i++) {
			Usuario usuario = usuariosList.get(i);
			rowData[i][0] = usuario.getId_usuario();
			rowData[i][1] = usuario.getNome();
			rowData[i][2] = usuario.getEmail();
		}

		DefaultTableModel tableModel = new DefaultTableModel(rowData, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Atualizar o modelo da tabela
		usuariosTable.setModel(tableModel);
	}

	public JButton getConectar() {
		return conectar;
	}

	public String getPorta() {
		return porta.getText();
	}

	public JTable getUsuariosTable() {
		return usuariosTable;
	}

	public List<Usuario> getUsuariosList() {
		return usuariosList;
	}

}
