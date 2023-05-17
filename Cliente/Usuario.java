package Cliente;
import java.io.*;


import com.google.gson.*;
import java.util.Random;

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

	Usuario() {}
    
    public String login(BufferedReader stdIn) throws IOException {
    	
    	Gson gson = new Gson();
    	JsonObject jsonObject = new JsonObject();
    	jsonObject.addProperty("id_operacao", 3);
    	System.out.println("=========Realizar Login=========");
    	System.out.println("Insira seu e-mail:");
    	jsonObject.addProperty("email", stdIn.readLine());
        System.out.println("Insira sua senha:");
        jsonObject.addProperty("senha", hashed(stdIn.readLine()));
        String gsonString = gson.toJson(jsonObject);
        return gsonString;
    	
    }
    
    public String cadastro(BufferedReader stdIn) throws IOException {
    	
    	Gson gson = new Gson();
    	JsonObject jsonObject = new JsonObject();
    	jsonObject.addProperty("id_operacao", 1);
    	System.out.println("=========Realizar Cadastro=========");
    	System.out.println("Insira seu nome:");
    	jsonObject.addProperty("nome", stdIn.readLine());
    	System.out.println("Insira seu e-mail:");
    	jsonObject.addProperty("email", stdIn.readLine());
    	System.out.println("Insira sua senha:");
    	jsonObject.addProperty("senha", hashed(stdIn.readLine()));
    	String gsonString = gson.toJson(jsonObject);
    	return gsonString;
    	
    }
    
    
    public String logout(BufferedReader stdIn, int id_usuario, String token) throws IOException {
    	
    	Gson gson = new Gson();
    	JsonObject jsonObject = new JsonObject();
    	jsonObject.addProperty("id_operacao", 9);
    	jsonObject.addProperty("token", token);
    	jsonObject.addProperty("id_usuario", id_usuario);
        String gsonString = gson.toJson(jsonObject);
        return gsonString;
    }
    
    public String hashed (String pswd) {
    	
    	String hashed = "";
    	
        for (int i = 0; i < pswd.length(); i++) {
            char c = pswd.charAt(i);
            int asciiValue = (int) c; 
            int novoAsciiValue = asciiValue + pswd.length();
            if (novoAsciiValue > 127) {
                novoAsciiValue = novoAsciiValue - 127 + 32;
            }
            char novoCaractere = (char) novoAsciiValue;
            hashed += novoCaractere;
        }
        return hashed;
    }
    
    public String gerarToken() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 36; i++) {
           int indice = random.nextInt(caracteres.length());
           char caractereAleatorio = caracteres.charAt(indice);
           sb.append(caractereAleatorio);
        }
        
        return sb.toString();
    }

    
    public boolean verificarCadastro(Usuario usuario) {
    	
    	if (validarNome(usuario.getNome()) && validarEmail(usuario.getEmail()) && validarSenha(usuario.getSenha())) {
    		return true;
    		
    	} else return false;
    	
    }
    
    public boolean validarNome(String nome) {
    	
    	if (nome.length() >= 3 && nome.length() <= 32) {
    		//return nome.matches("[a-zA-Z ]+");
    		return nome.matches("^[\\P{Digit}\\p{all}]+$");


    	} else return false;
    }
    
    public boolean validarEmail(String email) {
    	
    	if (email.length() >= 16 && email.length() <= 50 && email.contains("@")) {
    		return true;
    	} else return false;
    }
    
    public boolean validarSenha(String senha) {
    	if (senha.length() >= 8 && senha.length() <= 32) {
    		return true;
    	} else return false;
    }

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