
package Modelo;

/**
 *
 * @author Carlos Daniel
 */
public class Usuario {
    
    
    private int idUsuario;
    
    
    private String nome;
    
    
    private String email;
    
    
    private String senha;
    
    
    private String habilidades;
    
    
    private String interesses;
    
    
    private String dificuldades;
    

    public int getidUsuario() {
        
        return idUsuario;
        
    }
    
    

    public void setidUsuario(int idUsuario) {
        
        this.idUsuario = idUsuario;
        
    }
    
    

    public String getNome() {
        
        return nome;
        
    }
    
    

    public void setNome(String nome) {
        
        this.nome = nome;
        
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
    
    

    public String getHabilidades() {
        
        return habilidades;
        
    }
    
    

    public void setHabilidades(String habilidades) {
        
        this.habilidades = habilidades;
        
    }
    
    

    public String getInteresses() {
        
        return interesses;
        
    }
    
    

    public void setInteresses(String interesses) {
        
        this.interesses = interesses;
        
    }
    
    

    public String getDificuldades() {
        
        return dificuldades;
        
    }

    
    
    public void setDificuldades(String dificuldades) {
        
        this.dificuldades = dificuldades;
        
    }



    public Usuario(){


        
    }

    
    
    public Usuario(int idUsuario, String nome, String email, String senha, String habilidades, String interesses, String dificuldades) {
        
        
        this.idUsuario = idUsuario;
        
        
        this.nome = nome;
        
        
        this.email = email;
        
        
        this.senha = senha;
        
        
        this.habilidades = habilidades;
        
        
        this.interesses = interesses;
        
        
        this.dificuldades = dificuldades;
        
        
    }
    
    
    
    public void cadastrar(){
        
        System.out.println("Usuário cadastrado com sucesso!");
        
    }
    
    
    
   public void login(){
       
       System.out.println("Login feito com sucesso!");
       
   }
   
   
   
    public void editarPerfil(){
       
       
       
   }  
    
    
    
     public void consultarPerfil(){
       
       
       
   }


   
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Usuario{");

        sb.append("idUsuario=").append(idUsuario);

        sb.append(", nome=").append(nome);

        sb.append(", email=").append(email);

        sb.append(", senha=").append(senha);

        sb.append(", habilidades=").append(habilidades);

        sb.append(", interesses=").append(interesses);

        sb.append(", dificuldades=").append(dificuldades);

        sb.append('}');

        return sb.toString();

    }
     
     
     
    
}
