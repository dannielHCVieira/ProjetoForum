import java.io.*;
import java.util.Scanner;

public class Main {

  // Arquivo declarado fora de main() para ser poder ser usado por outros métodos
  private static CRUD<Usuario, pcvUsuario, pcvEmail> arqPessoas;

  public static void main(String[] args) 
  {
    try{
      arqPessoas = new CRUD<>(Usuario.class.getConstructor(), 
      pcvUsuario.class.getDeclaredConstructor(int.class,long.class),
      pcvUsuario.class.getConstructor(), 
      pcvEmail.class.getDeclaredConstructor(String.class,int.class),
      pcvEmail.class.getConstructor(),"Etapa_1/dados/usuarios.db");

      int opmenu = -1;
      Scanner sc = new Scanner(System.in);
      while (opmenu != 0)
      {
        menu();
        opmenu = sc.nextInt();
        sc.nextLine();
        switch (opmenu)
        {
          case 1: acessoSistema(sc);
            break;
          
          case 2: novoUsuario(sc);
            break;
          
          case 0: 
            System.out.println("\nFechando sistema...");
            break;

          default:
            System.out.println("\n\nValor invalido, digite novamente");
        }
      }
      sc.close();
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void menu()
  {
    System.out.println("\n=============");
    System.out.println("PERGUNTAS 1.0");
    System.out.println("=============");
    System.out.println("\nACESSO\n");
    System.out.println("1) Acesso ao sistema");
    System.out.println("2) Novo usuario (primeiro acesso)\n");
    System.out.println("0) Sair");
    System.out.print("Digite sua escolha:");
  }

  public static void novoUsuario(Scanner sc)
  {
    try
    {
      System.out.println("\n=============");
      System.out.println("NOVO USUARIO");
      System.out.print("E-mail: ");
      //Ler email e verificar se ele está no sistema
      String email = sc.nextLine(), nome, senha;
      if(email.length() == 0){
        System.out.println("Email inválido. Voltando para o menu...");
      }
      else{
        Usuario user = arqPessoas.read(email); 
        if (user != null) 
          System.out.println("Email já cadastrado. Voltando para o menu...");
        else
        {
          //criar um novo usuario no sistema com os dados inseridos
          user = new Usuario(); 
          do{
            System.out.print("Nome: ");
            nome = sc.nextLine();
          }while(nome.length() == 0);

          do{
            System.out.print("Senha: ");
            senha = sc.nextLine();
            System.out.print("Digite sua senha novamente: ");
          }while(!sc.nextLine().equals(senha) || senha.length() == 0);
          
          System.out.println("\n\nConfirme seus dados:");
          System.out.println("Email: " + email);
          System.out.println("Nome: " + nome);
          System.out.println("Senha: " + senha);

          System.out.print("Confirmar? (Y/N) ");
          if(sc.nextLine().toUpperCase().equals("Y")){
            user.setEmail(email);
            user.setNome(nome);
            user.setSenha(senha.hashCode());

            arqPessoas.create(user);
            System.out.println("\nUsuário cadastrado com sucesso\n");
          }
          else{
            System.out.println("\nCadastro de Usuário cancelado.\n");
          }
        }
      }
    }
    catch (Exception erro)
    {
      erro.printStackTrace();
    }
  }

  public static void acessoSistema(Scanner sc)
  {
    try
    {
      System.out.println("\n=============");
      System.out.println("NOVO USUARIO");
      System.out.print("E-mail: ");
      //Ler email e verificar se esse usuario ta cadastrado
      String email = sc.nextLine();
      Usuario user = arqPessoas.read(email); 
      if (user != null) 
      {
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        if (senha.hashCode() == user.getSenha())
          //rediciona para a tela principal
          System.out.println("\nRedirecionando para tela principal\n");
        else
          System.out.println("\nSenha invalida\n");
      }
      else
        System.out.println("\nEmail nao cadastrado\n");
    }
    catch(Exception erro)
    {
      erro.printStackTrace();
    }
  }
}