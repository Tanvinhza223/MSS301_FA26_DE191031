# Watch Management Microservices

The system implements watch CRUD through Saga orchestration with database-per-service. It contains an API Gateway, Account service, Brand participant, and Watch orchestrator.

## Run

1. Copy `.env.example` to `.env` and set a strong SQL Server password and JWT secret.
2. Run `docker compose up --build` from this directory.
3. Use the API through `http://localhost:8080`.

Default accounts use password `@1`. `adminsys` has administrator role. Only `GET /api/watches` is public.

## Verify

Run Maven tests in each service, then import `WatchManagement/WatchManagement_Saga.postman_collection.json` into Postman. The collection demonstrates successful Saga execution, validation, compensation after injected failures, authorization, and Saga monitoring.
