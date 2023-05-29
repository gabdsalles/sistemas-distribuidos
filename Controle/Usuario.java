package Controle;

public class Usuario {

	private String nome;
	private String email;
    private String senha;
    private String token;
    private int id_usuario;

    public Usuario(String nome, String email, String senha) {

    	this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

	public Usuario() {}
   

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}
}