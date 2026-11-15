---
name: audit-plan-execution
description: Use before you push a branch or open or update a merge request, before you hand work to another stage, reviewer or person, and before you declare a task, ticket or plan done — whether or not anyone has told you the work is finished. Also use when the user says they have finished implementing a plan, completed a feature or wrapped up a task, or asks for a review, audit, post-implementation check or gap analysis, even without the word "audit". Checks the work against its plan or spec for skipped steps, security issues, cross-tenant leaks in lookups keyed on phone numbers, emails, protocol or document numbers, missing tests, subtle bugs (concurrency, context handling, races), function design, and parallel-execution safety.
---

# Audit Plan Execution

A self-review pass that runs after an implementation is claimed complete. The goal is to catch the things that slip through when you're deep in execution: silent gaps against the plan, security oversights, missed tests, subtle concurrency bugs, and function-design drift.

## When to run this

Run this audit when:
- You've just finished implementing a plan, spec, or multi-step task
- The user signals completion ("done", "finished", "that's it", "ready for review")
- The user explicitly asks for a review, audit, or gap analysis
- You're about to hand work off and want to avoid "we forgot X" a day later

The audit is cheap compared to the cost of shipping something broken or incomplete. Err toward running it.

## How to run the audit

Work through the checklist below in order. Don't skim — each item catches a different class of problem, and the reason you're doing this is because implementation-mode attention isn't the same as review-mode attention. Approach the code as if someone else wrote it.

For each finding: **fix it immediately in the code**, then record what you found and what you did in the final report. Don't batch fixes for later — the point of this skill is to leave the work in a better state before reporting.

Only skip auto-fixing when:
- The fix requires a design decision the user should make (e.g., "should this be eventually consistent or strongly consistent?")
- The fix would meaningfully expand scope beyond the original plan
- You're uncertain whether it's actually a bug

In those cases, flag it clearly in the report as "needs user input" rather than silently fixing or silently skipping.

### A clean audit is a valid outcome

The audit's job is to hunt — every checklist section gets full effort. But hunting hard and finding nothing is a legitimate result, not a failed audit. When the search comes up empty, the deliverable is the verdict sentence plus the Test certification block, and that is the whole report.

Never manufacture work to justify the pass: no style rewrites of working code, no speculative hardening without a demonstrated defect, no renaming or restructuring that no checklist item demanded. A change without a real finding behind it degrades good code and pollutes the diff — the audit's value is measured by the accuracy of its verdict, not by the number of edits it produced. Feeling pressure to "show something" for the audit is itself a red flag: report clean and stop.

### Gate every bug fix on evidence

A "probable bug" — whether flagged by a reviewer, a linter, or your own reading of the code — is a hypothesis, not a finding. Plausible-looking bug reports are routinely wrong, and an unverified "fix" degrades working code (the classic: adding a lock acquisition inside a helper whose caller already holds the non-reentrant lock — instant deadlock). Before changing code to fix a suspected bug:

1. **Check the spec first.** The "wrong" behavior may be the specified behavior. If the plan, spec, or design notes define the observed behavior as intended, the report is refuted — record the refutation with its evidence, change nothing.
2. **Reproduce it.** Demonstrate the defect before touching the code: a failing test, a reproduction script, a race-detector run, or a traced execution showing observably wrong behavior against the spec. When the defect is unreachable on your platform (a 32-bit-only alignment panic, an arch-specific overflow), a conclusive static proof citing the documented language/runtime guarantee counts as evidence — but prefer a runtime reproduction whenever one is possible.
3. **Fix, then prove no degrade.** After the fix, the reproduction passes and the previously green tests still pass (section 7 certifies this at the end on the full module set).

No reproduction and no proof → do not patch. Report it under "Still needs attention" as suspected-but-unconfirmed. When you refute a claim someone else made, keep the disproof evidence in the report — an unrecorded refutation gets re-raised on the next review pass.

### Governing principle: production drives shape, tests never do

This constrains how you *resolve* every finding below, not just one category. When a finding turns on "should this production code exist / stay / change / stay this shape?", the answer comes from what production needs — never from what the tests happen to do. Tests are downstream: they adapt to correct production code, not the reverse.

- **Never dismiss a finding because tests depend on the current shape.** "Keep it, the tests use it / mock it / need it exported / would break otherwise" is not a reason — it inverts the dependency. Fix the production code, then update the tests to whatever the corrected code needs (a setter, a helper, a narrower seam, a fixed input).
- **The one legitimate feedback direction:** a test drives a production change only by *revealing a real defect* — a failing-when-violated assertion exposing wrong behavior. Then production changes to fix the behavior, still production-driven. Convenience is not a defect.
- **Smells this catches** (all are production problems to remove, tests updated after): a parameter/field only tests ever supply a real value for (production passes `nil`/const/zero); visibility widened (exported, `public`, package-leaked) only so a test can reach it; a "test mode" branch, flag, or hook compiled into production; a looser type, nullable field, or weaker validation retained only because test inputs exercise the loose path; dead code kept "because a test covers it" (the test covers dead code — delete both); an abstraction, interface, or indirection kept because a test mocks it; a refactor/simplification skipped because "it would break tests".

When you catch yourself justifying a production shape by pointing at a test, stop — that is the tell, and the finding is real.

## The audit checklist

### 1. Plan vs. execution gaps

Re-read the original plan, spec, or request. For each planned step or requirement, verify it was actually implemented. Look for:
- Steps that were skipped entirely
- Steps that were implemented differently than planned (may be fine, but flag it)
- Requirements mentioned in passing that didn't make it into code
- Edge cases the plan called out that aren't handled

If the plan evolved mid-implementation, that's fine — but the deviation should be intentional, not accidental.

### 2. Security

Walk through the changes with an attacker's mindset:
- **Authorization**: Are permission checks present at the right layer? Are they granular enough? Is authorization checked on every path, or only the happy path?
- **Tenant scoping**: Is every lookup keyed on a globally ambiguous identifier scoped to the company/tenant in the same query? Mandatory sweep in 2a below.
- **Input validation**: Is untrusted input validated before it reaches sensitive operations (DB queries, filesystem, shell, serialization)?
- **Execution context**: Is code running with the right privileges? Are service accounts / tokens / credentials scoped appropriately?
- **Attack vectors**: Injection (SQL, command, template), SSRF, path traversal, deserialization, timing attacks, IDOR, privilege escalation. Consider which apply to the change.
- **Secrets**: No credentials, tokens, or keys committed. No sensitive data in logs.

### 2a. Multi-tenant identifier scoping (mandatory sweep)

A **globally ambiguous identifier** is one that is unique inside a company but not inside the deployment: phone number, email address, protocol / ticket / order number, document number (CPF, CNPJ, tax id, SSN), external or vendor id, room or extension number. The platform runs many companies per install, so a query keyed on one of these reaches every other company's rows by default. This is the defect class behind recurring production bugs in the user's multi-tenant projects, where phone and protocol lookups crossed company boundaries.

**Run this sweep once per lookup, match, dedup, upsert, or cache key that the diff added or changed.** For each one, write the line:

```
<function> @ <file>:<line> — keyed on <identifier>; scope <tenant column/field> enters at <exact expression>
```

These lines are working notes, not report output — a scoped lookup produces zero report lines. If you cannot complete the last clause with an expression that sits **in the same query, match condition, or key** as the identifier, that is a finding: fix it and record it. Do not leave the clause blank, do not soften it to "looks scoped", and do not drop a lookup from the sweep because it was already there before the diff — a changed caller makes the reach of an existing lookup part of this change set.

Where the scope has to live:

- **SQL / ORM**: in the same `WHERE` as the identifier (`WHERE company_id = $1 AND phone = $2`), not in a later filter, not in a view the caller "should" use.
- **Unique constraints and upserts**: the unique index and the `ON CONFLICT` / merge key must be `(company_id, identifier)`. An index on the identifier alone silently collapses two companies' distinct records into one.
- **Cache and idempotency keys**: a cache key is a lookup key. `phone` alone as a key serves one company's session to another; the key must be `companyID + ":" + phone` or equivalent.
- **In-memory match / dedup**: `a.Phone == b.Phone` must be `a.CompanyID == b.CompanyID && a.Phone == b.Phone`. Same for map keys, `groupBy`, and set membership.
- **Search, list, export, count, and aggregate paths**: each is its own query. Scoping the detail path and not the export path is still a leak.

**Where the scope value comes from.** The tenant id must be derived from the authenticated principal — session, token claim, or request context. A tenant id read from a request parameter, body field, header, or hostname (`r.URL.Query().Get("company_id")` and friends) is attacker-chosen, so scoping by it is not authorization; it is a leak with an audit trail. If such a value is accepted, validate it against the principal before any query uses it, and make the failure path reject rather than fall back to a default company.

**Filtering results is not authorization — same defect class.** Fetching by the ambiguous identifier and then hiding or dropping rows that belong to another company never makes the authorization decision; it only trims the presentation. Every path that skips the trim leaks: the not-found branch, the error branch, `count`, the cached copy, the metrics label, the log line naming the other tenant's customer, and the next caller of the same store method that forgets the filter. Treat "we filter it afterwards" as an unscoped query plus a workaround, and fix the query.

A store method reachable from one scoped caller and one unscoped caller is a finding against the store method, not against the unscoped caller alone.

#### This is evidence, not a hypothesis

The "Gate every bug fix on evidence" rule above is not a licence to withhold a scoping finding. For scoping, **the query text is the evidence**: reading `WHERE phone = $1` and seeing no tenant predicate in that statement is a conclusive static observation, exactly the kind that section admits. Report it as a defect with the function name, the file, and the quoted query — not as an area of concern.

In baseline testing an auditor got within one step of this finding and then discarded it, writing: *"That is a hypothesis about where to look, not a finding; I have no evidence either way."* That is the failure this section exists to prevent. The instinct was right; the downgrade to a non-finding is what lets the bug ship. Name the lookup, quote the query, call it a defect.

The reverse loophole is closed too: this section does not authorize inventing a scoping finding. When the identifier and the tenant scope are in the same query, key, or match condition, the lookup is correct — say nothing about it and move on. The "clean audit is a valid outcome" allowance covers hunting hard and finding nothing; it does not cover never hunting, and it does not cover manufacturing a scoping finding to prove the sweep ran.

#### Rationalization table

| Excuse | Reality |
|---|---|
| "That is a hypothesis about where to look, not a finding" | The query is on screen. A missing tenant predicate is observed, not suspected. Findings say what is wrong, where. |
| "The caller already has the company in context and passes it down" | Then adding it to the query costs one line. Scope enforced by convention is scope not enforced — the next caller will not pass it. |
| "Phone numbers / emails are unique in practice" | Unique per company, not per deployment. Shared switchboards, one customer buying from two of your clients, and the same test number seeded in every tenant are all routine. |
| "We filter by company after the fetch" | The authorization decision was never made. Every path that skips the filter — error, not-found, count, cache, log — leaks. |
| "The endpoint takes company_id, so it is scoped" | Client-supplied scope is attacker-supplied scope. Derive it from the authenticated principal or validate it against one. |
| "It's an internal endpoint, only our own workers hit it" | Internal callers are the ones that pass the wrong company id. Network position is not a tenant boundary. |
| "This deployment only serves one company today" | Deployments serve multiple companies per install, and the second one arrives without a code review. |
| "The unique index would have caught a collision" | A unique index on the identifier alone *is* the bug — it merges two companies' distinct records. |
| "Adding company_id would break the existing tests" | Production drives shape (see the Governing principle). Fix the query, update the tests. |

#### Red flags — stop and write the finding

- A `WHERE`, key, map lookup, or comparison whose only identifier is a phone, email, protocol, document, or external id
- `company_id` / `tenant_id` read from query string, body, header, or hostname
- A tenant check that happens *after* the fetch, in the handler, or in the template
- A cache key, idempotency key, or dedup key that omits the tenant
- A store method with one scoped caller and one unscoped caller
- Wording in your own draft report like "would be worth checking", "natural IDOR surface", "hypothesis about where to look", or "no evidence either way" about a lookup you have read

Each of these means: name the function and file, quote the key or query, and file it as a defect under `### Findings and fixes`.

### 3. Test coverage

For every code path that was added or changed:
- Is there at least one test that exercises it?
- Are the error paths tested, not just the happy path?
- Are edge cases (empty input, nil/null, boundary values, concurrent access) covered?
- For every lookup in the 2a sweep: is there a test that seeds the **same** identifier under two companies and asserts the other company's row is never returned? A scoping fix without that test is one refactor away from regressing — write it.
- Do the tests actually assert meaningful behavior, or do they just check that the code runs without panicking?

If tests are missing, write them. If tests exist but are weak, strengthen them.

### 4. Subtle bugs

This is the category that bites in production. Look specifically for:
- **Concurrency**: Shared mutable state without synchronization, goroutine/thread leaks, unbounded channels/queues, deadlock risk, races on initialization
- **Context / cancellation**: Using a background or root context where a scoped one should propagate (e.g., `context.Background()` in a request handler). Ignored cancellation signals. Timeouts missing from external calls.
- **Error handling**: Swallowed errors, errors logged but not returned, errors returned but not wrapped with context, panic where an error should be returned
- **Resource lifecycle**: Leaked connections, files, locks, transactions. Missing cleanup on error paths.
- **Initialization & wiring order**: In composition roots, DI containers, bootstrap sequences, and constructors, verify every dependency is created *before* the code that consumes it. Trace the actual execution order in the source — never assume declaration order, file order, or "it's all in the same function" equals init order. A consumer that runs before its dependency is built receives a zero/nil value.
- **Silent-no-op guards (the dangerous twin of the above)**: A consumer that *silently does nothing* when a dependency is nil (`if dep == nil { return }` / `if dep == nil { return nil }`) hides ordering and wiring bugs — nothing logs, nothing errors, the feature just never activates, and the failure surfaces far away (e.g. a downstream 401/empty result with no trace). For any **load-bearing** dependency that must always be present in production, the absence should fail loud (return/log an error, or fail fast at boot), not return quietly. When you see such a guard, prove the dependency is actually wired on the path that reaches it, and confirm a degraded/local fallback isn't masking a misconfiguration.
- **Off-by-one, nil dereference, type assertion without check, integer overflow** — the classics

### 5. Function design

For each function touched or added, ask:
- **Unused parameters**: Any arguments that aren't actually used inside? Remove them.
- **Parameters only tests supply**: a parameter every production caller passes as `nil`/const/zero, real only in tests, is dead in production — drop it and adapt the tests (see the Governing principle above; a constructor dep that init-ordering forces production to wire post-construction via a setter is the classic case).
- **Too many responsibilities**: Is the function doing more than one thing? Could it be split cleanly?
- **Too few responsibilities**: Is it a trivial wrapper that could be inlined or combined with its only caller?
- **Name mismatch**: Does the name still describe what the function does after the changes? If the function grew new behavior, either rename it or factor the new behavior out.
- **Signature clarity**: Are booleans hiding what should be named options? Are long parameter lists a sign of a missing struct?

### 6. Horizontal scalability / parallel execution safety

If this code might run in multiple replicas simultaneously (HPA, worker pools, serverless, any parallel execution):
- **Shared state**: Is any in-memory state assumed to be singleton? If so, does it need to move to a shared store?
- **Race conditions across replicas**: Two pods picking up the same work, double-writes, non-idempotent side effects on retry
- **Ordering assumptions**: Does the code assume operations happen in a specific order across replicas? That assumption usually breaks under load.
- **Cache coherence**: Local caches that could diverge across replicas
- **Locking**: If you need a lock across replicas, is it actually distributed (e.g., DB row lock, distributed mutex) rather than an in-process lock?

### 7. Test certification on every changed module (MANDATORY — final gate)

**Runs LAST, after sections 1–6 are complete and every finding's fix has landed in the working tree.** The audit itself routinely produces new code changes — a fixed nil check, a tightened validation, a renamed helper, a hardened test. Those edits are themselves "new code"; running tests before the audit's own fixes are in is certifying a state that no longer exists. The certification must reflect the actual bytes about to be pushed.

**Run the test suites for every module whose files changed AND every module that consumes them.** "Module" here is the project's natural unit of code organization (Go package, Python module, JS/TS workspace package, Rust crate, Java package, Ruby gem, etc.). Compiling / type-checking clean + new unit tests passing is NOT enough — the most expensive bugs land in modules you didn't touch but that wire the code you did touch (composition roots, e2e bootstraps, callers using a partial fixture). Failure mode this section exists to catch: new code passes lint + new tests pass, but a sibling integration test in another module builds a partial dependency graph, hits the new code through an untested wiring path, and crashes (null deref / typed-nil / undefined attribute / panic / unhandled exception).

Procedure:
1. Confirm sections 1–6 are done and every fix is applied. If a new finding surfaces during this section, loop back: fix it, then re-enter this section from step 2.
2. Enumerate changed modules. Drop changed file paths to their module boundary using whatever the language uses — directory for Go packages and JS/TS workspaces, `__init__.py` ancestor for Python, `Cargo.toml` ancestor for Rust, etc. Generic shape: `git diff --name-only <base>...HEAD | <map-files-to-modules> | sort -u`. These are the **direct change set**.
3. Enumerate consumers — for each changed module, find every module that imports / requires / depends on it. Use the language's native reverse-dependency tool:
   - Go: `go list -f '{{.ImportPath}} {{.Imports}}' ./... | grep <pkg>`
   - Python: `grep -rn 'from <module>\|import <module>' ./` or `pipdeptree --reverse`
   - JS / TS: `grep -rn "from ['\"]<pkg>" ./src` or workspace-level `nx affected`, `turbo affected`
   - Rust: `cargo tree -i <crate>`
   - When no native tool exists or the lookup is heavy, fall back to a recursive grep for the module's import string.
4. Always include the project's entry-point modules in the test set — wherever the application boots (`cmd/*` in Go, `main.py` / app factory in Python, `apps/*` or `packages/server` in JS monorepos, `src/bin/*` in Rust, the executable target in Java/Kotlin). Composition / bootstrap / e2e tests live there and trip wiring bugs that pure-unit tests miss.
5. Run the project's test command on the union of change set + consumers + entry-points. Use the language's "no-cache" flag (Go: `-count=1`; Python: `pytest --cache-clear`; JS: `--no-cache` / `vitest run`; Rust: cargo's test command does not cache successful test bodies, but disable any code-coverage cache if present).
6. Every failure is a blocker: fix the code (or the test if it is genuinely wrong), re-run from step 2 (the fix itself may pull in more modules). Do NOT mark the audit clean while a related module fails.

The cost is real (minutes to tens of minutes). The cost of skipping is worse — a CI failure on the MR, a forced fix-up commit, a re-review cycle, and a credibility hit on the original "tests pass" claim. Pay the cost.

Document in the final report exactly which modules were run AT THIS STEP, in the project's own naming convention. A line like "ran tests on the change set (3 modules) + 4 reverse-dependency modules + entry-point — all green, post-audit-fix state" is the evidence that backs the "clean" claim.

## The final report

After working through the checklist and applying fixes, produce a diagnostic report. The report contains problems and the evidence backing the verdict — nothing else. Its parts, in order:

1. One-sentence verdict: clean, or the count and worst class of what was found.
2. `### Findings and fixes` — one bullet per defect: what was found → what was fixed (or "needs user input: <question>"). Present only when there is at least one finding.
3. `### Still needs attention` — items flagged for user input or out of scope. Present only when at least one item exists.
4. `### Test certification` — always present:

```
### Test certification
- Changed modules: <list, in the project's own naming convention>
- Reverse-dependency modules run: <list>
- Entry-point modules run: <list>
- Test command used: <e.g. `go test -count=1`, `pytest`, `pnpm test`, `cargo test`>
- Result: <all green | failures + what was fixed>
```

A fully clean audit is the verdict sentence plus the Test certification block — two parts, nothing between them.

**Verdict first, evidence after.** The reader's next decision is ship-or-don't, and that decision lives in the findings. Certification is what *backs* the verdict, so it goes below it — a report whose first block is a module list buries its own answer.

The Test certification block is mandatory — it is the evidence behind every "clean" claim in the report. An audit that omits it is incomplete, even if all six numbered sections turned up nothing. Mandatory means present, not first.

**A checklist section that found nothing produces zero report lines.** The verdict sentence is the single place that asserts overall cleanliness; every line after it states a defect, a needs-input item, or a certification fact. After the last finding bullet, the next line is the next section header — no "no other findings" summary, no walkthrough of what was checked and found fine, no per-category "N/A" entries. The reader pays for output tokens; lines certifying cleanliness are the expensive way of saying nothing. If a checked-and-clean area genuinely changes what the reader does next (a by-design limit they would otherwise file as a bug), it earns one finding-shaped bullet — not a tour of clean categories.

## Notes on tone

The audit is a tool for catching your own misses, not a performance. Be direct about what's broken, honest about what you're unsure of, and specific about what you changed. A report that says "fixed the auth check on line 47 of handler.go — it was only validating read permissions, not write" is more useful than "improved security."
