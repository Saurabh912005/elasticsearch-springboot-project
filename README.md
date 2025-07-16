# 🧠 Course Search API – Spring Boot + Elasticsearch

This is a Spring Boot application that integrates with **Elasticsearch 7.17** to index and search educational course data.

It demonstrates:
- ✅ Full-text search with filters (title, description)
- ✅ Sorting and pagination
- ✅ Fuzzy search (typo-tolerant queries)


---

## ⚙️ Tech Stack & Versions

| Component                     | Version     |
|------------------------------|-------------|
| Java                         | 17          |
| Spring Boot                  | 2.7.18      |
| Elasticsearch (Docker)       | 7.17.18     |
| Elasticsearch Java Client    | 7.17.18     |
| Spring Data Elasticsearch    | via Spring Boot |
| JSON API                     | Jakarta 2.1.1 / Glassfish 2.0.1 |
| Build Tool                   | Maven       |

---

## 🚀 How to Run the Project

### 1️⃣ Launch Elasticsearch

Ensure Docker is installed and running.

```bash
docker-compose up -d
```
This launches Elasticsearch on http://localhost:9200.

--- 
##Verify it's running:

```bash
curl http://localhost:9200

Expected Output--
{
  "name": "es",
  "cluster_name": "docker-cluster",
  "version": {
    "number": "7.17.18",
    ...
  }
}
```
---
2️⃣ Build and Run the Spring Boot App
From the terminal:
```bash
./mvnw clean install
./mvnw spring-boot:run
```
---
Sample data is located at:
```bash
src/main/resources/sample-courses.json
```
---
📦 API Endpoints
🔍 GET /api/search
Search for courses using filters, keyword search, sort, and pagination.

1️⃣ Basic keyword search
```bash
curl "http://localhost:8080/api/search?q=math"
```

---
2️⃣ Filter by category and age
```bash

curl "http://localhost:8080/api/search?category=Science&minAge=8&maxAge=12"
```
---
3️⃣ Sorted by price descending
```bash

curl "http://localhost:8080/api/search?q=club&sort=priceDesc"
```

---
✨ Bonus: Fuzzy Search Support (Typo Tolerance)

If a course has:

``` bash
{ "title": "Dinosaurs 101" }
And the user types:
curl "http://localhost:8080/api/search?q=dinors

✅ This still returns Json output like this :
{
  "total": 1,
  "courses": [
    {
      "id": "course-42",
      "title": "Dinosaurs 101",
      ...
    }
  ]
}
```

