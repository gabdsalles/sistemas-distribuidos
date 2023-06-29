package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.Usuario;

public class UsuarioBD {
	
	Connection conexao;
	PreparedStatement st;
	ResultSet rs;
	
	public UsuarioBD() throws SQLException, IOException {
		this.conexao = BancoDeDados.conectar();
		this.st = null;
		this.rs = null;
	}
	
	public boolean verificarExistenciaEmail(String email) throws SQLException {

		st = conexao.prepareStatement("SELECT * FROM usuario WHERE email = ?");
		st.setString(1, email);
		rs = st.executeQuery();

		if (rs.next()) {
			return true; // se já tem um email
		}

		BancoDeDados.finalizarStatement(st);
		BancoDeDados.finalizarResultSet(rs);

		return false; // se não tem
	}
	
	public void cadastrar(Usuario usuario1) throws SQLException {
		
		st = conexao.prepareStatement("insert into usuario (nome, email, senha, token) values (?,?,?,?)");
		st.setString(1, usuario1.getNome());
		st.setString(2, usuario1.getEmail());
		st.setString(3, usuario1.getSenha());
		st.setString(4, "");

		st.executeUpdate();
		BancoDeDados.finalizarStatement(st);
	}
	
	public int pesquisarLogin(String email, String senha) throws SQLException {
		
		st = conexao.prepareStatement("SELECT * FROM usuario WHERE email = ? AND senha = ?");
		st.setString(1, email);
		st.setString(2, senha);
		rs = st.executeQuery();
		
		if (rs.next()) {
			return rs.getInt("id_usuario"); // se já tem um email
		}

		BancoDeDados.finalizarStatement(st);
		BancoDeDados.finalizarResultSet(rs);

		return 0; // se não tem
	}
	
	public String pesquisarNomeUsuario(String email, String senha) throws SQLException {
		
		st = conexao.prepareStatement("SELECT * FROM usuario WHERE email = ? AND senha = ?");
		st.setString(1, email);
		st.setString(2, senha);
		rs = st.executeQuery();
		
		if (rs.next()) {
			return rs.getString("nome"); // se já tem um email
		}

		BancoDeDados.finalizarStatement(st);
		BancoDeDados.finalizarResultSet(rs);

		return ""; // se não tem
	}
	
	public String pesquisarUsuario(String token, int id_usuario, String dado) throws SQLException {
		
		st = conexao.prepareStatement("SELECT * FROM usuario WHERE id_usuario = ? and token = ?");
		st.setInt(1, id_usuario);
		st.setString(2, token);
		rs = st.executeQuery();

		if (rs.next()) return rs.getString(dado);
		else return "";
		
	}
	
	public void alterarToken(String token, int id_usuario) throws SQLException {
		st = conexao.prepareStatement("UPDATE usuario SET token = ? WHERE id_usuario = ?");
		st.setString(1, token);
		st.setInt(2, id_usuario);
		st.executeUpdate();
		
		BancoDeDados.finalizarStatement(st);
	}
	
	public void alterarCadastro(Usuario usuario, String token, int id_usuario) throws SQLException {
		st = conexao.prepareStatement(
				"UPDATE usuario SET nome = ?, email = ?, senha = ?, token = ? WHERE id_usuario = ?");
		st.setString(1, usuario.getNome());
		st.setString(2, usuario.getEmail());
		st.setString(3, usuario.getSenha());
		st.setString(4, token);
		st.setInt(5, id_usuario);
		st.executeUpdate();
	}
	
	public void deletarUsuario(int id_usuario) throws SQLException {
		st = conexao.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?"); // deletar usuário
		st.setInt(1, id_usuario);
		st.executeUpdate();
	}
	
	

}
