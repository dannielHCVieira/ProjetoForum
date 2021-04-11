import java.io.*;
import java.util.Scanner;

public class Main {

  // Arquivo declarado fora de main() para ser poder ser usado por outros métodos
  private static CRUD<Usuario> arqPessoas;

  public static void main(String[] args) 
  {
    int opmenu = -1;
    Scanner sc = new Scanner(System.in);
    while (opmenu != 0 && (opmenu < 0 || opmenu > 2))
    {
      menu();
      opmenu = sc.nextInt();
      switch (opmenu)
      {
        case 1: acessoSistema(sc);
          break;
        
        case 2: novoUsuario(sc);
          break;
        
        default:
          System.out.println("\n\nValor invalido, digite novamente");
      }
    }
    sc.close();
  }

  public static void menu()
  {
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
      arqPessoas = new CRUD<>(Usuario.class.getConstructor(), "usuarios.db");
      System.out.println("NOVO USUARIO");
      System.out.print("E-mail:");
      //Ler email e verificar se ele está no sistema
      String email = sc.next();
      if(email.length == 0){
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
          System.out.print("Digite seu nome:");
          user.setNome(sc.nextLine());
          System.out.print("Digite sua senha:");
          user.setSenha(sc.nextLine().hashCode());
          arqPessoas.create(user);
          System.out.println("Usuário cadastrado com sucesso");
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
      System.out.println("NOVO USUARIO");
      System.out.print("E-mail:");
      //Ler email e verificar se esse usuario ta cadastrado
      Usuario user = arqPessoas.read(sc.next());
      if (user != null)
      {
        System.out.print("Digite a sua senha:");
        int senha = sc.nextInt();
        if (senha == user.getSenha())
          //rediciona para a tela principal
          System.out.println("Redirecionando para tela principal");
        else
          System.out.println("Senha invalida");
      }
      else
        System.out.println("Email nao cadastrado");
    }
    catch(Exception erro)
    {
      erro.printStackTrace();
    }
  }
}