import java.io.*;
import java.util.Scanner;

public class Main {//extends RegistroHashExtensivel ?

  // Arquivo declarado fora de main() para ser poder ser usado por outros métodos
  private static CRUD<Usuario, pcvUsuario> arqPessoas;
  private static HashExtensivel<pcvEmail> hashEmail;

  public static int id_usuarioAtual;

  public static void main(String[] args) 
  {
    try{
      //criacao do crud
      arqPessoas = new CRUD<>(Usuario.class.getConstructor(),
      pcvUsuario.class.getDeclaredConstructor(int.class,long.class),
      pcvUsuario.class.getConstructor(), 
      "dados/usuario_hash_d.db","dados/usuario_hash_c.db","dados/usuarios.db");
      //criacao hashEmail
      hashEmail = new HashExtensivel<>(pcvEmail.class.getConstructor(), 4, "dados/email_hash_d.db", "dados/email_hash_c.db");

      int opcode = -1;
      Scanner sc = new Scanner(System.in);
      //menu principal
      while (opcode != 0)
      {
        menuPrincipal();
        opcode = sc.nextInt();
        sc.nextLine();
        switch (opcode)
        {
          case 1: acessoSistemaUsuario(sc);
            break;
          
          case 2: novoUsuario(sc);
            break;

          case 3: esqueciSenha(sc);
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

  //---------------------Menus------------------------

  public static void menuPrincipal()
  {
    System.out.println("\n=============");
    System.out.println("PERGUNTAS 1.0");
    System.out.println("=============");
    System.out.println("\nACESSO\n");
    System.out.println("1) Acesso ao sistema");
    System.out.println("2) Novo usuario (primeiro acesso)");
    System.out.println("3) Esqueci a senha\n");

    System.out.println("0) Sair");
    System.out.print("Digite sua escolha:");
  }

  public static void menuPerguntas(int notificacoes)
  {
    System.out.println("\n=============");
    System.out.println("PERGUNTAS 1.0");
    System.out.println("=============");
    System.out.println("\nINICIO\n");
    System.out.println("1) Criacao de perguntas");
    System.out.println("2) Consulta/responder perguntas");
    System.out.println("3) Notificacoes: "+notificacoes);
    System.out.println("\n0) Sair\n");
    System.out.print("Opcao:");
  }

  public static void menuCriacaoPergunta()
  {
    System.out.println("\n=============");
    System.out.println("PERGUNTAS 1.0");
    System.out.println("=============");
    System.out.println("\nINICIO > CRIACAO DE PERGUNTAS");
    System.out.println("\n1) Listar");
    System.out.println("2) Incluir");
    System.out.println("3) Alterar");
    System.out.println("4) Arquivar");
    System.out.println("\n0) Retornar ao menu anterior");
    System.out.print("\nOpcao:");
  }

  //-------------------Usuarios-------------------------
  public static void novoUsuario(Scanner sc)
  {
    try
    {
      System.out.println("\n=============");
      System.out.println("NOVO USUARIO");
      System.out.print("E-mail: ");
      //Ler email e verificar se ele está no sistema
      String email = sc.nextLine();
      String nome, senha, pergunta, resposta;
      if(email.length() == 0)
        System.out.println("Email inválido. Voltando para o menu...");
      else if(email.length() >= 34)
        System.out.println("O email não pode passar de 34 caracteres, atualmente tem "+email.length());
      else
      {
        pcvEmail verificacao = hashEmail.read(email.hashCode());
        if (verificacao != null) 
          System.out.println("Email já cadastrado. Voltando para o menu...");
        else
        {
          //criar um novo usuario no sistema com os dados inseridos
          Usuario user = new Usuario(); 
          do{
            System.out.print("Nome: ");
            nome = sc.nextLine();
          }while(nome.length() == 0);

          do{
            System.out.print("Senha: ");
            senha = sc.nextLine();
            System.out.print("Digite sua senha novamente: ");
          }while(!sc.nextLine().equals(senha) || senha.length() == 0);

          do{
            System.out.print("\nDigite uma pergunta secreta que sera usada caso esqueça a senha:");
            pergunta = sc.nextLine();
            System.out.print("\nAgora a resposta da pergunta:");
            resposta = sc.nextLine();
          }while(pergunta.length() == 0 && resposta.length() == 0);
          
          System.out.println("\n\nConfirme seus dados:");
          System.out.println("Email: " + email);
          System.out.println("Nome: " + nome);
          System.out.println("Senha: " + senha);
          System.out.println("Pergunta Secreta: " + pergunta);
          System.out.println("Resposta Secreta: " + resposta);

          System.out.print("Confirmar? (Y/N) ");
          if(sc.nextLine().toUpperCase().equals("Y")){
            user.setEmail(email);
            user.setNome(nome);
            user.setSenha(senha.hashCode());
            user.setPerguntaSecreta(pergunta);
            user.setRespostaSecreta(resposta);

            arqPessoas.create(user);
            pcvEmail criacao = new pcvEmail(user.getEmail(), user.getId());
            hashEmail.create(criacao);
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

  public static void acessoSistemaUsuario(Scanner sc)
  {
    try
    {
      System.out.println("\n=============");
      System.out.println("ACESSO AO SISTEMA");
      String email = "";
      int tam_email;
      do
      {
        System.out.print("E-mail: ");
        
        email = sc.nextLine();//Ler email e verificar se esse usuario ta cadastrado

        tam_email = email.length();
        if(tam_email >= 34)
          System.out.println("O email não pode passar de 34 caracteres, atualmente tem "+tam_email);
        else if(tam_email == 0)
          System.out.println("Email nao pode contar 0 caracteres");
      }while(tam_email > 0 && tam_email < 34);

      pcvEmail verificacao = hashEmail.read(email.hashCode());
      if (verificacao != null) 
      {
        Usuario user = arqPessoas.read(verificacao.getValor());
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        if (senha.hashCode() == user.getSenha()){
          //rediciona para a tela principal
          id_usuarioAtual = user.getId();
          sistemaPerguntas(sc);
        }
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

  public static void esqueciSenha(Scanner sc)
  {
    try
    {
      System.out.println("\n=============");
      System.out.println("ESQUECI A SENHA");
      System.out.print("E-mail: ");
      String email = sc.nextLine();
      pcvEmail verificacao;

      if(email.length() == 0)
        System.out.println("Email invalido. Voltando para o menu...");
      else
      {
        verificacao = hashEmail.read(email.hashCode());

        if(verificacao == null)
          System.out.println("Email não cadastrado. Voltando para o menu...");
        else
        {
          Usuario user = arqPessoas.read(verificacao.getValor());
          System.out.println("Pergunta Secreta: "+user.getPerguntaSecreta());
          String resposta = sc.nextLine();
          String senha;

          if(!resposta.equals(user.getRespostaSecreta()))
            System.out.println("Resposta errada. Voltando para o menu...");
          else
          {
            do
            {
              System.out.print("\nNova senha: ");
              senha = sc.nextLine();
              System.out.print("Digite novamente a nova senha: ");
            }while(!sc.nextLine().equals(senha) && senha.length() == 0);

            System.out.println("\n\nConfirme seus dados:");
            System.out.println("Senha: " + senha);
            System.out.print("Confirmar? (Y/N) ");
            if(sc.nextLine().toUpperCase().equals("Y"))
            {
              user.setSenha(senha.hashCode());

              arqPessoas.update(user);
              System.out.println("\nSenha atualizada com sucesso\n");
            }
            else
              System.out.println("\nAtualização de senha cancelada.\n");
          }
        }
      }
    }
    catch(Exception e){
       e.printStackTrace();
    }
  }

  //---------------------------Perguntas------------------------

  public static void sistemaPerguntas(Scanner sc)
  {
    int opcode = -1;
    //menu Perguntas
    while (opcode != 0)
    {
      menuPerguntas(0); 
      opcode = sc.nextInt();
      sc.nextLine();
      switch (opcode)
      {
        case 1: 
          criacaoPerguntas(sc);
          break;
        
        case 2: System.out.println("Consultar/responder perguntas sera implementado no futuro");
          break;

        case 3: System.out.println("Notificacoes sera implementado no futuro");
          break;
        case 0: 
          System.out.println("\nFechando sistema...");
          break;

        default:
          System.out.println("\n\nValor invalido, digite novamente");
      }
    }
  }

  public static void criacaoPerguntas(Scanner sc)
  {
    int opcode = -1;
    //menu Perguntas
    while (opcode != 0)
    {
      menuCriacaoPergunta(); 
      opcode = sc.nextInt();
      sc.nextLine();
      switch (opcode)
      {
        case 1: listaPerguntas(sc);
          System.out.println("Pressione qualquer tecla para continuar...");
          sc.nextLine();
          break;
        
        case 2: incluirPerguntas(sc);
          break;

        case 3: alterarPerguntas(sc);
          break;

        case 4: arquivamentoPerguntas(sc);
          break;

        case 0: 
          System.out.println("\nRetornando ao menu...");
          break;

        default:
          System.out.println("\n\nValor invalido, digite novamente");
      }
    }
  }

  public static void listaPerguntas(Scanner sc)
  {
    System.out.println("MINHAS PERGUNTAS\n");
    //faltando a lista de perguntas
  }

  public static void incluirPerguntas(Scanner sc)
  {
    System.out.print("\nDigite a sua pergunta:");
    String pergunta = sc.nextLine();
    //faltando a inclusao da pergunta
  }

  public static void alterarPerguntas(Scanner sc)
  {
    System.out.println("\nVeja qual pergunta voce deseja alterar, lembrando que apenas as perguntas que nao possuem repostas podem ser alteradas");
    listaPerguntas(sc);
    System.out.print("Opcao:");
    int pergunta = sc.nextInt();
    //faltando verificacao se a pergunta possui resposta
    System.out.print("\nDigite a sua nova pergunta:");
    String novaPergunta = sc.nextLine();
    //faltando atualizar pergunta propriamente dita
  }

  public static void arquivamentoPerguntas(Scanner sc)
  {
    System.out.println("\nEscolha a pergunta que vc deseja arquivar:");
    listaPerguntas(sc);
    System.out.print("Opcao:");
    int pergunta = sc.nextInt();
    //faltando arquivar a pergunta
  }
}