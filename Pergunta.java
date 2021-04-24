import java.io.DataOutputStream;

public class Pergunta implements Registro{
  private int idPergunta;
  private int idUsuario;

  private long criacao;
  private short nota;
  private String pergunta;
  private boolean ativa;
  
  public byte[] toByteArray() throws IOException{
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    dos.writeInt(idPergunta);
    dos.writeInt(idUsuario);
    dos.writeLong(criacao);
    dos.writeShort(nota);
    dos.writeUTF(pergunta);
    dos.writeBoolean(ativa);

    return baos.toByteArray();
  }
  public void fromByteArray(byte[] ba) throws IOException{
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    
    this.idPergunta = dis.readInt();
    this.idUsuario = dis.readInt();
    this.criacao = dis.readLong();
    this.nota = dis.readShort();
    this.pergunta = dis.readUTF();
    this.ativa = dis.readBoolean();
  } 

  public int getIdPergunta() {
    return idPergunta;
  }
  public void setIdPergunta(int idPergunta) {
    this.idPergunta = idPergunta;
  }
  public int getIdUsuario() {
    return idUsuario;
  }
  public void setIdUsuario(int idUsuario) {
    this.idUsuario = idUsuario;
  }
  public long getCriacao() {
    return criacao;
  }
  public void setCriacao(long criacao) {
    this.criacao = criacao;
  }
  public short getNota() {
    return nota;
  }
  public void setNota(short nota) {
    this.nota = nota;
  }
  public String getPergunta() {
    return pergunta;
  }
  public void setPergunta(String pergunta) {
    this.pergunta = pergunta;
  }
  public boolean isAtiva() {
    return ativa;
  }
  public void setAtiva(boolean ativa) {
    this.ativa = ativa;
  }

  @Override
  public String toString() {
    return "Pergunta [ativa=" + ativa + ", criacao=" + criacao + ", idPergunta=" + idPergunta + ", idUsuario="
        + idUsuario + ", nota=" + nota + ", pergunta=" + pergunta + "]";
  }

  //NECESSARIO PARA USO NO CRUD
  public String getEmail(){

  }
  public void setEmail(String email){

  }
}