package validacao;

import entities.Incidente;
import entities.Usuario;

public class VerificarDados {
	
	public VerificarDados() {}
	
	public boolean verificarCadastro(Usuario usuario) {

		if (validarNome(usuario.getNome()) && validarEmail(usuario.getEmail()) && validarSenha(usuario.getSenha())) {
			return true;

		} else return false;

	}
	
	public boolean verificarLogin(String email, String senha) {
		if (validarEmail(email) && validarSenha(senha)) {
			return true;
		} else return false;
	}
	
	public boolean verificarLogout(String token, int id_usuario) {
		
		if (validarToken(token) && validarId(id_usuario)) return true;
		else return false;
	}
	
	public boolean validarToken(String token) {
		
		if (token.length() >= 16 && token.length() <= 36) return true;
		else return false;
	}
	
	public boolean validarId(int id_usuario) {
		
		if (id_usuario >= 0) return true;
		else return false;
	}

	public boolean validarNome(String nome) {

		if (nome.length() >= 3 && nome.length() <= 32) {
			// return nome.matches("[a-zA-Z ]+");
			return nome.matches("^[\\P{Digit}\\p{all}]+$");

		} else
			return false;
	}

	public boolean validarEmail(String email) {

		if (email.length() >= 16 && email.length() <= 50 && email.contains("@")) {
			return true;
		} else
			return false;
	}

	public boolean validarSenha(String senha) {
		if (senha.length() >= 8 && senha.length() <= 32) {
			return true;
		} else
			return false;

	}
	
	public boolean validarIncidente(Incidente e) {
		
		if (validarRodovia(e.getRodovia()) && validarKm(e.getKm()) && validarTipoIncidente(e.getTipo_incidente())) {
			return true;
		} else return false;
	}
	
	public boolean validarTipoIncidente(int tipo) {
		
		if (tipo >= 1 && tipo <= 14) return true;
		else return false;
	}
	
	public boolean validarRodovia(String rodovia) {
		return rodovia.matches("^[A-Za-z]{2}-[0-9]{1,3}$");

	}

	public boolean validarKm(int km) {
		if (km >= 0 && km <= 999)
			return true;
		else
			return false;
	}

	public boolean validarData(String data) {

		String[] partes = data.split(" ");

		String dataFormatada = partes[0];

		String[] partesData = dataFormatada.split("-");

		int ano = Integer.parseInt(partesData[0]);
		int mes = Integer.parseInt(partesData[1]);
		int dia = Integer.parseInt(partesData[2]);

		if (ano >= 0 && ano <= 2023 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31) {

			return true;
		} else
			return false;

	}

	public boolean validarPeriodo(int periodo) {

		if (periodo >= 1 && periodo <= 4)
			return true;
		else
			return false;

	}



}
