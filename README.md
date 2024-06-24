
# Projeto: Aplicativo Android de Geolocalização e Criptografia

Este projeto foi desenvolvido como parte da disciplina de Automação Avançada no curso de Engenharia de Controle e Automação na Universidade Federal de Lavras (UFLA), sob orientação do professor Arthur. O aplicativo Android é desenvolvido em Java e realiza várias funcionalidades relacionadas à geolocalização, criptografia e armazenamento de dados em um banco de dados Firebase. O projeto faz parte de uma série de tarefas destinadas a aprimorar habilidades em Android Studio, manipulação de filas, utilização de threads, e aplicação de conceitos de polimorfismo e criptografia.

## Funcionalidades

### Tarefa 1: Leitura de Dados 
- Leitura periódica da localização do dispositivo usando GPS.
- Exibição das coordenadas atuais em componentes visuais (labels).
- Indicação da localização atual em um mapa.

### Tarefa 2: Inserção na Fila
- Adição de novas regiões em uma fila ao clicar no botão "Adicionar Região".
- Utilização de semáforos para garantir acesso exclusivo à fila.
- Verificação de proximidade de 30 metros antes de adicionar uma nova região.
- Criação de uma biblioteca Android para cálculos de distância.
- Adição de timestamp e código do usuário às regiões.

### Tarefa 3: Criptografia
- Implementação de criptografia para dados armazenados na fila.
- Criptografa os dados de localização para garantir a segurança das informações.
- Conversão de dados para formato JSON antes de armazenamento.

### Tarefa 4: Envio para Banco de Dados
- Envio e recuperação de dados criptografados no Firebase.
- Envia os dados criptografados para o banco de dados Firebase para armazenamento persistente.
- Armazenamento de dados de região no Firebase.

### Tarefa 5: Busca no Banco de Dados
- Realiza buscas no banco de dados Firebase para recuperar informações sobre regiões geográficas.
- Verificação prévia no banco de dados e na fila antes de adicionar novas regiões.

## Instalação

1. Clone o repositório:
    ```bash
    git clone https://github.com/seu-usuario/seu-repositorio.git
    ```
2. Abra o projeto no Android Studio.
3. Configure o Firebase no projeto seguindo as instruções [aqui](https://firebase.google.com/docs/android/setup).

## Uso

### Pré-requisitos
1. **Criar uma Chave API do Google**: 
    - Siga as instruções [aqui](https://developers.google.com/maps/documentation/android-sdk/get-api-key) para obter uma chave API do Google para usar os serviços de mapas.
2. **Criar uma Chave Secreta para Criptografia**: 
    - Gere uma chave secreta para ser usada nos processos de criptografia e descriptografia de dados.
3. **Criar uma Conta no Firebase**:
    - Crie uma conta no Firebase e configure um novo projeto. Adicione o aplicativo Android ao projeto Firebase e obtenha o arquivo `google-services.json`.

### Adicionar uma Região
1. Execute o aplicativo em um dispositivo ou emulador.
2. Clique no botão "Adicionar Região" para incluir a localização atual na fila.
3. A região será adicionada se não houver outra região dentro de um raio de 30 metros.

### Gravar no Firebase
1. Clique no botão "Gravar no Bando de Dados" para armazenar as regiões na fila no Firebase.
2. Os dados serão criptografados e enviados para o Firebase.

## Estrutura de Classes

- `Region`: Classe base para representar uma região.
- `SubRegion`: Subclasse de `Region` que representa uma sub-região.
- `RestrictedRegion`: Subclasse de `Region` que representa uma região restrita.
- `Cryptography`: Classe utilitária para criptografia e descriptografia de dados.
- `RegionVerificationThread`: Classe thread responsável por verificar a proximidade de uma localização atual em relação a regiões pré-definidas, tanto localmente quanto no Firebase.
- `GpsThread`: Classe thread projetada para gerenciar a atualização de localização, para receber atualizações de localização do GPS.
- `FirebaseSaveThread`: Classe thread responsável por gerenciar o processo de salvamento de regiões no Firebase, utilizando um semáforo para garantir que apenas uma thread possa acessar a fila de regiões de cada vez.

