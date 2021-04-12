/** 
 * Esta classe representa o PAR CHAVE VALOR de um Usuario.
 * Chave: Id
 * Valor: Endereço
*/

import java.io.*;

public class pcvUsuario implements RegistroHashExtensivel<Integer,Long> {
    private Integer chave;
    private long valor;
    private short TAMANHO = 20;

    public pcvUsuario(){
        this(-1,-1);
    }

    public pcvUsuario(int chave, long valor){
        this.chave = chave;
        this.valor = valor; 
    }

    @Override
    public int hashCode() {
        return this.chave.hashCode();
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.chave);
        dos.writeLong(this.valor);

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
        this.chave = dis.readInt();
        this.valor = dis.readLong();
    }

    public Integer getChave() {
        return chave;
    }

    public void setChave(Integer chave) {
        this.chave = chave;
    }

    public Long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }
    
}
