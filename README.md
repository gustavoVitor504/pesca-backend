# 🎣 Pesca Backend

API REST para e-commerce de equipamentos de pesca, desenvolvida com **Spring Boot 3**, **PostgreSQL (Supabase)** e autenticação via **JWT**.

---

## 🚀 Tecnologias

- Java 17
- Spring Boot 3.2
- Spring Security + JWT (jjwt 0.11.5)
- Spring Data JPA + Hibernate
- PostgreSQL (Supabase)
- Lombok
- Maven

---

## 📁 Estrutura do Projeto

```
src/main/java/com/example/pesca/
├── config/
│   └── SecurityConfig.java          # CORS, filtros, autenticação
├── controller/
│   ├── AuthController.java          # POST /api/auth/register | login
│   ├── ProductController.java       # CRUD /api/products
│   └── OrderController.java         # POST/GET /api/orders
├── dto/
│   ├── AuthDTO.java                 # RegisterRequest, LoginRequest, AuthResponse
│   └── OrderDTO.java                # CreateRequest, OrderResponse, ItemResponse
├── model/
│   ├── User.java
│   ├── Product.java
│   ├── Order.java
│   └── OrderItem.java
├── repository/
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── security/
│   ├── JwtService.java              # Geração e validação de tokens
│   ├── JwtAuthFilter.java           # Filtro de autenticação por request
│   └── UserDetailsServiceImpl.java  # Carrega usuário do banco
└── service/
    ├── AuthService.java
    ├── ProductService.java
    └── OrderService.java
```

---

## ⚙️ Configuração Local

### Pré-requisitos

- Java 17+
- Maven
- Conta no [Supabase](https://supabase.com)

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/pesca-backend.git
cd pesca-backend
```

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e preencha com seus valores:

```bash
cp .env.example .env
```

Edite o `.env`:

```env
JWT_SECRET=sua_chave_gerada_aqui
PASSWORD=sua_senha_do_supabase
```

Para gerar uma chave JWT segura:

```bash
openssl rand -base64 64
```

### 3. Configure o Supabase

1. Acesse [supabase.com](https://supabase.com) e crie um projeto
2. Vá em **Connect → JDBC → Session Pooler**
3. Copie a URL e substitua no `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?user=postgres.SEUREF&sslmode=require
spring.datasource.username=postgres.SEUREF
spring.datasource.password=${PASSWORD}
```

### 4. Execute o projeto

```bash
./mvnw spring-boot:run
```

O Spring criará as tabelas automaticamente no Supabase via `ddl-auto=update`.

---

## 📡 Endpoints

### Auth — público

| Método | URL | Body |
|--------|-----|------|
| POST | `/api/auth/register` | `{ name, email, password }` |
| POST | `/api/auth/login` | `{ email, password }` |

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "name": "Gustavo",
  "email": "gustavo@email.com",
  "role": "USER"
}
```

### Produtos — público

| Método | URL | Params |
|--------|-----|--------|
| GET | `/api/products` | `?category=Varas` ou `?search=shimano` |
| GET | `/api/products/{id}` | — |
| POST | `/api/products` | body Product |
| PUT | `/api/products/{id}` | body Product |
| DELETE | `/api/products/{id}` | — |

### Pedidos — requer JWT

| Método | URL | Header |
|--------|-----|--------|
| POST | `/api/orders` | `Authorization: Bearer TOKEN` |
| GET | `/api/orders` | `Authorization: Bearer TOKEN` |

**Body POST /api/orders:**
```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ]
}
```

---

## 🚢 Deploy no Railway

1. Suba o projeto no GitHub
2. Acesse [railway.app](https://railway.app) → **New Project → Deploy from GitHub**
3. Selecione o repositório
4. Vá em **Variables** e adicione:

| Variável | Valor |
|----------|-------|
| `SPRING_DATASOURCE_URL` | URL JDBC do Supabase (Session Pooler) |
| `SPRING_DATASOURCE_USERNAME` | `postgres.SEUREF` |
| `PASSWORD` | senha do Supabase |
| `JWT_SECRET` | chave gerada com openssl |
| `CORS_ALLOWED_ORIGINS` | `https://seu-frontend.vercel.app` |

5. Railway detecta o `pom.xml` automaticamente e faz o build
6. Copie a URL pública gerada (ex: `https://pesca-backend.up.railway.app`)

---

## 🔒 Segurança

- Senhas armazenadas com **BCrypt**
- Autenticação **stateless** via JWT (sem sessão no servidor)
- CORS configurado por variável de ambiente
- `.env` nunca versionado (ver `.gitignore`)

---

## 🌱 Variáveis de Ambiente

| Variável | Descrição |
|----------|-----------|
| `PASSWORD` | Senha do banco PostgreSQL (Supabase) |
| `JWT_SECRET` | Chave secreta para assinar tokens JWT (mín. 256 bits) |

---

## 📝 Licença

MIT
