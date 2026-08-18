# Simulateur de Crédit avec Moteur de Scoring

Application web full-stack de gestion de demandes de prêt, avec un moteur de scoring automatique et un workflow d'approbation multi-rôles. Développée avec **Spring Boot** (backend) et **Angular** (frontend).

## Aperçu

Un client soumet une demande de prêt (immobilier, auto ou personnel) accompagnée de son profil financier. Le système calcule automatiquement un score de risque selon plusieurs facteurs pondérés (taux d'endettement, stabilité professionnelle, ancienneté, apport personnel), détermine si la demande est approuvée, à analyser manuellement ou rejetée, puis génère un contrat et un tableau d'amortissement au format PDF une fois le prêt approuvé.

Le projet suit une architecture REST sécurisée par JWT, avec trois rôles distincts (Client, Agent de crédit, Manager) et des permissions différenciées à chaque étape du cycle de vie d'une demande.

## Statut du projet

- ✅ **Backend** : complet et fonctionnel (API REST testée)
- 🚧 **Frontend** : en cours de développement

## Fonctionnalités

### Authentification & sécurité

- Inscription / connexion avec JWT
- 3 rôles : Client, Agent de crédit, Manager
- Endpoints protégés par rôle (`@PreAuthorize`)

### Côté Client

- Soumission d'une demande de prêt avec profil financier complet
- Évaluation automatique du score de risque à la soumission
- Consultation de ses propres demandes et de leur statut
- Téléchargement du contrat + tableau d'amortissement (PDF) une fois approuvé

### Côté Agent de crédit / Manager

- Consultation des demandes en attente d'analyse
- Changement de statut d'une demande, avec règles de transition contrôlées (state machine — ex : seul un manager peut valider le décaissement final)
- Historique complet de chaque changement de statut (audit trail)

### Moteur de scoring

Score sur 100 points, calculé selon 4 facteurs pondérés :

| Facteur                    | Poids max |
| -------------------------- | --------- |
| Taux d'endettement         | 40 pts    |
| Situation professionnelle  | 25 pts    |
| Ancienneté professionnelle | 15 pts    |
| Apport personnel           | 20 pts    |

Score ≥ 70 → approbation automatique · 40-69 → analyse manuelle · < 40 → rejet automatique

## Stack technique

**Backend**

- Java 21, Spring Boot
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- MySQL
- Apache PDFBox (génération de contrats PDF)
- Lombok

**Frontend**

- Angular
- TypeScript
- Reactive Forms

## Lancer le projet en local

### Prérequis

- JDK 21
- Node.js (LTS) + Angular CLI
- MySQL Server

### Backend

```bash
cd backend
# Copier application-example.properties en application.properties
# et renseigner vos identifiants MySQL
mvn spring-boot:run
```

Le backend démarre sur `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
ng serve
```

Le frontend démarre sur `http://localhost:4200`.

### Base de données

Créer une base MySQL nommée `credit_scoring_db` avant de lancer le backend — les tables sont créées automatiquement au démarrage (Hibernate `ddl-auto=update`).

## Structure du projet

```
credit-scoring-app/
├── backend/          # API Spring Boot
│   └── src/main/java/com/ranya/backend/
│       ├── model/        # Entités JPA
│       ├── repository/   # Repositories Spring Data
│       ├── security/     # JWT, Spring Security
│       ├── service/      # Logique métier (scoring, workflow, PDF)
│       ├── controller/   # Endpoints REST
│       ├── dto/          # Objets de transfert
│       └── exception/    # Gestion centralisée des erreurs
└── frontend/         # Application Angular
    └── src/app/
        ├── core/         # Services, guards, intercepteurs
        └── features/     # Pages par rôle (client, agent...)
```

## Points techniques

- **Workflow à états contrôlé** : les transitions de statut suivent des règles explicites selon le rôle de l'utilisateur, plutôt qu'un simple champ modifiable librement
- **Audit trail complet** : chaque changement de statut est historisé (qui, quand, ancien/nouveau statut)
- **Séparation DTO / Entité** : les entités JPA ne sont jamais exposées directement dans les réponses API
- **Gestion centralisée des exceptions** (`@RestControllerAdvice`) pour des réponses d'erreur cohérentes

## Captures d'écran

_À venir une fois le frontend terminé._

## Auteur

Ranya — [LinkedIn](www.linkedin.com/in/jaidane-ranya-72a9a0207) · [GitHub](https://github.com/RJAAY)
