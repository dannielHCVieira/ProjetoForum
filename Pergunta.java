
public class Pergunta implements Registro{
  public int getId();
  public void setId(int n);
  
  public byte[] toByteArray() throws IOException;
  public void fromByteArray(byte[] ba) throws IOException; 
  public String toString();

  //NECESSARIO PARA USO NO CRUD
  public String getEmail();
  public void setEmail(String email);
}