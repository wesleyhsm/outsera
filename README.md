# outsera

# Golden Raspberry Awards API

API RESTful para consulta de estatísticas dos indicados e vencedores da categoria Pior Filme do Golden Raspberry Awards.

## Pré-requisitos
* **Java 25** instalado.
* **Maven 3.9+** instalado.

## Como Executar a Aplicação
1. Clone este repositório.
2. Certifique-se de que o arquivo `movielist.csv` está na pasta `src/main/resources/`.
3. Na raiz do projeto, execute o comando:
   ```bash
   mvn spring-boot:run
   ```
4. A API estará acessível em `http://localhost:8081/api/movies/producer-intervals`.

## Como Executar os Testes de Integração
Na raiz do projeto, execute:
```bash
mvn test
```
