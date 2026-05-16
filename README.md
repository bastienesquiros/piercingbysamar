# Piercing by Samar

E-commerce de bijoux et piercings en titane — Marrakech.

## Stack

| Couche | Techno |
|---|---|
| Frontend | Nuxt 3 · Tailwind CSS · TypeScript |
| Backend | Spring Boot 4 · Java 25 · PostgreSQL · Flyway |
| Paiement | Stripe Checkout |
| Stockage | Cloudflare R2 (prod) · MinIO (dev) |
| Infra | Docker · Nginx · Cloudflare · VPS |

## Lancer en local

### Prérequis
- Java 25, Maven
- Node 22
- Docker (pour PostgreSQL + MinIO)

### Backend

```bash
cd backend
./mvnw spring-boot:run
# Profil dev actif par défaut → application-dev.properties
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Services locaux (Docker)

```bash
# PostgreSQL sur :5432, MinIO sur :9000 / console :9001
docker compose -f docker-compose.dev.yml up -d
```

## Variables d'environnement (prod)

Copier `.env.example` → `.env` et remplir les valeurs.  
Ne jamais committer `.env`.

## Tests

```bash
cd backend && ./mvnw test
```

## Build Docker

```bash
docker build -t piercingbysamar-backend ./backend
docker build -t piercingbysamar-frontend ./frontend
```
