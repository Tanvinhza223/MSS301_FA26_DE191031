# Saga Design for Watch Management

MSWatch is the orchestrator and owns `Watches`, `WatchCategories`, `SagaStates`, and `SagaLogs`. MSBrand is the participant and owns `WatchBrands` and `BrandWatches`. Every forward step commits as a local transaction before the next service is called.

## Create watch

1. `CREATE_WATCH` inserts the watch in DBWatch. Compensation: `DELETE_WATCH`.
2. `ASSIGN_BRAND` writes the brand link in DBBrand. Compensation: `UNASSIGN_BRAND`.

## Update watch

1. `UPDATE_WATCH` stores the new watch values. Compensation: `RESTORE_WATCH` from the captured snapshot.
2. `ASSIGN_BRAND` moves the brand link. Compensation: `ASSIGN_BRAND_BACK`.

## Delete watch

1. `UNASSIGN_BRAND` removes the brand link. Compensation: `ASSIGN_BRAND_BACK`.
2. `DELETE_WATCH` deletes the watch and is the pivot. Fault injection after this point is logged and ignored because the original identity cannot be restored safely.

Compensations run in reverse order and retry up to three times. Assign and unassign operations are idempotent. A Saga that exhausts compensation retries remains `COMPENSATING` for manual recovery.

