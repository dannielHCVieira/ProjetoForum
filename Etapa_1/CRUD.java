import java.io.*;
import java.lang.reflect.Constructor;


public class CRUD<T extends Registro, T2 extends RegistroHashExtensivel<T2>,
                  T3 extends RegistroHashExtensivel<T3>> {

    private RandomAccessFile raf;
    private Constructor<T> construtor;
    private HashExtensivel<pcvUsuario> hash; //private Constructor<T2> construtorHash;
    private HashExtensivel<pcvEmail> hash2;  //private Constructor<T3> construtorHashEmail;
    //private HashExtensivel<T2> hash;
    //private HashExtensivel<T3> hash2;
    private String fileString;

    public CRUD(Constructor<T> construtor, String fileString) throws Exception 
    {
        this.construtor = construtor;
        this.fileString = fileString;
        this.hash = new HashExtensivel<>(pcvUsuario.class.getConstructor(), 4, "dados/usuario.hash_d.db", "dados/usuario.hash_c.db");
        this.hash2 = new HashExtensivel<>(pcvEmail.class.getConstructor(), 4, "dados/email.hash_d.db", "dados/email.hash_c.db");
    }

    /**
     * 
    public CRUD(Constructor<T> construtor, Constructor<T2> construtorHash, 
                Constructor<T3> construtorHashEmail, String fileString) throws Exception 
    {
        this.construtor = construtor;
        this.construtorHash = construtorHash;
        this.construtorHashEmail = construtorHashEmail;
        this.fileString = fileString;
        this.hash = new HashExtensivel<>(this.construtorHash, 4, "dados/usuario.hash_d.db", "dados/usuario.hash_c.db");
        this.hash2 = new HashExtensivel<>(this.construtorHashEmail, 4, "dados/email.hash_d.db", "dados/email.hash_c.db");
    }
     */

    public void openFile() throws IOException {
        raf = new RandomAccessFile(this.fileString, "rw");
        if (raf.length() == 0)
            raf.writeInt(0);
    }

    public void closeFile() throws IOException {
        raf.close();
    }

    /**
     * Criação do registro no arquivo sequencial.
     * 
     * @param object
     * @return Id do registro
     * @throws Exception
     */
    public int create(T object) throws Exception {
        int ultimoId;

        //Abre o arquivo no inicio do mesmo.
        openFile();
        raf.seek(0);

        //Lê o últimoId e atribui o novo id ao objeto.
        ultimoId = raf.readInt();
        object.setId(ultimoId + 1);

        //Atualiza o ultimoId.
        raf.seek(0);
        raf.writeInt(object.getId());
        
        //Vai para o final do arquivo e escreve o registro.
        long endereco = raf.length();
        raf.seek(endereco);
        createRegistro(object);

        //Cria o indice na hash
        pcvUsuario pcvuser = new pcvUsuario(object.getId(), endereco);
        hash.create(pcvuser);
        pcvEmail pcvemail = new pcvEmail(object.getEmail(), object.getId());
        hash2.create(pcvemail);

        /**
         * hash.create(construtorHash.newInstance(object.getId(), endereco));
         * hash2.create(construtorHashEmail.newInstance(object.getEmail(), object.getId()));
         */

        closeFile();
        return object.getId();
    }

    /**
     * Lê um registro no arquivo sequencial através de um Id.
     * 
     * @param id
     * @return registro
     * @throws Exception
     */
    public T read(int id) throws Exception{
        T objeto = null;
        byte[] ba;
        int tam;
        boolean isDeleted;

        //Lê-se o hash procurando pelo endereco do id.
        pcvUsuario pcv = hash.read(Integer.valueOf(id).hashCode());

        openFile();

        //Caso o endereço exista e seja diferente de -1, prosseguir.
        if(pcv != null && pcv.getEndereco() != -1){
            objeto = this.construtor.newInstance();

            //Pular no arquivo para o endereço salvo no indice.
            raf.seek(pcv.getEndereco()); 
           
            //Lê-se a lápide.
            isDeleted = raf.readBoolean();
        
            //Caso o registro não tenha sido deletado, prossiga.
            if (!isDeleted) {
                tam = raf.readInt();

                //Lê se o registro e o transforma em objeto.
                ba = new byte[tam];
                raf.read(ba);
                objeto.fromByteArray(ba);

                //If de segurança, para caso o indice falhe.
                if (objeto.getId() != id) {
                    objeto = null;
                }
            } else {
                objeto = null;
            }
        }
        closeFile();
        return objeto;
    }

    /**
     * Lê um registro no arquivo sequencial através de um email.
     * 
     * @param email
     * @return registro
     * @throws Exception
     */
    public T read(String email) throws Exception{
        T objeto = null;
        byte[] ba;
        int tam;
        boolean isDeleted;

        //Lê-se o hash2 procurando pelo endereco do email.
        pcvEmail pcv = hash2.read(email.hashCode());

        openFile();

        //Caso o endereço exista e seja diferente de -1, prosseguir.
        if(pcv != null && pcv.getEndereco() != -1)
        {
            objeto = this.construtor.newInstance();

            //Pular no arquivo para o endereço salvo no indice.
            raf.seek(pcv.getEndereco()); 
           
            //Lê-se a lápide.
            isDeleted = raf.readBoolean();
        
            //Caso o registro não tenha sido deletado, prossiga.
            if (!isDeleted) 
            {
                tam = raf.readInt();

                //Lê se o registro e o transforma em objeto.
                ba = new byte[tam];
                raf.read(ba);
                objeto.fromByteArray(ba);

                //If de segurança, para caso o indice falhe.
                if (!objeto.getEmail().equals(email)) 
                    objeto = null;
            } 
            else
                objeto = null;
        }
        closeFile();
        return objeto;
    }

    /**
     * Atualiza um registro no arquivo sequencial.
     * 
     * @param registro
     * @return true caso tenha sucesso.
     * @throws Exception
     */
    public boolean update(T novoObjeto) throws Exception 
    {
        boolean resp = false, isDeleted;
        T objeto = this.construtor.newInstance();
        byte[] ba,novoBa;
        long pos;
        int tam;

        //Lê-se o hash procurando pelo endereco do id.
        pcvUsuario pcv = hash.read(Integer.valueOf(novoObjeto.getId()).hashCode());

        openFile();

        //Caso o endereço exista e seja diferente de -1, prosseguir.
        if(pcv != null && pcv.getEndereco() != -1)
        {
            //Pular no arquivo para o endereço salvo no indice.
            raf.seek(pcv.getEndereco());

            objeto = this.construtor.newInstance();

            //Guarda a posição no arquivo e lê a lapide.
            pos = raf.getFilePointer();
            isDeleted = raf.readBoolean();

            //Caso o registro não tenha sido deletado, prossiga.
            if (!isDeleted) 
            {
                tam = raf.readInt();
                
                //Lê se o registro e o transforma em objeto.
                ba = new byte[tam];
                raf.read(ba);
                objeto.fromByteArray(ba);

                //Pega o objeto atualizado e o transforma em byte array.
                novoBa = novoObjeto.toByteArray();

                //If de segurança, para caso o indice falhe.
                if (objeto.getId() == novoObjeto.getId()) 
                {
                    boolean emailIsEquals = false;
                    if (objeto.getEmail().equals(novoObjeto.getEmail()))
                        emailIsEquals = true;
                    if (novoBa.length <= ba.length) 
                    {
                        raf.seek(pos+5); //Pula os 5 bytes de lapide e tamRegistro, e depois escreve os dados.
                        raf.write(ba);
                    } 
                    else 
                    {
                        //Deleta o registro.
                        raf.seek(pos); 
                        raf.writeBoolean(true);
                        
                        //Cria um novo registro no final.
                        pos = raf.length();
                        raf.seek(pos);
                        createRegistro(novoObjeto);
                        
                        //Atualiza o indíce.
                        pcv = new pcvUsuario(novoObjeto.getId(), pos);
                        resp = hash.update(pcv);
                        if (emailIsEquals)
                        {
                            pcvEmail pcvemail = new pcvEmail(novoObjeto.getEmail(), pos);
                            resp = hash2.update(pcvemail);
                        }
                        else
                        {
                            pcvEmail pcvemail = hash.read(objeto.getEmail().hashCode());
                            hash2.delete(pcvemail.getEmail().hashCode());
                            pcvemail = new pcvEmail(novoObjeto.getEmail(), pos);
                            hash2.create(pcvemail); 
                        }
                    }
                }
            } 
        }
        closeFile();
        return resp;
    }

    /**
     * Deleta um registro no arquivo sequencial.
     * 
     * @param id
     * @return true caso tenha sucesso.
     * @throws Exception
     */
    public boolean delete(int id) throws Exception {
        boolean resp = false, isDeleted;
        T objeto = this.construtor.newInstance();
        byte[] ba;
        int tam;

        //Lê-se o hash procurando pelo endereco do id.
        pcvUsuario pcv = hash.read(Integer.valueOf(id).hashCode());

        openFile();

        //Caso o endereço exista e seja diferente de -1, prosseguir.
        if(pcv != null && pcv.getEndereco() != -1){
            //Pula para o endereco do objeto. E armazena o endereço. 
            raf.seek(pcv.getEndereco());

            objeto = this.construtor.newInstance();

            //Lê-se a lápide
            isDeleted = raf.readBoolean();
            //Caso o registro não tenha sido deletado, prossiga.
            if (!isDeleted) {
                tam = raf.readInt();

                //Lê se o registro e o transforma em objeto.
                ba = new byte[tam];
                raf.read(ba);
                objeto.fromByteArray(ba);

                //If de segurança, para caso o indice falhe.
                if (objeto.getId() == id) {
                    //Altera a lápide, deletando o registro.
                    raf.seek(pcv.getEndereco());
                    raf.writeBoolean(true);
                    
                    //Deleta o indice no hash e hash2.
                    resp = hash.delete(id);
                    hash2.delete(objeto.getEmail().hashCode());
                }
            }
        }
        closeFile();
        return resp;
    }

    //Metódo para criar registro.
    public void createRegistro(T objeto) throws Exception{
        if(raf != null){
            raf.writeBoolean(false);
            byte[] ba = objeto.toByteArray();
            raf.writeInt(ba.length);
            raf.write(ba);
        }
    }

}
