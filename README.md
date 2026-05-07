# Projeto Java com Spring Boot, Docker e SQL Server

## Objetivo

Este projeto tem como objetivo ensinar os conceitos básicos de desenvolvimento backend utilizando:

* Java
* Spring Boot
* Docker
* SQL Server
* Docker Compose
* Persistência de dados
* APIs REST

O conteúdo foi preparado para alunos do 5º período do curso de Engenharia da Computação.

---

# Tecnologias Utilizadas

| Tecnologia      | Finalidade                    |
| --------------- | ----------------------------- |
| Java 21         | Linguagem principal           |
| Spring Boot     | Framework backend             |
| Maven           | Gerenciamento de dependências |
| Docker          | Containerização               |
| Docker Compose  | Orquestração dos containers   |
| SQL Server      | Banco de dados relacional     |
| Spring Data JPA | Persistência de dados         |
| Hibernate       | ORM                           |

---

# Estrutura do Projeto

```text
projeto-springboot/
│
├── src/
│   ├── main/
│      ├── java/
│      │   └── br/edu/ifsuldeminas/app/
│      │       ├── controller/
│      │       ├── service/
│      │       ├── repository/
│      │       ├── model/
│      │       └── AppApplication.java
│      │
│      └── resources/
│          └── application.properties
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# Conceitos Fundamentais

## O que é Spring Boot?

O Spring Boot é um framework Java que facilita a criação de aplicações web e APIs REST.

Principais vantagens:

* Configuração simplificada
* Servidor embutido
* Integração com banco de dados
* Injeção de dependência
* Facilidade de testes

---

## O que é Docker?

Docker é uma plataforma de containerização.

Um container é um ambiente isolado contendo:

* aplicação
* bibliotecas
* dependências
* configurações

Benefícios:

* facilidade de deploy
* padronização do ambiente
* execução em qualquer sistema operacional
* isolamento da aplicação

---

## O que é Docker Compose?

Docker Compose permite executar múltiplos containers simultaneamente.

Neste projeto serão utilizados:

* container da aplicação Spring Boot
* container do SQL Server

---

# Configuração do Projeto Spring Boot

## Dependências Maven

Exemplo de dependências principais:

```xml
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webmvc</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<scope>provided</scope>
		</dependency>

    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>com.microsoft.sqlserver</groupId>
			<artifactId>mssql-jdbc</artifactId>
			<scope>runtime</scope>
		</dependency>

</dependencies>
```

---

# Configuração do Banco de Dados

## application.properties

```properties
spring.datasource.url=jdbc:sqlserver://sqlserver:1433;databaseName=curso_db;encrypt=false;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=Sql1234@%

spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080
```

---

# Exemplo de Entidade

## Classe Aluno

```java
package br.com.exemplo.model;

import jakarta.persistence.*;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String curso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}
```

---

# Repository

```java
package br.com.exemplo.repository;

import br.com.exemplo.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
```

---

# Controller REST

```java
package br.com.exemplo.controller;

import br.com.exemplo.model.Aluno;
import br.com.exemplo.repository.AlunoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoRepository repository;

    public AlunoController(AlunoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Aluno> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Aluno salvar(@RequestBody Aluno aluno) {
        return repository.save(aluno);
    }
}
```

---

# Dockerfile da Aplicação

```dockerfile
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

# Docker Compose

## docker-compose.yml

```yaml
version: '3.9'

services:

  sqlserver:
    image: mcr.microsoft.com/mssql/server:2022-latest
    container_name: sqlserver
    environment:
      ACCEPT_EULA: "Y"
      SA_PASSWORD: "Sql1234@%"
    ports:
      - "1433:1433"
    volumes:
      - sql_data:/var/opt/mssql

  app:
    build: .
    container_name: springboot-app
    ports:
      - "8080:8080"
    depends_on:
      - sqlserver

volumes:
  sql_data:
```

---

# Construindo a Aplicação

## Gerar o JAR

```bash
./mvnw clean package
```

ou

```bash
mvn clean package
```

---

# Executando os Containers

```bash
docker compose up --build
```

---

# Verificando os Containers

```bash
docker ps
```

---

# Testando a API

## Endpoint GET

```http
GET http://localhost:8080/alunos
```

---

## Endpoint POST

```http
POST http://localhost:8080/alunos
Content-Type: application/json

{
    "nome": "Maria",
    "curso": "Engenharia da Computação"
}
```

---

# Conceitos Importantes para os Alunos

## Persistência de Dados

O SQL Server armazenará os dados da aplicação.

O Spring Data JPA será responsável pela comunicação entre Java e banco de dados.

---

## ORM

ORM (Object Relational Mapping) permite mapear:

* classes Java → tabelas
* atributos → colunas
* objetos → registros

Neste projeto o Hibernate realiza essa função.

---

## API REST

A aplicação disponibiliza endpoints HTTP para comunicação.

Principais métodos:

| Método | Função    |
| ------ | --------- |
| GET    | Consultar |
| POST   | Inserir   |
| PUT    | Atualizar |
| DELETE | Remover   |

---

# Fluxo de Funcionamento

```text
Cliente HTTP
      ↓
Controller REST
      ↓
Service
      ↓
Repository
      ↓
SQL Server
```

---

# Exercícios Propostos

## Exercício 1

Criar endpoints para:

* buscar aluno por ID
* remover aluno
* atualizar aluno

---

## Exercício 2

Criar uma entidade:

```text
Professor
```

com:

* id
* nome
* disciplina
* salário

---

## Exercício 3

Implementar relacionamento:

```text
Aluno → Curso
```

---

## Exercício 4

Adicionar validações usando:

```text
jakarta.validation
```

---

## Exercício 5

Criar:

* DTOs
* camada Service
* tratamento global de exceções

---

# Comandos Docker Importantes

## Parar containers

```bash
docker compose down
```

---

## Ver logs

```bash
docker logs springboot-app
```

---

## Entrar no container

```bash
docker exec -it springboot-app bash
```

---

# Possíveis Problemas

## Porta ocupada

Erro:

```text
Bind for 0.0.0.0:8080 failed
```

Solução:

* alterar a porta
* encerrar aplicação que utiliza a porta

---

## Senha fraca do SQL Server

O SQL Server exige:

* letras maiúsculas
* minúsculas
* números
* caracteres especiais

---

## Container encerrando imediatamente

Verificar:

```bash
docker logs nome_container
```

---

# Boas Práticas

* utilizar variáveis de ambiente
* não salvar senhas no código
* separar camadas da aplicação
* utilizar DTOs
* tratar exceções
* documentar endpoints
* utilizar Git

---

# Próximos Passos

Após concluir este projeto, recomenda-se estudar:

* Spring Security
* JWT
* Swagger/OpenAPI
* Testes unitários
* CI/CD
* Kubernetes
* Microsserviços
* RabbitMQ
* Redis

---

# Referências

## Documentações Oficiais

* Spring Boot
* Docker
* Docker Compose
* SQL Server
* Spring Data JPA
* Hibernate

---

# Conclusão

Este projeto apresenta uma arquitetura moderna de desenvolvimento backend utilizando Java, Spring Boot, containers Docker e SQL Server.

Os conceitos abordados são amplamente utilizados no mercado de desenvolvimento de software e servem como base para aplicações corporativas modernas.

