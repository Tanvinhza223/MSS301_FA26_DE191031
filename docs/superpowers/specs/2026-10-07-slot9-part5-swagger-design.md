# Slot 9 — Part 5 Swagger/OpenAPI Design

## Scope

`slot9` will be initialized from the completed Part 1–4 service set in
`slot7`: Product Service, Inventory Service, Order Service, and API Gateway.
Part 5 then implements the sixteen documentation TODOs in the provided guides.

## Approach

The source services retain their existing API behavior. Each service gains
Springdoc dependencies, fixed Swagger and OpenAPI document paths, an OpenAPI
metadata bean, and API CORS configuration. The Gateway gains the corresponding
Springdoc configuration, three aggregate document routes, and public access
only for Swagger resources and aggregate documents. Existing API routes remain
JWT-protected.

## Delivery and Verification

The copied Part 1–4 baseline is committed first so `slot9` is self-contained.
Each of DOC-1 through DOC-16 is then committed independently. Service Swagger
integration tests are run after each service group; Gateway tests are run after
the Gateway group, followed by the complete suites when infrastructure permits.

## Boundaries

Only files required by the two Part 5 guides are changed. No unrelated service
refactoring, dependency upgrades, endpoint changes, or security-rule changes
beyond Swagger and aggregate documentation access are included.
