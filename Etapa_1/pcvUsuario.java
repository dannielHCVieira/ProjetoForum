/** 
 * Esta classe representa o PAR CHAVE VALOR de um Usuario.
 * Chave: Id
 * Valor: Endereço
*/

import java.io.*;

public class pcvUsuario implements RegistroHashExtensivel<pcvUsuario> {
    private Integer id;
    private long endereco;
    private short TAMANHO = 20;

    public pcvUsuario(){
        this(-1,-1);
    }

    public pcvUsuario(int id, long endereco){
        this.id = id;
        this.endereco = endereco; 
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeLong(this.endereco);

        byte[] ba = baos.toByteArray();
        byte[] ba2 = new byte[TAMANHO];

        for (int i = 0; i < TAMANHO; i++)
            ba2[i] = ' ';
        for (int i = 0; i < ba.length && i < TAMANHO; i++)
            ba2[i] = ba[i];

        return ba2;
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.endereco = dis.readLong();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getEndereco() {
        return endereco;
    }

    public void setEndereco(long endereco) {
        this.endereco = endereco;
    }
    
}
