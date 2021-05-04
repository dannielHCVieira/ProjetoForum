import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Pergunta implements Registro
{
  private int idPergunta;
  private int idUsuario;
  private long criacao;
  private short nota;
  private String pergunta;
  private boolean ativa;

  public Pergunta()
  {
    this(-1,-1,"");
  }

  public Pergunta(int idPergunta, int idUsuario, String pergunta)
  {
    this.idPergunta = idPergunta;
    this.idUsuario = idUsuario;
    this.pergunta = pergunta;
    this.criacao = 0;
    this.nota = 0;
    this.ativa = true;
  }
  
  public byte[] toByteArray() throws IOException
  {
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

  public void fromByteArray(byte[] ba) throws IOException
  {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);  
    this.idPergunta = dis.readInt();
    this.idUsuario = dis.readInt();
    this.criacao = dis.readLong();
    this.nota = dis.readShort();
    this.pergunta = dis.readUTF();
    this.ativa = dis.readBoolean();
  } 

  public int getIdPergunta() 
  {
    return idPergunta;
  }

  public void setIdPergunta(int idPergunta) 
  {
    this.idPergunta = idPergunta;
  }

  public int getIdUsuario() 
  {
    return idUsuario;
  }

  public void setIdUsuario(int idUsuario) 
  {
    this.idUsuario = idUsuario;
  }

  public long getCriacao() 
  {
    return criacao;
  }

  public void setCriacao(long criacao) 
  {
    this.criacao = criacao;
  }

  public void setCriacao() 
  {
    Calendar calendar = calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
    this.criacao = calendar.getTimeInMillis();
  }

  public short getNota() 
  {
    return nota;
  }
  
  public void setNota(short nota) 
  {
    this.nota = nota;
  }

  public String getPergunta() 
  {
    return pergunta;
  }

  public void setPergunta(String pergunta) 
  {
    this.pergunta = pergunta;
  }

  public boolean isAtiva() 
  {
    return ativa;
  }

  public void setAtiva(boolean ativa) 
  {
    this.ativa = ativa;
  }

  @Override
  public String toString() 
  {
    return "Pergunta [ativa=" + ativa + ", criacao=" + criacao + ", idPergunta=" + idPergunta + ", idUsuario="
        + idUsuario + ", nota=" + nota + ", pergunta=" + pergunta + "]";
  }

  public int getId()
  {
    return getIdPergunta();
  }

  public void setId(int id)
  {
    setIdPergunta(id);
  }
}