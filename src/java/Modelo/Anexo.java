package Modelo;


/**
 *
 * @author Carlos Daniel
 */
public class Anexo {


 private int idAnexo;


 private String nomeArquivo, tipo, url;


 private Postagem postagem;


    
    
    
        public int getidAnexo() {

            return idAnexo;

        }
    


        public void setidAnexo(int idAnexo) {

            this.idAnexo = idAnexo;

        }


    
        public String getNomeArquivo() {

            return nomeArquivo;

        }


    
        public void setNomeArquivo(String nomeArquivo) {

            this.nomeArquivo = nomeArquivo;

        }
    


        public String getTipo() {

            return tipo;

        }


    
        public void setTipo(String tipo) {

            this.tipo = tipo;

        }


    
        public String getUrl() {

            return url;

        }


    
        public void setUrl(String url) {

            this.url = url;

        }

        public Postagem getPostagem() {

            return postagem;

        }



        public void setPostagem(Postagem postagem) {

            this.postagem = postagem;

        }



        public Anexo() {
        }
    
        public Anexo(int idAnexo, String nomeArquivo, String tipo, String url) {


            this.idAnexo = idAnexo;


            this.nomeArquivo = nomeArquivo;


            this.tipo = tipo;


            this.url = url;


        }



   @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Anexo{");

        sb.append("idAnexo=").append(idAnexo);

        sb.append(", nomeArquivo=").append(nomeArquivo);

        sb.append(", tipo=").append(tipo);

        sb.append(", url=").append(url);

        sb.append('}');

        return sb.toString();

    }


        

}
