# GitHub Code Analyzer

Spring Boot + React kullanılarak geliştirilmiş bir statik kod analiz uygulamasıdır. Kullanıcıdan alınan GitHub reposunu analiz eder, sonuçları veri tabanına kaydeder ve PDF olarak raporlar.

---

## 🧰 Kullanılan Teknolojiler

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-success?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?logo=postgresql)
![React](https://img.shields.io/badge/React-19.1.0-61DAFB?logo=react)
![Vite](https://img.shields.io/badge/Vite-7.0.0-purple?logo=vite)
![OpenAI](https://img.shields.io/badge/OpenAI-GPT4-black?logo=openai)
![Ollama](https://img.shields.io/badge/Ollama-local--LLM-green?logo=data)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![Springdoc](https://img.shields.io/badge/Springdoc-OpenAPI-blue?logo=openapiinitiative)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT--based-green?logo=spring)
![ModelMapper](https://img.shields.io/badge/ModelMapper-Entity--DTO-blue)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-Hibernate-red?logo=hibernate)
![Lombok](https://img.shields.io/badge/Lombok-Auto--Code-yellow?logo=lombok)
![OpenPDF](https://img.shields.io/badge/OpenPDF-PDF--Reports-lightgrey)
![Spring AI](https://img.shields.io/badge/Spring%20AI-OpenAI%2FOllama-blueviolet?logo=openai)
![Global Exception Handler](https://img.shields.io/badge/Error%20Handling-Custom--Exceptions-critical)



---

---

## 📁 Proje Yapısı

```
Github/
├── backend/             # Spring Boot uygulaması
│   └── githubfiles/     # Ana Java backend kodları
├── frontend/            # React + Vite tabanlı kullanıcı arayüzü
```

---

## ⚙️ Backend (Spring Boot)

### 📁 Modüller

- `githubfiles`: Ana Spring Boot uygulaması

### 

`application.properties` örneği:

```properties
spring.application.name=githubfiles

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/code_analyzer_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# AI varsayılan sağlayıcı ve model
ai.default.provider=ollama
ai.default.model=gemma3:4b

# OpenAI Ayarları
spring.ai.openai.api-key=${OPENAI_API_KEY}
#spring.ai.openai.chat.model=gpt-4o
spring.ai.openai.chat.temperature=0.2
spring.ai.openai.chat.top-p=1.0
spring.ai.openai.chat.timeout=60s

# Ollama Ayarları
spring.ai.ollama.base-url=http://localhost:11434
#spring.ai.ollama.chat.options.model=gemma3:4b
spring.ai.ollama.chat.options.temperature=0.2
spring.ai.ollama.chat.options.top-p=1.0
spring.ai.ollama.chat.timeout=60s

# JWT
jwt.secret=${JWT_SECRET}

### 🧠 Yapay Zeka Desteği

Uygulama, yerel çalışan modellerle (Ollama) veya OpenAI API ile analiz gerçekleştirebilir.

---

## 💻 Frontend (React)

- Vite + React ile geliştirilmiştir
- REST API üzerinden backend ile haberleşir
- Kullanıcı analiz başlatabilir ve sonuçları PDF olarak indirebilir

### Kurulum:

```bash
cd frontend
npm install
npm run dev
```

---

## 🚀 Projeyi Çalıştırma

Uygulama Docker Compose ile tek komutla ayağa kaldırılabilir. PostgreSQL, Spring Boot (backend), Ollama ve React (frontend) container olarak başlatılır.

---

### 1. Ortam Değişkenlerini Ayarla

📝 Proje kök dizininde aşağıdaki komutla `.env` dosyasını oluştur:

```bash
cp .env.example .env

✍️ Daha sonra .env dosyasındaki değişkenleri kendi bilgilerine göre doldur:

DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-secret-key
OPENAI_API_KEY=your-openai-key
GITHUB_TOKEN=your-github-token
```

---

### 2.🐳 Docker Compose ile Başlat:

Projenin kök dizininde:

```bash
docker-compose up --build
```

---

### 3. Servislere Erişim

| Servis         | URL                          |
|----------------|------------------------------|
| Backend API    | http://localhost:8080        |
| Frontend UI    | http://localhost:5173        |
| Ollama (LLM)   | http://localhost:11434       |
| PostgreSQL     | localhost:5432 (iç bağlantı) |

---

### 4. Temiz Başlatmak İstersen

Her şeyi sıfırlayıp yeniden başlatmak için:

```bash
docker-compose down -v
docker-compose up --build
```
---

## 🔐 Login Akışı

![Login Flow](images/login_diagram.png)

---

## 🔁 Access Token Yenileme Akışı

![Access Token Refresh](images/access_token_refresh_diagram.png)

---

## 📊 Analyze API İşleyişi

![Analyze Flow](images/analyze_diagram.png)

---

## 📥 Repository Import Akışı

![Import Repository](images/import_repository_diagram.png)

---

## 🔄 Repository Update Akışı

![Update Repository](images/update_repository_diagram.png)

---

## 📦 Repository Değişiklik Kontrolü

![Check Repo Changes](images/check_all_repositories_has_changed_diagram.png)

---

## 🔓 Logout Akışı

![Logout Flow](images/logout_diagram.png)

---