# Projeto Fórum
### Membros:
    - Danniel Henrique Correa Vieira
    - Letícia Americano Lucas
    - Marcos Ani Cury Vinagre Silva

## Quarta Sprint (Metodos e funcoes feitos pra essa entrega):
  ### Entidades/Resposta.java
    - Adição da entidade Resposta 
    
## Diretórios e Arquivos:
### Principal:
    - Main.java: Classe Main, classe principal do projeto ao qual todo o sistema gira em torno.
### Diretório CRUD:
    - CRUD.java: Classe CRUD, no qual faz todo o gerênciamento de acesso aos bancos de dados.
    - Registro.java: Interface que apresenta os métodos que os objetos a serem incluídos no CRUD devem conter.
### Diretório arvoreBPlus:
    - ArvoreBMais_ChaveComposta_Int_Int.java: Contém o par de chaves idUsuario e idPergunta, e facilita na busca por perguntas de um mesmo usuário.
### Diretório dados: 
    - arvore.db: Contém os dados gerados pela arvore e são armazenados em memória secundária.
    - email_hash_c.db: Contém os dados gerados que foram armazenados em memória secundária sobre a tabela hash em cesto do email.
    - email_hash_d.db: Contém os dados gerados que foram armazenados em memória secundária sobre a tabela hash em diretório do email.
    - pergunta_hash_d.db: Contém os dados gerados que foram armazenados em memória secundária sobre a tabela hash em diretório das perguntas.
    - perguntas.db: Contém os dados gerados pelo hash sobre perguntas e são armazenados em memória secundária.
    - usuario_hash_c: Contém os dados gerados que foram armazenados em memória secundária sobre a tabela hash em cesto do usuario.
    - usuario_hash_d: Contém os dados gerados que foram armazenados em memória secundária sobre a tabela hash em diretório do usuario.
    - usuarios.db: Contém os dados gerados pelo hash sobre usuarios e são armazenados em memória secundária.
    - listainvertida_blocos.db: Armazenamento dos blocos na lista invertida.
    - listainvertida_dict.db: Armazenamento dos dicionario na lista invertida.
### Diretório Entidades: 
    - Pergunta.java: Entidade Pergunta utilizada no projeto.
    - Usuario.java: Entidade Usuario utilizada no projeto.
### Diretório HashExtensivel:
    - HashExtensivel.java: Arquivo utilizado para criação da tabela Hash Extensivel.
    - RegistroHashExtensivel.java: Interface que apresenta os métodos que os objetos a serem incluídos na tabela hash extensível devem conter.
### Diretório pvc:
    - pcvEmail.java: Esta classe representa o par chave valor de um email, no caso, o email e o idUsuario referente ao email.
    - pcvPergunta.java: Esta classe representa o par chave valor de uma pergunta, no caso, o idUsuario e o idPergunta.
    - pcvUsuario.java: Esta classe representa o par chave valor de um usuario, no caso, o idUsuario e o endereço no arquivo.
### Diretório listaInvertida:
    - ListaInvertida.java: Classe da criação da lista invertida para acrescentar ou deletar itens da lista, diferente da padrão a lista invertida inverte a hierarquia da informação, sendo assim, ao invés de uma lista de documentos contendo termos, é obtida uma lista de termos, referenciando documentos.

## Log de Tarefas passadas
### Terceira Sprint (Metodos e funcoes feitos pra essa entrega):
  #### Entidades/Pergunta.java
    - Adicionado atributo palavrasChave. Feito alterações necessárias nos construtores além dos getters e setters de palavrasChave.
    - O setter de palavrasChave já possui um formatador para padronizar as palavras-chave.
  
  #### listaInvertida e listaInvertida/ListaInvertida.java
    - Adicionado classe de lista invertida para indexar palavras-chave com os ids da perguntas respectivas.  

  #### Main.java
    - public static void menuConsultaPerguntas(): Apresenta o menu de opções de consulta.
    - public static void consultaPerguntas(Scanner sc): Solicita do usuário as palavras-chave, apresenta os resultados que contém as palavras-chave, e permite o usuário selecionar uma pergunta entre os resultados.
    - public static ArrayList<Pergunta> buscarPerguntas(String palavrasChave): Executa o processo de selecionar apenas as perguntas com palavras-chave em comum através de uma lista invertida de palavras chave e id.

    - Metódo de criarIndInver e alterarIndInver: Contêm o detalhamento para criar um indice para cada palavra-chave e alterar caso necessário.

    - int[] VetorIntVariavel(int[] vetor, int valor) - MAIN: Função criada para adicionar mais um de tamanho a um vetor de int. 
    - void desarquivamentoPerguntas(Scanner sc) - MAIN: Função criada para mostrar perguntas arquivadas e assim desarquiva-las.
    - int[] listaPerguntas(Scanner sc, boolean ativa) - MAIN (alteração): agora a função pega a lista das perguntas ou arquivadas ou desarquivadas.
 