package Modelo;


/**
 *
 * @author Carlos Daniel
 */
public class SkillShare {


    private Usuario usuario;

   
    public Usuario getUsuario() {

        return usuario;

    }



    public void setUsuario(Usuario usuario) {

        this.usuario = usuario;

    }


    public SkillShare() {

    }



    public SkillShare(Usuario usuario) {

        this.usuario = usuario;

    }



    public void Cadastro() {

        System.out.println("Usuário cadastrado com sucesso!");

    }



    public void Login() {

        System.out.println("Login realizado com sucesso!");

    }



    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("SkillShare{");

        sb.append("usuario=").append(usuario.getNome());

        sb.append('}');

        return sb.toString();

    }





}
