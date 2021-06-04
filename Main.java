import java.io.*;
import java.util.*;
import java.text.*;

public class Main {
  // Arquivo declarado fora de main() para ser poder ser usado por outros métodos
  private static CRUD<Usuario, pcvUsuario> arqPessoas;
  private static CRUD<Pergunta, pcvPergunta> arqPerguntas;
  private static CRUD<Resposta, pcvResposta> arqRespostas;
  private static HashExtensivel<pcvEmail> hashEmail;
  private static ArvoreBMais_ChaveComposta_Int_Int arvB_UsuarioPergunta;
  private static ArvoreBMais_ChaveComposta_Int_Int arvB_PerguntaResposta;
  private static ArvoreBMais_ChaveComposta_Int_Int arvB_UsuarioResposta;
  private static ListaInvertida indInver;

  public static int usuarioAtual;
  public static int perguntaAtual;

  public static void main(String[]args) {
    try {
      // criacao do crud
      arqPessoas = new CRUD<>(Usuario.class.getConstructor(),
          pcvUsuario.class.getDeclaredConstructor(int.class, long.class), 
          pcvUsuario.class.getConstructor(),
          "dados/usuario_hash_d.db", "dados/usuario_hash_c.db", "dados/usuarios.db");
      arqPerguntas = new CRUD<>(Pergunta.class.getConstructor(),
          pcvPergunta.class.getDeclaredConstructor(int.class, long.class), pcvPergunta.class.getConstructor(),
          "dados/pergunta_hash_d.db", "dados/pergunta_hash_c.db", "dados/perguntas.db");
      arqRespostas = new CRUD<>(Resposta.class.getConstructor(),
          pcvResposta.class.getDeclaredConstructor(int.class, long.class),
          pcvResposta.class.getConstructor(),
          "dados/resposta_hash_d.db","dados/resposta_hash_c.db","dados/resposta.db");
      // criacao hashEmail
      hashEmail = new HashExtensivel<>(pcvEmail.class.getConstructor(), 4, "dados/email_hash_d.db",
          "dados/email_hash_c.db");
      // criacao arvoreB+_idUsuario_idPergunta
      arvB_UsuarioPergunta = new ArvoreBMais_ChaveComposta_Int_Int(255,        "dados/arvore_usuario_pergunta.db");

      arvB_PerguntaResposta = new ArvoreBMais_ChaveComposta_Int_Int(255, "dados/arvore_resposta_pergunta.db");

      arvB_UsuarioResposta = new ArvoreBMais_ChaveComposta_Int_Int(255, "dados/arvore_usuario_resposta.db");
      
      // criacao indice invertido
      indInver = new ListaInvertida(255, "dados/listainvertida_dict.db", 
        "dados/listainvertida_blocos.db");

      Scanner sc = new Scanner(System.in);
      // menu principal
      sistemaUsuario(sc);
      sc.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // ---------------------Menus------------------------
  public static void menuPrincipal() {
    System.out.println("\n=============");
    System.out.println("PERGUNTAS 1.0");
    System.out.println("=============");
    System.out.println("\nACESSO\n");
    System.out.println("1) Acesso ao sistema");
    System.out.println("2) Novo usuario (primeiro acesso)");
    System.out.println("3) Esqueci a senha\n");
    System.out.println("0) Sair\n");
    System.out.print("Digite sua escolha:");
  }

  public static void menuPerguntas(int notificacoes) {
    menuPadrao();
    System.out.println("1) Criacao/Gerenciamento de perguntas");
    System.out.println("2) Consulta/responder perguntas");
    System.out.println("3) Gerenciar suas respostas");
    System.out.println("4) Notificacoes: " + notificacoes);
    System.out.println("\n0) Sair\n");
    System.out.print("Opcao:");
  }

  public static void menuCriacaoPergunta() {
    menuPadrao();
    System.out.println("> CRIACAO DE PERGUNTAS");
    System.out.println("\n1) Listar");
    System.out.println("2) Incluir");
    System.out.println("3) Alterar");
    System.out.println("4) Arquivar");
    System.out.println("5) Desarquivados");
    System.out.println("\n0) Retornar ao menu anterior");
    System.out.print("\nOpcao:");
  }

  public static void menuConsultaPerguntas() {
    menuPadrao();
    System.out.println("> PERGUNTAS\n");
    System.out.println("Busque as perguntas por palavra chave separadas por ponto e vírgula");
    System.out.println("Ex: política;Brasil;eleições\n");
    System.out.print("Palavras chave: ");
  }

  public static void menuRespostas() {
    menuPadrao();
    System.out.println("> GERENCIADOR DE RESPOSTAS");
  }

  public static void menuGerenciarRespostas() {
    System.out.println("\n\n1)Alterar Resposta"+
    "\n2)Arquivar Resposta"+
    "\n\n0)Sair");
  }

  public static void menuPadrao() {
    System.out.println("\n=============");
    System.out.println("PERGUNTAS 1.0");
    System.out.println("=============");
    System.out.println("\nINICIO");
  }

  // -------------------Usuarios-------------------------
  public void sistemaUsuario(Scanner sc) {
    int opcode = -1;
    do {
      menuPrincipal();
      opcode = sc.nextInt();
      sc.nextLine();
      switch (opcode) {
      case 1:
        acessoSistemaUsuario(sc);
        break;
      case 2:
        novoUsuario(sc);
        break;
      case 3:
        esqueciSenha(sc);
        break;
      case 0:
        System.out.println("\nFechando sistema...");
        break;
      default:
        System.out.println("\n\nValor invalido, digite novamente");
      }
    } while (opcode != 0);
  }

  public void acessoSistemaUsuario(Scanner sc) {
    try {
      System.out.println("\n=============");
      System.out.println("ACESSO AO SISTEMA");
      String email = "";
      int tam_email;
      do {
        System.out.print("E-mail: ");
        email = sc.nextLine();// Ler email e verificar se esse usuario ta cadastrado
        tam_email = email.length();
        if (tam_email >= 34)
          System.out.println("O email não pode passar de 34 caracteres, atualmente tem " + tam_email);
        else if (tam_email == 0)
          System.out.println("Email nao pode contar 0 caracteres");
      } while (tam_email == 0 || tam_email >= 34);
      pcvEmail verificacao = hashEmail.read(email.hashCode());
      if (verificacao != null) {
        Usuario user = arqPessoas.read(verificacao.getValor());
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        if (senha.hashCode() == user.getSenha()) {
          // rediciona para a tela principal
          usuarioAtual = user.getId();
          sistemaPerguntas(sc);
        } else
          System.out.println("\nSenha invalida");
      } else
        System.out.println("\nEmail nao cadastrado");
    } catch (Exception erro) {
      erro.printStackTrace();
    }
  }

  public static void novoUsuario(Scanner sc) {
    try {
      System.out.println("\n=============");
      System.out.println("NOVO USUARIO");
      System.out.print("E-mail: ");
      // Ler email e verificar se ele está no sistema
      String email = sc.nextLine();
      String nome, senha, pergunta, resposta;
      if (email.length() == 0)
        System.out.println("Email inválido. Voltando para o menu...");
      else if (email.length() >= 34)
        System.out.println("O email não pode passar de 34 caracteres, atualmente tem " + email.length());
      else {
        pcvEmail verificacao = hashEmail.read(email.hashCode());
        if (verificacao != null)
          System.out.println("Email já cadastrado. Voltando para o menu...");
        else {
          // criar um novo usuario no sistema com os dados inseridos
          Usuario user = new Usuario();
          do {
            System.out.print("Nome: ");
            nome = sc.nextLine();
          } while (nome.length() == 0);
          do {
            System.out.print("Senha: ");
            senha = sc.nextLine();
            System.out.print("Digite sua senha novamente: ");
          } while (!sc.nextLine().equals(senha) || senha.length() == 0);
          do {
            System.out.print("\nDigite uma pergunta secreta que sera usada caso esqueça a senha:");
            pergunta = sc.nextLine();
            System.out.print("\nAgora a resposta da pergunta:");
            resposta = sc.nextLine();
          } while (pergunta.length() == 0 && resposta.length() == 0);
          System.out.println("\n\nConfirme seus dados:");
          System.out.println("Email: " + email);
          System.out.println("Nome: " + nome);
          System.out.println("Senha: " + senha);
          System.out.println("Pergunta Secreta: " + pergunta);
          System.out.println("Resposta Secreta: " + resposta);
          System.out.print("Confirmar? (Y/N) ");
          if (sc.nextLine().toUpperCase().equals("Y")) {
            user.setEmail(email);
            user.setNome(nome);
            user.setSenha(senha.hashCode());
            user.setPerguntaSecreta(pergunta);
            user.setRespostaSecreta(resposta);
            arqPessoas.create(user);
            pcvEmail criacao = new pcvEmail(user.getEmail(), user.getId());
            hashEmail.create(criacao);
            System.out.println("\nUsuário cadastrado com sucesso\n");
          } else {
            System.out.println("\nCadastro de Usuário cancelado.\n");
          }
        }
      }
    } catch (Exception erro) {
      erro.printStackTrace();
    }
  }

  public static void esqueciSenha(Scanner sc) {
    try {
      System.out.println("\n=============");
      System.out.println("ESQUECI A SENHA");
      System.out.print("E-mail: ");
      String email = sc.nextLine();
      pcvEmail verificacao;
      if (email.length() == 0)
        System.out.println("\nEmail invalido. Voltando para o menu...");
      else {
        verificacao = hashEmail.read(email.hashCode());
        if (verificacao == null)
          System.out.println("\nEmail não cadastrado. Voltando para o menu...");
        else {
          Usuario user = arqPessoas.read(verificacao.getValor());
          System.out.print("\nPergunta Secreta: " + user.getPerguntaSecreta() + ":");
          String resposta = sc.nextLine();
          String senha;
          if (!resposta.equals(user.getRespostaSecreta()))
            System.out.println("\nResposta errada. Voltando para o menu...");
          else {
            do {
              System.out.print("\nNova senha: ");
              senha = sc.nextLine();
              System.out.print("Digite novamente a nova senha: ");
            } while (!sc.nextLine().equals(senha) && senha.length() == 0);
            System.out.println("\n\nConfirme seus dados:");
            System.out.println("Senha: " + senha);
            System.out.print("Confirmar? (Y/N) ");
            if (sc.nextLine().toUpperCase().equals("Y")) {
              user.setSenha(senha.hashCode());
              arqPessoas.update(user);
              System.out.println("\nSenha atualizada com sucesso\n");
            } else
              System.out.println("\nAtualização de senha cancelada.\n");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  // ---------------------------Perguntas------------------------

  public void sistemaPerguntas(Scanner sc) throws Exception {
    int opcode = -1;
    // menu Perguntas
    while (opcode != 0) {
      menuPerguntas(0);
      opcode = sc.nextInt();
      sc.nextLine();
      switch (opcode) {
      case 1:
        criacaoPerguntas(sc);
        break;
      case 2:
        consultaPerguntas(sc);
        break;
      case 3:
        gerenciarRespostas(sc);
        break;
      case 4:
        System.out.println("\nNotificacoes sera implementado no futuro");
        break;
      case 0:
        System.out.println("\nFechando sistema...");
        break;
      default:
        System.out.println("\n\nValor invalido, digite novamente");
      }
    }
  }

  public static void criacaoPerguntas(Scanner sc) {
    int opcode = -1;
    // menu Perguntas
    while (opcode != 0) {
      menuCriacaoPergunta();
      opcode = sc.nextInt();
      sc.nextLine();
      switch (opcode) {
      case 1:
        listaPerguntas(sc,true);
        System.out.println("Pressione Enter para continuar...");
        sc.nextLine();
        break;
      case 2:
        incluirPerguntas(sc);
        break;
      case 3:
        alterarPerguntas(sc);
        break;
      case 4:
        arquivamentoPerguntas(sc);
        break;
      case 5:
        desarquivamentoPerguntas(sc);
        break;
      case 0:
        System.out.println("\nRetornando ao menu...");
        break;
      default:
        System.out.println("\n\nValor invalido, digite novamente");
      }
    }
  }

  public static int[] listaPerguntas(Scanner sc, boolean ativa) {
    int[] arrayIdPerguntas = new int[0];
    int[] arrayIdPerguntasSelecionadas = new int[0];
    try {
      System.out.println("\nMINHAS PERGUNTAS\n");
      // Lê-se todos os idPerguntas do usuario atual
      arrayIdPerguntas = arvB_UsuarioPergunta.read(usuarioAtual);

      // Imprime cada pergunta a partir do idPergunta pesquisado no indice.
      Pergunta p;
      for (int i = 0; i < arrayIdPerguntas.length; i++) {
        p = arqPerguntas.read(arrayIdPerguntas[i]);
        if (p.isAtiva() == ativa) {
          arrayIdPerguntasSelecionadas = VetorIntVariavel(arrayIdPerguntas, arrayIdPerguntas[i]);
          System.out.print(i + 1 + ".");
          printPergunta(p);
        }
      }
      if (arrayIdPerguntas.length == 0)
        System.out.println("\nNenhuma pergunta disponivel");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return arrayIdPerguntasSelecionadas;
  }

  public static void incluirPerguntas(Scanner sc) {
    try {
      String pergunta = "";
      do {
        System.out.print("\nDigite a sua pergunta:");
        pergunta = sc.nextLine();
      } while (pergunta.length() == 0);

      String palavrasChave = "";
      do {
        System.out.print("\nDigite as palavras-chaves da sua pergunta(ex.: galinha;atravessou;rua):");
        palavrasChave = sc.nextLine();
      } while (palavrasChave.length() == 0);

      System.out.println("\n\nConfirme seus dados:");
      System.out.println("Pergunta: " + pergunta);
      System.out.println("Palavras-chave: " + palavrasChave);
      System.out.print("Confirmar? (Y/N) ");
      if (sc.nextLine().toUpperCase().equals("Y")) {
        Pergunta question = new Pergunta();
        question.setIdUsuario(usuarioAtual);
        question.setPergunta(pergunta);
        question.setNota((short) 0);
        question.setCriacao(); // Metodo interno já obtem a data e a hora.
        question.setAtiva(true);
        question.setPalavrasChave(palavrasChave);

        arqPerguntas.create(question);
        arvB_UsuarioPergunta.create(usuarioAtual, question.getId());
        criarIndInver(question.getId(), formatarPalavrasChave(palavrasChave));
        System.out.println("\nPergunta criada com sucesso.\n");
      } else
        System.out.println("\nCriação de pergunta cancelada.\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void alterarPerguntas(Scanner sc) {
    try {
      int[] listaPerguntas = listaPerguntas(sc,true);
      if (listaPerguntas.length == 0) {
        System.out.println("\nNão há perguntas para alterar.");
      } else {
        System.out.println(
            "\nVeja qual pergunta voce deseja alterar, lembrando que apenas as perguntas que nao possuem repostas podem ser alteradas");

        System.out.println("\nDigite o número da pergunta que queira alterar.");
        System.out.println("\n0) Retornar para CRIACAO DE PERGUNTAS");
        System.out.print("\nOpcao:");
        int iPergunta = sc.nextInt();
        sc.nextLine();
        if (iPergunta != 0 || iPergunta <= listaPerguntas.length) {
          Pergunta pergunta = arqPerguntas.read(listaPerguntas[iPergunta - 1]);

          if (pergunta.isAtiva()) {
            System.out.println("\nPergunta:");
            printPergunta(pergunta);

            String perguntaAlt;
            do {
              System.out.print("Digite a pergunta alterada:");
              perguntaAlt = sc.nextLine();
            } while (perguntaAlt.length() == 0);

            String palavrasChaveAlt;
            do {
              System.out.println("Digite as palavras-chave(ex.: galinha;atravessou;rua):");
              palavrasChaveAlt = sc.nextLine();
            } while (palavrasChaveAlt.length() == 0);

            if (perguntaAlt.length() != 0) {
              System.out.println("\nConfirme seus dados:");
              System.out.println("Pergunta: " + perguntaAlt);
              System.out.println("Palavras-chave: " + palavrasChaveAlt);
              System.out.print("Confirmar? (Y/N) ");
              if (sc.nextLine().toUpperCase().equals("Y")) {
                pergunta.setPergunta(perguntaAlt);

                alterarIndInver(pergunta, palavrasChaveAlt);
                arqPerguntas.update(pergunta);
                System.out.println("\nPergunta atualizada com sucesso.\n");
              } else
                System.out.println("\nAtualização de pergunta cancelada.\n");
            } else
              System.out.println("\nAtualização vazia.\n");
          } else
            System.out.println("\nPergunta arquivada não pode ser alterada.\n");
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void arquivamentoPerguntas(Scanner sc) 
  {
    try 
    { 
      int[] listaPerguntas = listaPerguntas(sc,true); 
      if(listaPerguntas.length == 0) 
        System.out.println("\nNão há perguntas para arquivar."); 
      else 
      {
        System.out.println("\n0) Retornar para CRIACAO DE PERGUNTAS");
        System.out.println("\nDigite o número da pergunta que queira arquivar.");
        System.out.println("\nEscolha a pergunta que vc deseja arquivar.");
        System.out.print("\nOpcao:"); 
        int iPergunta = sc.nextInt(); 
        sc.nextLine(); 
        if (iPergunta != 0 || iPergunta <= listaPerguntas.length) 
        {
          Pergunta pergunta = arqPerguntas.read(listaPerguntas[iPergunta-1]);

          System.out.println("\n\nConfirme seus dados:"); 
          System.out.print("Pergunta:"); 
          printPergunta(pergunta); 
          System.out.print("\nConfirmar? (Y/N) ");

          if(sc.nextLine().toUpperCase().equals("Y")) 
          {
            pergunta.setAtiva(false);

            String[] split = pergunta.getPalavrasChave().split(";"); 
            for(String str : split)
              indInver.delete(str,pergunta.getId());

            arqPerguntas.update(pergunta); 
            System.out.println("\nPergunta arquivada com sucesso.\n"); 
          } 
          else 
            System.out.println("\nArquivamento de pergunta cancelada.\n"); 
        } 
      } 
    } 
    catch(Exception e) 
    { 
      e.printStackTrace(); 
    }
  }

  public static void desarquivamentoPerguntas(Scanner sc) 
  {
    try
    { 
      int[] listaPerguntas = listaPerguntas(sc,false); 
      if(listaPerguntas.length == 0) 
        System.out.println("\nNão há perguntas arquivadas."); 
      else
      {
        System.out.println("\n0) Retornar para CRIACAO DE PERGUNTAS");
        System.out.println("\nDigite o número da pergunta que queira desarquivar.");
        System.out.println("\nEscolha a pergunta que vc deseja desarquivar.");
        System.out.print("\nOpcao:"); 
        int iPergunta = sc.nextInt(); 
        sc.nextLine(); 
        if (iPergunta != 0 || iPergunta <= listaPerguntas.length) 
        { 
          Pergunta pergunta = arqPerguntas.read(listaPerguntas[iPergunta-1]);
      
          System.out.println("\n\nConfirme seus dados:"); 
          System.out.print("Pergunta: "); 
          printPergunta(pergunta); 
          System.out.print("\nConfirmar? (Y/N) ");
          if(sc.nextLine().toUpperCase().equals("Y")) 
          { 
            pergunta.setAtiva(true);
          
            criarIndInver(pergunta.getId(), formatarPalavrasChave(pergunta.getPalavrasChave()));
            arqPerguntas.update(pergunta); 
            System.out.println("\nPergunta desarquivada com sucesso.\n"); 
          } 
          else 
            System.out.println("\nDesarquivamento de pergunta cancelada.\n"); 
        } 
      } 
    }
    catch(Exception e)
    { 
      e.printStackTrace(); 
    }
  }
  // ------------Consulta/Responder pergunta---------------

  public static void consultaPerguntas(Scanner sc) {
    try {
      ArrayList<Pergunta> listaPerguntas = listaPerguntasUsuario(sc);

      int iPergunta = sc.nextInt();
      sc.nextLine();
      if (iPergunta != 0) {
        Pergunta perguntaSelecionada = listaPerguntas.get(iPergunta - 1);
        printPerguntaExibida(perguntaSelecionada);

        perguntaAtual = perguntaSelecionada.getId();

        listarTodasRespostas(sc);
        System.out.println(
        "1) Responder\n"+
        "2) Avaliar\n\n"+
        "0) Retornar");
        System.out.print("Opção: ");  

        int opcode = sc.nextInt();
        sc.nextLine();
        switch (opcode) {
        case 0:
          System.out.println("\nVoltando para o menu...");
          break;
        case 1:
          incluirResposta(sc);
          break;
        default:
          System.out.println("\nEm breve...");
        }
      } else {
        System.out.println("\nVoltando para o menu...");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<Pergunta> listaPerguntasUsuario(Scanner sc) {
    menuConsultaPerguntas();
    String palavrasChave = sc.nextLine();

    palavrasChave = formatarPalavrasChave(palavrasChave);
    ArrayList<Pergunta> listaPerguntas = buscarPerguntas(palavrasChave);

    System.out.println("\nRESULTADO DA PESQUISA:");
    int i = 1;
    for (Pergunta p : listaPerguntas) {
      System.out.print(i++ + ".");
      printPerguntaResumido(p);
    }
    if (listaPerguntas.size() == 0)
      System.out.println("\nNenhuma pergunta disponivel");
    System.out.println("\n0) Retornar para CRIACAO DE PERGUNTAS");
    System.out.println("\nDigite o número da pergunta que queira visualizar.");
    return listaPerguntas;
  }

  public static ArrayList<Pergunta> buscarPerguntas(String palavrasChave) {
    ArrayList<Pergunta> listPerguntas = new ArrayList<Pergunta>();
    try {
      String[] split = palavrasChave.split(";");

      int[] listIds = indInver.read(split[0]);
      HashSet<Integer> setId = new HashSet<Integer>();
      for (int i : listIds) {
        setId.add(i);
      }

      for (String str : split) {
        listIds = indInver.read(str);
        HashSet<Integer> setNewId = new HashSet<Integer>();
        for (int i : listIds) {
          setNewId.add(i);
        }

        setId.retainAll(setNewId);
      }

      for (int id : setId) {
        Pergunta p = arqPerguntas.read(id);
        listPerguntas.add(p);
      }

      Collections.sort(listPerguntas, Pergunta.comparadorNota);
    } 
    catch (Exception e) 
    {
      e.printStackTrace();
    }

    return listPerguntas;

  }

  // ------------Respostas---------------

  public static void incluirResposta(Scanner sc){
    try{
      String resposta = "";
      do {
        System.out.print("\nDigite a sua resposta:");
        resposta = sc.nextLine();
      } while (resposta.length() == 0);

      System.out.println("\n\nConfirme seus dados:");
      System.out.println("Resposta: " + resposta);
      System.out.print("Confirmar? (Y/N) ");
      if (sc.nextLine().toUpperCase().equals("Y")) {
        Resposta answer = new Resposta();
        answer.setIdUsuario(usuarioAtual);
        answer.setIdPergunta(perguntaAtual);
        answer.setResposta(resposta);
        answer.setCriacao(); 

        arqRespostas.create(answer);
        arvB_PerguntaResposta.create(perguntaAtual, answer.getId());
        arvB_UsuarioResposta.create(usuarioAtual, answer.getId());
        System.out.println("\nResposta criada com sucesso.\n");
      } else
        System.out.println("\nInclusão de resposta cancelada.\n");
    }
    catch(Exception e){

    }
  }

  public static void AlterarRespostas(Scanner sc, Resposta r) {
    if (r.isAtiva()) {
      String respostaAlt;
      do {
        System.out.print("Digite a resposta alterada:");
        respostaAlt = sc.nextLine();
      } while (respostaAlt.length() == 0);

      if (respostaAlt.length() != 0) {
        System.out.println("\nConfirme seus dados:");
        System.out.println("resposta: " + respostaAlt);
        System.out.print("Confirmar? (Y/N) ");
        if (sc.nextLine().toUpperCase().equals("Y")) {
          r.setresposta(respostaAlt);

          arqrespostas.update(resposta);
          System.out.println("\nresposta atualizada com sucesso.\n");
        } else
          System.out.println("\nAtualização de resposta cancelada.\n");
      } else
        System.out.println("\nAtualização vazia.\n");
    } else
      System.out.println("\nresposta arquivada não pode ser alterada.\n");
  }

  public static void ArquivarResposta(Scanner sc, Resposta r) {
    System.out.println("\n\nConfirme seus dados:"); 
    System.out.print("resposta:"); 
    printResposta(r); 
    System.out.print("\nConfirmar? (Y/N) ");

    if(sc.nextLine().toUpperCase().equals("Y")) 
    {
      r.setAtiva(false);

      arqRespostas.update(r); 
      System.out.println("\nresposta arquivada com sucesso.\n"); 
    } 
    else 
      System.out.println("\nArquivamento de resposta cancelada.\n"); 
  }

  public void gerenciarRespostas(Scanner sc) throws Exception{
    menuRespostas();
    //Procurar respostas do usuario
    int[] idRespostasUser = this.arvB_UsuarioResposta.read(this.usuarioAtual);
    Pergunta[] PerguntasRespondidasUser = new Pergunta[idRespostasUser.length+1];
    Resposta[] RespostasUser = new Resposta[idRespostasUser.length+1];
    for (int i = 1;i < PerguntasRespondidasUser.length+1;i++){
      //Pegar respostas do user
      RespostasUser[i] = this.arqRespostas.read(idRespostasUser[i-1]);
      //Pegar perguntas respondidas
      PerguntasRespondidasUser[i] = this.arqPerguntas.read(RespostasUser[i].getIdPergunta());
      System.out.println("-----"+i+". Pergunta-----");
      printPergunta(PerguntasRespondidasUser[i]);
      System.out.println("\n------- Resposta -------");
      printResposta(RespostasUser[i]);
    }
    System.out.print("\n\n0)Retornar para o INICIO"+
    "\nEscolha uma das respostas a cima para gerenciar"+
    "\nopcao:");
    int opcao = 0;
    do{
      opcao = sc.nextInt();
      if (opcao != 0 && opcao < PerguntasRespondidasUser.length)
      {
        menuRespostas();
        System.out.println("\n\n-----"+opcao+". Pergunta-----");
        printPergunta(PerguntasRespondidasUser[opcao]);
        System.out.println("\n------- Resposta -------");
        printResposta(RespostasUser[opcao]);
        
        menuGerenciarRespostas();
        System.out.println("Opcao:");
        int opcaoGerenciarResposta = 0;
        do{
          opcaoGerenciarResposta = sc.nextInt();
          switch (opcaoGerenciarResposta){
            case 0: 
              System.out.println("Voltando ao Gerenciador de Respostas...");
              break;
            case 1:
              AlterarRespostas(sc, RespostasUser[opcaoGerenciarResposta]);
              break;
            case 2:
              ArquivarResposta(sc, RespostasUser[opcaoGerenciarResposta]);
              break;
            default:
              System.out.println("Valor invalido, digite novamente");
          }
        }while(opcaoGerenciarResposta != 0);
      }
      else if(opcao != 0)
      {
        System.out.println("Valor invalido digite novamente");
      }
    }while(opcao != 0);
    System.out.println("\n\nVoltando ao INICIO...");
  }

  public static int[] listarTodasRespostas(Scanner sc){
    int[] arrayIdRespostas = new int[0];
    int[] arrayIdRespostasSelecionadas = new int[0];
    try{
      arrayIdRespostas = arvB_PerguntaResposta.read(perguntaAtual);

      Resposta r;
      for (int i = 0; i < arrayIdRespostas.length; i++) {
        r = arqRespostas.read(arrayIdRespostas[i]);
        if (r.isAtiva()) {
          arrayIdRespostasSelecionadas = VetorIntVariavel(arrayIdRespostas, arrayIdRespostas[i]);
          System.out.println(i + 1 + ".");
          printResposta(r);
        }
      }
      if (arrayIdRespostas.length == 0)
        System.out.println("\nNenhuma resposta disponivel");         
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return arrayIdRespostasSelecionadas;
  }

  // ------------IndiceInvertido------------

  public static void criarIndInver(int id, String palavrasChave) throws Exception {
    String[] split = palavrasChave.split(";");
    for (int i = 0; i < split.length; i++) {
      indInver.create(split[i], id);
    }
  }

  public static void alterarIndInver(Pergunta pergunta, String palavrasChaveAlt) throws Exception {
    HashSet<String> keyWords = new HashSet<String>(), keyWordsAlt = new HashSet<String>(),
        keyWordsRemove = new HashSet<String>(), keyWordsAltRemove = new HashSet<String>();

    String[] splitKeyWords = pergunta.getPalavrasChave().split(";"), splitKeyWordsAlt = palavrasChaveAlt.split(";");

    for (String str : splitKeyWords) {
      keyWords.add(str);
    }
    for (String str : splitKeyWordsAlt) {
      keyWordsAlt.add(str);
    }

    keyWordsRemove = keyWords;
    keyWordsAltRemove = keyWordsAlt;

    keyWordsRemove.removeAll(keyWordsAlt);
    keyWordsAltRemove.removeAll(keyWords);

    for (String str : keyWordsRemove) {
      indInver.delete(str, pergunta.getId());
    }
    for (String str : keyWordsAltRemove) {
      indInver.create(str, pergunta.getId());
    }

    pergunta.setPalavrasChave(palavrasChaveAlt);
  }

  // -----------Funcoes Gerais-----------

  public static int[] VetorIntVariavel(int[] vetor, int valor) {
    int[] NovoVetor = null;
    int tamVetor = vetor.length;
    if (vetor == null || vetor.length <= 1) {
      NovoVetor = new int[1];
      NovoVetor[0] = valor;
    } else {
      NovoVetor = new int[tamVetor + 1];
      NovoVetor[tamVetor] = valor;
    }
    return NovoVetor;
  }

  public static String formatarPalavrasChave(String palavrasChave) {
    palavrasChave = Normalizer.normalize(palavrasChave, Normalizer.Form.NFD);
    palavrasChave = palavrasChave.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

    palavrasChave = palavrasChave.toLowerCase().trim();
    return palavrasChave;
  }

  public static void printPerguntaResumido(Pergunta p) {
    if (p.isAtiva()) {
      System.out.print("\n");
      System.out.println("\n" + p.getPergunta());
      System.out.println("Palavras-chaves: " + p.getPalavrasChave());
      System.out.println("Nota: " + p.getNota() + "\n");
    }
  }

  public static void printPerguntaExibida(Pergunta p) throws Exception {
    menuPadrao();
    System.out.println("PERGUNTAS\n");
    System.out.println("+---------------------------------------------------------------------+");
    System.out.println(p.getPergunta());
    System.out.println("+---------------------------------------------------------------------+");
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
    calendar.setTimeInMillis(p.getCriacao());
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Usuario u = arqPessoas.read(p.getIdUsuario());
    System.out.println("Criada em " + formatter.format(calendar.getTime()) + " por " + u.getNome());

    System.out.println("Palavras chave: " + p.getPalavrasChave());
    System.out.println("Nota: " + p.getNota() + "\n");

    System.out.println("RESPOSTAS");
    System.out.println("---------\n");
  }

  public static void printResposta(Resposta r) throws Exception{
    System.out.println(r.getResposta());
    System.out.print("Respondido em ");

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
    calendar.setTimeInMillis(r.getCriacao());
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    Usuario u = arqPessoas.read(r.getIdUsuario());

    System.out.println(formatter.format(calendar.getTime()) + " por "+ u.getNome());
    System.out.println("Nota: "+r.getNota()+"\n");
  }

  public static void printPergunta(Pergunta p) {
    System.out.print("\n");
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
    calendar.setTimeInMillis(p.getCriacao());
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    System.out.print(formatter.format(calendar.getTime()));
    if (!p.isAtiva())
      System.out.print(" (ARQUIVADA)");
    System.out.println("\n" + p.getPergunta());
    System.out.println("Palavras-chaves: " + p.getPalavrasChave());
    System.out.println("Nota: " + p.getNota());
  }
}
