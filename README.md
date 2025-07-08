# GitHub Code Analyzer

Spring Boot + React kullanılarak geliştirilmiş bir statik kod analiz uygulamasıdır. Kullanıcıdan alınan GitHub reposunu analiz eder, sonuçları veri tabanına kaydeder ve PDF olarak raporlar.

---

## 📁 Proje Yapısı

```
Github/
├── backend/             # Spring Boot uygulaması
│   ├── db/
│   │   └── schema.sql   # PostgreSQL tablo yapısı (veri içermez)
│   └── githubfiles/     # Ana Java backend kodları
├── frontend/            # React + Vite tabanlı kullanıcı arayüzü
```

---

## ⚙️ Backend (Spring Boot)

### 📁 Modüller

- `githubfiles`: Ana Spring Boot uygulaması
- `db/schema.sql`: PostgreSQL tablo şeması (veri içermez)

### 💾 PostgreSQL Ayarları

`application.properties` örneği:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/code_analyzer_db
spring.datasource.username=postgres
spring.datasource.password=***
```

Güvenli ortam değişkeni kullanımı önerilir:

```
spring.datasource.password=${DB_PASSWORD}
```

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

## 🛠 Veritabanı Şeması

Veritabanı şemasını oluşturmak için:

```bash
psql -U postgres -d code_analyzer_db -f backend/db/schema.sql
```

---

## 🚀 Projeyi Çalıştırma

### Backend:

```bash
cd backend/githubfiles
./mvnw spring-boot:run
```

### Frontend:

```bash
cd frontend
npm run dev
```

---