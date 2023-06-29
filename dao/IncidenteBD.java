package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import entities.Incidente;
import entities.IncidenteSemId;
import entities.Periodo;

public class IncidenteBD {

	Connection conexao;
	PreparedStatement st;
	ResultSet rs;

	public IncidenteBD() throws SQLException, IOException {
		this.conexao = BancoDeDados.conectar();
		this.st = null;
		this.rs = null;
	}

	public void inserirIncidente(Incidente e) throws SQLException {
		st = conexao.prepareStatement(
				"insert into incidente (data, rodovia, km, tipo_incidente, id_usuario) values (?,?,?,?,?)");
		st.setString(1, e.getData());
		st.setString(2, e.getRodovia());
		st.setInt(3, e.getKm());
		st.setInt(4, e.getTipo_incidente());
		st.setInt(5, e.getId_usuario());

		st.executeUpdate();
	}

	public JsonArray criarMinhaLista(int id_usuario) throws SQLException {
		st = conexao.prepareStatement("SELECT * FROM incidente WHERE id_usuario = ?");
		st.setInt(1, id_usuario);
		rs = st.executeQuery();
		Gson gson = new Gson();
		JsonArray listaJson = new JsonArray();

		while (rs.next()) { // montar lista de incidentes

			String dataHoraBanco = rs.getTimestamp("data").toString();
			dataHoraBanco = dataHoraBanco.substring(0, dataHoraBanco.length() - 2);

			IncidenteSemId novoIncidente = new IncidenteSemId(dataHoraBanco, rs.getString("rodovia"), rs.getInt("km"),
					rs.getInt("tipo_incidente"), rs.getInt("id_incidente"));
			String incidenteJson = gson.toJson(novoIncidente);
			listaJson.add(gson.fromJson(incidenteJson, JsonObject.class));
		}

		return listaJson;
	}

	public JsonArray criarListaIncidentes(String rodovia, String data, int periodo, int km_inicial, int km_final)
			throws SQLException, IOException {

		JsonArray listaJson = new JsonArray();

		Periodo[] periodos = Periodo.values();

		Periodo periodoSelecionado = null;
		for (Periodo p : periodos) {
			if (p.getNumPeriodo() == periodo) {
				periodoSelecionado = p;
				break;
			}
		}

		LocalTime inicioPeriodo = periodoSelecionado.getHoraInicio();
		LocalTime finalPeriodo = periodoSelecionado.getHoraFim();

		String[] partes = data.split(" ");
		String dataFormatada = partes[0];

		PreparedStatement stmt = null;
		Connection conexao = BancoDeDados.conectar();
		ResultSet rs = null;

		if (km_inicial != -1 && km_final != -1) { // com faixa km
			stmt = conexao.prepareStatement("SELECT * FROM incidente WHERE rodovia = ? AND km BETWEEN ? AND ?");
			stmt.setString(1, rodovia);
			stmt.setInt(2, km_inicial);
			stmt.setInt(3, km_final);

		} else { // sem faixa km
			stmt = conexao.prepareStatement("SELECT * FROM incidente WHERE rodovia = ?");
			stmt.setString(1, rodovia);
		}

		rs = stmt.executeQuery();
		Gson gson = new Gson();

		while (rs.next()) {

			String dataHoraBanco = rs.getTimestamp("data").toString();
			dataHoraBanco = dataHoraBanco.substring(0, dataHoraBanco.length() - 2);

			String partesBanco[] = dataHoraBanco.split(" ");

			String dataBanco = partesBanco[0];
			String horaBanco = partesBanco[1];

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			LocalTime timeIncidente = LocalTime.parse(horaBanco, formatter);

			if (dataBanco.equals(dataFormatada) && timeIncidente.isAfter(inicioPeriodo)
					&& timeIncidente.isBefore(finalPeriodo)) {
				IncidenteSemId novoIncidente = new IncidenteSemId(dataHoraBanco, rs.getString("rodovia"),
						rs.getInt("km"), rs.getInt("tipo_incidente"), rs.getInt("id_incidente"));
				String incidenteJson = gson.toJson(novoIncidente);
				listaJson.add(gson.fromJson(incidenteJson, JsonObject.class));
			}

		}

		return listaJson;
	}

	public void deletarIncidente(int id_incidente) throws SQLException {
		st = conexao.prepareStatement("DELETE FROM incidente WHERE id_incidente = ?");
		st.setInt(1, id_incidente);
		st.executeUpdate();
	}

	public void editarIncidente(String rodovia, int km, int tipo_incidente, String data, int id_usuario,
			int id_incidente) throws SQLException {
		st = conexao.prepareStatement(
				"UPDATE incidente SET rodovia = ?, km = ?, tipo_incidente = ?, data = ? WHERE id_usuario = ? AND id_incidente = ?");
		st.setString(1, rodovia);
		st.setInt(2, km);
		st.setInt(3, tipo_incidente);
		st.setString(4, data);
		st.setInt(5, id_usuario);
		st.setInt(6, id_incidente);
		st.executeUpdate();
	}

	public void removerIncidentesUsuario(int id_usuario) throws SQLException {
		// tornar o id_usuario dos incidentes desse usuário nulo
		st = conexao.prepareStatement("UPDATE incidente SET id_usuario = NULL WHERE id_usuario = ?");
		st.setInt(1, id_usuario);
		st.executeUpdate();
	}

}
