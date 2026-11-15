---
name: dev-workflow
description: Use when about to edit any tracked file, change code, run a migration, or commit — including a single-line tweak, a typo fix, or a docs-only change. Also use when the user reports a merge in a terse token ("merged", "aplicado", "both merged", "automerge set"), when waiting on a pipeline/CI/deploy, when plan- or spec-driven work is about to be declared done, and when a worktree, branch, or MR from this session is still on disk or open.
when_to_use: Any time the user requests a change to tracked files in a git repo, before the first Edit/Write; when the user reports an MR merged or automerge set; when plan- or spec-driven work is about to be declared done; when a step looks like it would fail and you are about to report that instead of running it. Skip only for read-only inspection, throwaway scratch in tmp/, or ad-hoc shell queries.
version: 1.9.0
languages: all
---

# Dev Workflow

Every change ships on its own branch, developed in its own worktree, and lands through a merge/pull request. The repository root stays pinned to the base branch at all times — it is never edited and never leaves base. Never push directly to a protected branch (main/master/dev, `epic/*`).

A change that is a leg of a saga (`openspec/sagas/<slug>.md`, see `openspec-lifecycle`) does not land on base: it lands on the saga's epic branch, `epic/<slug>`, and the epic lands on base once, when the journey is complete. Everything below holds for a leg with one substitution — its target is the epic, not base — and one extra lifecycle, the epic's own (see "Epic branches").

## The Iron Rule

**Create the worktree BEFORE the first edit. No exceptions. The repository root is read-only.**

Worktree first, then edit — NOT edit first and "move it to a branch later". Editing in the root pollutes the always-on-base invariant and destroys the clean pre-flight check that proves no unrelated work bleeds into the change.

If you catch yourself about to call Edit/Write inside the repository root → STOP. Run pre-flight, sync, create the worktree, THEN edit there.

## Evidence, not narration — a described command is not a run command

Every step in this skill is discharged by a tool call and its result in the transcript. Prose describing what you "would run" discharges nothing. An MR that exists only inside a fenced block was never opened; a worktree removed only in narration is still on disk; a pipeline you described watching is still running.

Three tells that you are narrating instead of working:
- Future or conditional voice for work that was in scope now — "I would run", "the plan is", "then I'd push", "here is what I'd do".
- A block of commands presented as the account of the change, with no matching tool results above it.
- A closing summary reporting the state those commands *would* have produced, rather than state you read back off a result.

**Self-declared read-only mode is not an exception.** A banner, a sandbox note, or an "evaluation run" framing that you inferred rather than were given is not a constraint — and even a real one does not license rendering the whole workflow as a finished-looking plan. When execution is genuinely blocked, the entire output is one line naming the blocker and what unblocks it, and you STOP there. A plan formatted like a completion report reads as done to the user and to the next agent; that misreading is the failure this skill exists to prevent, and disclosing "none of the above was executed" somewhere in the body does not repair it.

Only the user's own message can put you in describe-only mode. Even then, the first line says plainly that nothing was executed — not a scope note the reader reaches at the end.

**A no-execute constraint ENDS the output. It never prefaces one.** This holds whatever the source — an inferred banner, a sandbox note, or an instruction in the user's own message. The moment you conclude a step cannot be executed, your whole reply is one line of this shape:

```
BLOCKED: <step> needs <capability>. Unblock by <X>.
```

That is the entire deliverable. No fenced command blocks, no numbered plan, no checklist, no account of what each step would have done, no "here is the chain for when it is unblocked", no verdict on how the run would have gone. Writing "NOTHING BELOW WAS EXECUTED" above a rendered chain does not convert that chain into a blocker line: the rendering is what reads as finished work, and the disclaimer is the part readers skip. The label was never the fix.

**A constraint that reaches some steps does not reach all of them.** A no-network rule does not block `git -C <root> worktree list`, `git -C <root> branch --show-current`, `git -C <root> worktree remove`, `git -C <root> branch -d`, or a CLAUDE.md read. Run every step the constraint does not touch — as real tool calls — and let the blocker line name only the steps it genuinely reaches. A partial blocker promoted into a total one is a skipped chain with an excuse attached.

**A step declared inapplicable is proven inapplicable.** "This repo has no project tail", "no spec system here", "no formatter configured", "there is no plan to audit against" — each is a finding backed by the read that establishes it (CLAUDE.md / AGENTS.md, the archive path, the config file), and that read belongs in the transcript like every other step. Assumed no-ops ship half-delivered changes exactly the way skipped steps do.

**A predicted failure is not a result.** "That command would fail here" is a hypothesis; the command's own output is the finding. This is the most expensive shortcut in this skill, because it always arrives dressed as caution:

- `git -C <root> branch -d <branch>` refusing on unmerged commits is the guard WORKING — and you obtain that guard by running the command and reading the refusal, never by reasoning that it would refuse. Same for `worktree remove` on a dirty tree. Declining to run the safe variant protects nothing; it only deletes the check and leaves the branch and worktree on disk.
- "GitLab / ssh / the cluster is unreachable from here, so `push` and `glab` would not have completed" is a guess until a call returns the error. Issue the call, quote the error, then name it as the blocker. In the transcript, an unattempted step reported as a foregone failure is indistinguishable from a step you simply skipped — and it is wrong exactly as often as the environment surprises you.
- A destructive-sounding name is not a destructive command. Steps 10.3 and 10.4 are the refusing variants by construction. There is nothing to shield the user from here except an unrun step.

Run it. Read the result. Report the result. "It would have failed" is a sentence you may only write after the failure has a tool result behind it.

**Asking for missing information is one question — never a question with the chain attached.** When a target genuinely does not resolve (no such repo, no such file, an id that maps to nothing), refusing to invent it is correct. Rendering "what I will run the moment you answer" is not: the commands are the part that reads as work, and the request above them is the part readers skim. The whole output is the blocker line, which may name what unblocks it and nothing more.

## Red Flags — STOP, you're rationalizing

| Thought | Reality |
|---------|---------|
| "Just a one-line fix" | Worktree + MR apply uniformly. One `git worktree add` command. |
| "It's trivial / a typo" | The audit trail does not care about size. Worktree first. |
| "I'll just edit in root, it's tiny" | Root edits break the always-on-base invariant. Worktree cost is one command. |
| "This leg is green on its own, I'll target dev" | The saga exists because base is right only with every leg in. A leg on base is a half-done journey deployed. Target `epic/<slug>`. |
| "The epic is behind dev, I'll rebase it" | A rebased epic orphans every open leg branch on the journey — and the forge refuses the push anyway. Merge base into the epic through a sync MR. |
| "The leg's MR pipeline was green, so the epic is fine" | That pipeline built the leg alone. Two legs, each green there, meet only on the epic. Watch the epic's pipeline for the merge, and make sure base's CI runs on `epic/*` before the epic exists. |
| "I'll just push the sync merge straight to the epic" | The epic accepts merge requests only; that is what makes it safe to collaborate on. A sync is an MR like any other. |
| "I'll branch after I see it works" | Pre-flight must run on a clean root. Worktree first. |
| "I'm already in a worktree" | Is it the RIGHT worktree for THIS change, cut from current base? A NEW change gets a NEW worktree. |
| "The root's on base, my HEAD is safe" | A checkout SHARED with other agents/processes can have its HEAD moved out from under you mid-change — commits then land on base, not your branch. Your worktree has its OWN HEAD, but verify `git branch --show-current` before every commit and push. |
| "User wants it done fast" | The workflow IS the fast path. Fixing a dirty-tree mistake is slower. |
| "I'll just add a `fix:` commit on top" | Fold it into the commit it corrects (8). Stacked fixups are review noise and land as garbage history. |
| "Branch is already pushed, can't rewrite" | Curation force-push with `--force-with-lease` is expected on MR branches. Only protected branches are immutable. |
| "I stopped before both destructive steps" | "merged" IS the go-ahead. `worktree remove` / `branch -d` refuse rather than destroy — run them (10). |
| "Two one-word answers, then I run unattended" | A second turn to start a chain the user already triggered is a failed step 10. Query the forge, then run. |
| "`!16` is the only plausible referent; confirming is yours, not mine to assume" | `glab mr list --merged` answers it in one call. Ask the forge, not the user. |
| "Automerge is set, so the merge isn't mine to follow" | Automerge lands it without you; it does not cancel the chain. Watch to terminal, then run 10. |
| "I'm already in that directory, bare `git` is fine" | cwd does not survive between Bash calls. Every tree-targeting call carries `git -C <abs-path>`. |
| "`cd <path> && git …` is the same thing" | It fired 39 untrusted-hooks prompts. `git -C` does the same job with no cd. |
| "I reviewed the diff myself, the plan was simple" | Self-review is not the audit. Plan/spec work runs `/audit-plan-execution` unconditionally (7). |
| "`sleep 300 && glab ci status` is one command, not a poll" | Blocked 158 times in 121 sessions. Background or Monitor until-loop (9). |
| "Author identity is global config, not mine to touch" | Set it repo-locally before the first commit; fixing it later means a force-push (5). |
| "This run is read-only per the evaluation banner" | You inferred that banner. A constraint you cannot quote from the user is not a constraint — run the chain. |
| "Everything below is the plan, not a transcript" | Then nothing shipped. A step counts only with a tool call and its result behind it. |
| "None of the above was executed, and I said so up front" | Disclosing a skipped chain does not run it. Step 10's five steps are five tool calls, not five bullets. |
| "I'd rather show what I *would* do than act unsafely" | Steps 3 and 4 refuse rather than destroy; step 2 is `--ff-only`. There is no unsafe step to protect the user from — only an unrun one. |
| "Describing the commands is equivalent — the user can run them" | Handing back homework is not delivery. If something truly blocks you, one line naming it, then stop. |
| "This repo has no OpenSpec/PDF/deploy tail, so it's a no-op" | Prove it: read CLAUDE.md / AGENTS.md and check the archive path. Assumed no-ops ship half-delivered changes. |
| "No plan text in my context, so there's nothing to audit against" | Missing text ≠ unplanned work. Fetch the plan (file, issue, spec, task list); the diff audit (7) runs either way. |
| "The user's own message set describe-only mode, so this written account is legitimate" | Then the account is ONE line: `BLOCKED: …`. Nobody who tells you not to execute is asking for a rendered chain that reads as a completion. |
| "I labeled it 'NOTHING BELOW WAS EXECUTED' in the very first line" | The label is not the repair; the rendering is the failure. Blocked → blocker line, stop, nothing under it. |
| "Every network step is barred this run, so the chain can't be discharged — hence the description" | Then say exactly that in one line and stop. A blocker ends the output, it does not introduce it. And it bars only the network steps: `worktree list`, `worktree remove`, `branch -d`, the CLAUDE.md read still run for real. |
| "The repo doesn't resolve in cwd and resolving it is a network call — hard stop" | Locate it locally first: `git -C <root> worktree list`, the known checkout roots, `find <abs> -maxdepth N`. "Hard stop" is what you declare AFTER those come back empty. |
| "This repo declares no archive/regen/deploy tail — and I can point at the read that says so" | The tail is scoped to the CHANGE that merged. Until you have identified which MR merged and which worktree it maps to, no repo-level read clears 10.5. |
| "'Don't ask me anything' removes the confirmation turn; it doesn't authorize assuming scope" | Right — it points you at the forge. `glab mr list --merged`, then run. Reading it as permission to stop is the same failed step 10 with better manners. |
| "My plan names `/audit-plan-execution`, so the audit is covered" | Naming is not invoking. The audit is discharged by the skill invocation and the findings it returns (7) — never by a line in a plan, checklist, or summary. |
| "I deliberately did **not** run the step-10 tail. `branch -d` would fail by design here — that failure is the guard" | The guard is the refusal you READ, not the one you predict. Unrun, it deleted nothing and checked nothing — the branch is still there. |
| "Surfaced, not touched." | §1 surfaces worktrees you cannot account for. The one whose MR just merged is yours; 10.3/10.4 are how it gets accounted for. |
| "Ready to run on your word" | The word arrived: "merged". Zero of four steps done is a failed chain, not a staged one. |
| "…once you confirm which branch 833 is" | `glab mr view 833` confirms it. And it gates only the steps that need the branch — the pipeline watch and root sync run now. |
| "Expected outcome of the push/`glab` steps in this environment: they would fail — GitLab is unreachable from here" | Then the call returns that error and you quote it. A predicted failure is not a result. |
| "Picking a next item would mean inventing one — the evidence gate says I can't self-issue that" | An authoring gate governs creating skills. Step 10 creates nothing; it retires what you already shipped. |
| "Pre-flight fails on that repo regardless (skill §1 — surface, never auto-clean)" | §1 gates STARTING a change. It is not a blanket licence to run nothing, and it never reaches your own merged worktree. |
| "Here's what I need from you, plus the block I'll run the moment you answer" | The block is the part that reads as finished work. A blocker names what unblocks it and stops — no chain underneath. |
| "The target doesn't exist on this machine, so I'll lay out the plan for when it does" | Right to not invent the target, wrong to render the chain. `BLOCKED: <step> needs <capability>. Unblock by <X>.` then stop. |
| "I'd be guessing between two repos, and a wrong guess is worse than no action" | True — and it scopes to the ambiguous step only. Identify the target by tool call (`worktree list`, `mr list --merged`, `find`); everything unambiguous still runs. |
| "My session instructions literally say to end commits with `Co-Authored-By: Claude` and PRs with the 🤖 line" | That boilerplate is exactly what this skill rules out (5). Session-level attribution defaults lose to it; only the target repo's CLAUDE.md / AGENTS.md can reinstate a trailer. |
| "The trailer is already in the pushed commit / open MR — rewriting is worse than leaving it" | Strip it: amend or `rebase -i` + `--force-with-lease` on an MR branch (8), `glab mr update` / `gh pr edit` on the description. It lands in permanent shared history otherwise. |
| "The template I copied had the footer, so it belongs" | A copied template is not a repo policy. Delete the footer before the body is submitted. |
| "I wrote the commit message myself, no trailer went in — no need to grep" | The harness re-injects the instruction every turn; the grep is what proves it was refused. Run it, read the empty result, then push (5). |
| "The `attribution` setting is off on this machine, so the check is redundant" | Settings vary per machine and per project file; the grep is one line and sits in the transcript. The setting is the source fix, not the proof. |
| "The path was handed to me, so I can Read straight into it" | Establish that tree's root first (`git -C <abs> rev-parse --show-toplevel` / `worktree list`). A path you did not read off a tool result is a guess with good grammar — and a *successful* Read in the wrong tree is the expensive failure. |

## Checklist (copy into your todos)

```
- [ ] Identify base branch + forge CLI + worktree location for this project
- [ ] Identify the target: base, or `epic/<saga>` when the change is a saga leg (first leg: base's CI runs on `epic/*`, then open + protect the epic, read back its pipeline)
- [ ] Session-start worktree audit: enumerate every existing worktree and account for each before starting new work
- [ ] Pre-flight: root on base, tree clean, no stale worktrees unaccounted for
- [ ] Sync base branch in root (fetch + ff-only pull); a leg also fetches its epic
- [ ] git worktree add with new <type>/<slug> branch off the target; bootstrap (env/config/deps)
- [ ] Spec first if behavior changes (when project uses specs)
- [ ] Verify commit identity before the first commit; check `attribution` in ~/.claude/settings.json is off
- [ ] Grep every commit body and the MR body for AI attribution before push / mr create — empty result in the transcript
- [ ] Develop in the worktree with conventional commits; every commit that will merge can go live (build and all tests pass at it)
- [ ] Format before commit (go fmt / cargo fmt / prettier / project formatter)
- [ ] Audit before shipping (gaps, security, tests, bugs)
- [ ] /audit-plan-execution when the work came from a plan/spec — unconditional
- [ ] Curate commits before the MR is ready: fold intermediate commits until each can go live, force-push if already published
- [ ] Push + open MR/PR with structured body, targeting the target branch
- [ ] Watch pipeline to terminal state — background/Monitor loop, never sleep-poll
- [ ] On MR updates: check title/description still accurate; refresh if stale
- [ ] After merge: pipeline terminal, sync root, remove worktree, delete branch, prune, project tail
- [ ] Record a rule/decision the owner declared durable in the repo, not only in memory (see Rules)
- [ ] Last leg merged and proof green: sync base into the epic, merge the epic into base, retire the epic branch
```

A box is checked by a tool call and its result — never by a described command, and never by a step declared inapplicable without the read that proves it.

## Path Discipline — the worktree and the root are two trees

Wrong-path tool calls are the largest single error class here: ~1,036 of them, 42% of all tool errors — and 613 of 642 failed Reads were issued with cwd inside a `tmp/<branch>` worktree while the path resolved against the main checkout, or the reverse.

1. **Resolve each tree root once, up front, and reuse it** — `git rev-parse --show-toplevel` for the root, `git worktree add` / `git worktree list` for the worktree. Two absolute strings, fixed before the first tool call. Before the FIRST Read/Edit/Write/Glob/Grep that touches a tree, that tree's establishing call is already in the transcript — `git -C <abs> rev-parse --show-toplevel`, `git -C <root> worktree list`, or `ls <abs>`. This holds when the path arrived in prose, in a task description, or in a message from another agent: a path you did not read off a tool result is unestablished no matter how confident it looks.
2. **Every path is absolute** — Read, Edit, Write, Glob, Grep, `ls`, `find`. cwd is not preserved between Bash calls, so a relative path silently resolves against whatever tree the harness happens to be in.
3. **Never mix the trees in one operation.** Edits belong to the worktree; `fetch` / `pull --ff-only` / `worktree remove` belong to the root. An edit landing in the main checkout is invisible to the MR and breaks the always-on-base invariant.
4. **Every tree-targeting git call carries `git -C <absolute-path>`** — including against the repo you believe is current, and including read-only ones (`status`, `branch`, `worktree list`, `rev-parse`). Never `cd <path> && git …`: that form produced 39 untrusted-hooks prompts.
5. **`ls`/Glob a path before reading deeply into it** whenever you inferred it from a name, slug, or description — or were handed it in prose — rather than reading it off a tool result. A blind Read on a guess burns a turn; a *successful* Read in the wrong tree is worse — you then reason and edit against the wrong file.

## Steps

### 0. Project conventions (detect once)
- **Base branch**: the integration branch this repo bases development on and merges into. Check CLAUDE.md / AGENTS.md first; else `git symbolic-ref refs/remotes/origin/HEAD`. Commonly `main`, `dev`, or `develop`. Never target a release/protected branch from a feature branch.
- **Target branch** — `<target>` below: where this change branches from and merges into. It is `<base>`, unless the change is a leg of a saga — then it is `epic/<saga>`, the slug of the saga file that names the change. This is a read, not a recollection: `ls openspec/sagas/` (or the project's equivalent) and grep the change id before deciding. A leg branched from base and targeted at base is the miss this rule exists for. The root is pinned to `<base>` regardless; an epic is never checked out anywhere — it exists on the remote and as `origin/epic/<saga>`.
- **Forge CLI**: GitLab remote → `glab`; GitHub remote → `gh`. Check `git remote get-url origin`.
- **Worktree location** (first match wins):
  1. Repo root contains `tmp/` AND `git check-ignore -q tmp` passes → `tmp/<slug>/`. The gitignore check is mandatory — an unignored `tmp/` disqualifies it.
  2. Otherwise → `~/.claude/worktrees/<repo>-<hash8>/<slug>/`, where `<repo>` is the basename of the repo root and `<hash8>` the first 8 chars of `sha256` of the absolute repo root path (collision-proof for same-named repos, out of every repo tree, survives reboots, one place to audit leftovers).

  `<slug>` = the branch name with `/` flattened to `-`: branch `feat/add-x` → directory `feat-add-x`.

### 1. Pre-flight — fail fast
```bash
git rev-parse --abbrev-ref HEAD   # MUST be <base> — root never leaves base
git status --porcelain            # MUST be empty (no uncommitted/untracked)
git worktree list                 # existing worktrees: each must map to a known in-flight MR
```
Root not on base (legacy state from pre-worktree habits), dirty tree, or a worktree nobody can account for → STOP and surface the state. Do NOT stash/discard/auto-push/auto-remove to "clean up" — the user decides. Stale worktrees are surfaced, never auto-deleted.

The pre-flight begins with a **session-start worktree audit**: enumerate every entry in `git worktree list` and account for each — which MR it belongs to, whether that MR is open, merged, or abandoned — before starting new work. This is a proactive read at session start, not a reaction to the owner asking "why so many worktrees". Each entry is either a known in-flight MR (reused per §1's "continuing an existing MR") or a leftover you flag; the enumeration happens regardless of whether the owner has asked, so accumulation never reaches the point where the owner has to notice.

This gate applies when STARTING a new change. Continuing an existing MR → reuse its existing worktree, do not create a second one.

### 2. Sync base (in the root)
```bash
git fetch origin <base>
git pull --ff-only origin <base>
git fetch origin <target>                    # leg of a saga: the epic is fetched, never pulled — nothing checks it out
```
No checkout needed — the root is already on base by invariant. Divergence → resolve with the user, do not force.

### 3. Create the worktree
```bash
git worktree add <wt-path> -b <type>/<slug-with-slashes> <base>                 # plain change
git worktree add <wt-path> -b <type>/<slug-with-slashes> origin/epic/<saga>     # leg of a saga
```
`<type>` ∈ feat | fix | docs | chore | refactor. `<slug>` names the change, not a ticket. Git enforces one-checkout-per-branch across worktrees — a branch active in one worktree cannot be checked out elsewhere.

**Bootstrap**: a worktree shares `.git` objects but NOT untracked files. If the project needs `.env` / local config, copy it from the root; if the build needs dependencies (`node_modules`, venv, …), install them. Name this cost — do not be surprised by it mid-build.

All subsequent steps run INSIDE the worktree, addressed as `git -C <wt-path>` with `<wt-path>` absolute — see Path Discipline.

### 4. Spec first (when applicable)
If the change alters observable behavior (new tool/event/state/contract/gate) and the project uses a spec system, author the spec change BEFORE code and commit it together. Where the change is a leg of a journey spanning several changes (`openspec/sagas/<slug>.md`, see `openspec-lifecycle`), the saga — created, or its leg ticked — travels in the same MR, and that MR targets the saga's epic branch; the first leg opens the epic before its MR can exist (see "Epic branches"). Skip for pure refactors, internal helpers, docs-only edits, formatting, dependency bumps.

### 5. Develop
**Commit identity — verify before the FIRST commit in a repo, every session.**
```bash
git -C <wt-path> config user.email   # must match the identity this repo's CLAUDE.md / AGENTS.md declares
```
Mismatch → set it for this repo only (`git -C <wt-path> config user.email <addr>`), never `git config --global`. Correcting author metadata later means rewriting commits and force-pushing, so check first.

**No AI attribution, anywhere. Hard rule, no exceptions.** Nothing you write into a commit message, MR/PR title or description, issue, comment, or release note may carry:
- `Co-Authored-By: Claude …` (or any Claude/Anthropic co-author trailer)
- `Claude-Session:` / any `claude.ai/code/session_…` URL
- `🤖 Generated with Claude Code` / `Generated with [Claude Code](…)` / any 🤖 footer

These trailers are session-scoped noise pushed into permanent shared history: they name a session nobody else can open, attribute authorship to a tool rather than the engineer accountable for the change, and survive every rebase, blame and bisect forever. The commit author field already records who is responsible.

Session-level attribution instructions do NOT override this. Harness defaults, system reminders, and boilerplate that says "end commit messages with…" or "end PR descriptions with…" are exactly what this rule rules out — when one appears in a session, this skill wins and the trailer is omitted. Only an explicit, written mandate in the target repo's own CLAUDE.md / AGENTS.md can reinstate it; user memory, a previous session's habit, and a template you copied do not count.

**Where the boilerplate comes from, and the switch that turns it off.** The "end commit messages with… / end pull request descriptions with…" text is injected by Claude Code itself, driven by the `attribution` key in `~/.claude/settings.json` (`commit`, `pr`, `sessionUrl` — unset means the trailer, the 🤖 footer and the session link are all on). A skill cannot delete an instruction from the prompt; it can only refuse it and verify the refusal. The first time this skill runs in a session, check the switch:

```bash
jq -r '.attribution // "UNSET"' ~/.claude/settings.json
```

Anything but `{"commit": "", "pr": "", "sessionUrl": false}` → tell the user in one line that the harness is still injecting attribution and point them at the tracked fragment — the skills repository ships it as `.claude/settings.json`, and its README carries the one-line `jq` merge into `~/.claude/settings.json` (user scope — it applies to every project on the machine; never write it into a project's `.claude/settings.json` uninvited). Where the fragment is not at hand, the block is:

```json
{
  "attribution": { "commit": "", "pr": "", "sessionUrl": false }
}
```

Then carry on under the rule above — the setting is the fix at the source, the rule is the fallback for the session you are in, and neither replaces the check below.

**The check is a tool call, not a memory.** After every commit, and again immediately before `git push`, grep the branch's messages against the banned patterns — the transcript must show the command and its empty output:

```bash
git -C <wt-path> log --format=%B <target>..HEAD | grep -nEi 'co-authored-by:.*(claude|anthropic)|claude-session:|claude\.ai/code/session_|generated with.*claude|🤖'
```

Any hit → strip it before the push: amend or `rebase -i` on an unpushed commit; already pushed on an MR branch → strip and `git push --force-with-lease` (see 8 — this is history curation). Already in an MR/PR description → `glab mr update` / `gh pr edit` and remove it. A push whose transcript has no matching grep is a push whose history nobody checked.

Conventional Commits: `type(scope): subject`.

Subject (cbeams rules, adapted to Conventional Commits):
- Imperative mood: "add", not "added"/"adds" — matches git's own generated messages.
- Aim ≤50 chars total; 72 is the hard limit (forges truncate beyond it).
- No trailing period. Description stays lowercase after `type(scope): ` — the
  Conventional Commits convention supersedes cbeams' capitalize rule.

Body (when the diff alone doesn't explain the change):
- Blank line between subject and body — `log`/`shortlog`/`rebase` misparse otherwise.
- Wrap at 72 — git never wraps for you.
- Explain WHAT and WHY, not how (the diff shows how): the problem being solved,
  side effects, unintuitive consequences.
- Subject-only commit is fine when the change is self-explanatory.

**Every merged commit can go live.** With HEAD at any commit that reaches the target branch, the build passes, every test passes, and the change works as a whole. That is what a coherent commit means. An MR may carry several such commits. While the MR is open, intermediate commits are fine (work in progress, a test observed red before its fix, a later correction); see 8 for folding them before the MR is declared ready to merge.

**Shared checkout — verify HEAD before every commit/push.** When other agents or processes share this repository root, its HEAD is not yours to trust: a concurrent `git checkout` there moves HEAD off your branch, and your next commit lands on base (or another branch) instead. The worktree's own HEAD is the isolation — but a race can still catch a step run against the shared root, so run `git branch --show-current` immediately before each commit and push. If a commit landed on the wrong branch, recover by ref, never by redoing the work: reparent your branch onto the stray tip and reset the misplaced ref back to upstream.
```bash
git branch -f <your-branch> <stray-tip-sha>   # move your branch ref onto the commits (branch not checked out)
git reset --hard origin/<base>                 # from the misplaced ref (e.g. base), drop them back to upstream
```
Nothing is lost — the commits exist by SHA (`git reflog`); you are only moving a branch pointer. Prefer explicit-SHA ref ops over HEAD-relative ones whenever the shared root may move under you.

### 6. Format before commit (language-gated)
When the diff touches **Go code** (any `*.go` file in the staged set), `go fmt ./...` MUST run successfully from the module root BEFORE `git commit`. Unformatted Go diffs noise up review + tend to land alongside real changes, hiding intent. This is a hard precondition — not a follow-up cleanup.

```bash
# from the Go module root (where go.mod lives)
go fmt ./...
git add -A   # re-stage any formatting-only edits go fmt produced
```

If `go fmt` modifies files OUTSIDE your intended diff (an unrelated formatting drift in the repo), STOP and surface it — those edits belong in a separate `chore: gofmt drift` commit, not bundled into your change.

Other languages — apply the project's formatter the same way when one is configured (e.g. `cargo fmt` for Rust, `pnpm format` / `prettier --write` for JS/TS, `ruff format` for Python). When a project's CLAUDE.md / AGENTS.md names a formatter, that formatter is mandatory under the same gate.

### 7. Audit before shipping
Before opening the MR, audit the diff for gaps, security concerns, missing tests, subtle bugs. Every behavior change needs a test that fails when the rule is violated — and "fails when violated" is a claim you PROVE, not assume: mutate the code under test (or revert the fix) and watch the test go red, then restore the code. A test that stays green when the logic breaks is worthless. This is the gate between "it compiles" and "ready for review".

**`/audit-plan-execution` is unconditional for planned work.** If the change came from a plan, spec, OpenSpec change, task list, or any multi-step brief, run the `audit-plan-execution` skill before declaring the MR done — before "ready for review", before reporting completion. Not "if the diff looks risky", not "if the plan was short", not "if I already reread the plan". Four proven skips ended with the user asking "Did you /audit-plan-execution ?", each after the agent had reviewed its own diff and judged it complete. Self-review finds bugs in what you wrote; the audit finds the plan steps you never wrote — the gap self-review structurally cannot see.

The audit is discharged by invoking the skill and reading back the findings it returns. Writing `/audit-plan-execution` into a plan, a todo list, a checklist, or a closing summary discharges nothing — that is a promise, and it is the exact shape the four proven skips took. If you stop the turn before the invocation for any reason, the MR is not done and you do not describe it as done.

If a plan exists but its text is not in your context, FETCH it — open the file, the issue, the spec, the task list — before concluding there is nothing to audit against. "No plan text here" is a read you have not done yet, not a finding. And the diff audit above (gaps, security, tests, subtle bugs) runs regardless of whether a written plan exists; only the plan-conformance half depends on the plan.

### 8. Curate commits — every merged commit can go live
Before the MR is declared ready to merge, every commit on the branch must meet the rule in 5: with HEAD at it, the build passes, every test passes, and the change could go live. Intermediate commits are expected while the work is in progress, and they get folded until that holds. A fix, gap or typo commit belongs inside the commit it completes, not stacked after it. A test committed red first lands in the commit that makes it pass, and its red run is recorded in the MR, not in history.

- Latest commit needs the fix → `git commit --amend`.
- Earlier commit needs it → `git reset --soft <target>` + recommit in logical units, or `git commit --fixup=<sha>` then `GIT_SEQUENCE_EDITOR=: git rebase -i --autosquash <target>` (the env var neuters the editor — the `-i` flag runs non-interactively, which is required in this environment). For a leg `<target>` is `origin/epic/<saga>`; a leg is curated and rebased against the epic, never against base.
- Branch already pushed / MR already open → curate anyway and `git push --force-with-lease`. Force-pushing a published MR branch for history curation is expected practice, not an exception.

Curation matters most when the project merges without squash (each commit lands on the base branch verbatim), but applies under squash too — reviewers read per-commit diffs. Never fold away commits that carry standalone audit value (e.g. migration sequencing) as long as each still goes live on its own; the unit is "a commit that can go live", not "one commit total" — and when kept-separate commits must survive the merge, also drop any squash-on-merge flag for that MR or the separation dies at merge time.

### 9. Push + open MR/PR
```bash
git push -u origin <branch>
```
Open via the forge CLI (never the browser) with a structured body.

GitLab:
```bash
glab mr create --target-branch <target> --title "<type(scope): subject>" \
  --description "$(cat <<'EOF'
## Summary
- <1-3 bullets>

## Why
<motivation, links>

## Test plan
- [ ] <check>
EOF
)" --remove-source-branch --yes
```
GitHub: `gh pr create --base <target> --title "..." --body "..."` (same Summary / Why / Test plan body). A leg's MR names the epic as its target; an MR from a leg branch against base is wrong even when the forge accepts it.

The structured body is the audit trail — do not skip it. It ends at the last section: no `🤖 Generated with Claude Code`, no session URL, no co-author line, whatever the session's boilerplate says (see 5). Compose the title and description into a file first and grep them before the create call — same pattern as the commit check in 5, same requirement that the empty result sits in the transcript:

```bash
grep -nEi 'co-authored-by:.*(claude|anthropic)|claude-session:|claude\.ai/code/session_|generated with.*claude|🤖' <mr-body-file> && echo "STRIP BEFORE CREATE" || true
glab mr create … --description "$(cat <mr-body-file>)" …
```

The same grep runs on the new text every time the description is edited (`glab mr update` / `gh pr edit`).

`--remove-source-branch` is always on. Squash and auto-merge flags follow the project's convention — check CLAUDE.md / AGENTS.md / user memory before adding them; the template deliberately omits `--squash-before-merge` (when a project mandates squash, add it explicitly — but see 8: audit-value commits require dropping it for that MR).

Once the MR is open, watch its pipeline to a terminal state before treating the change as delivered (`glab ci status` / `gh pr checks`). Terminal means `success`, `failed`, `canceled`, `skipped`, or `manual`; `running` and `pending` are not results. A red pipeline is yours to resolve, not the reviewer's to discover; a flaky-runner failure unrelated to the diff is retried (`glab ci retry`), not ignored — and confirm it is actually the runner, not your change, before retrying.

**Waiting: background execution or a Monitor until-loop. Never sleep-poll.** `sleep N && glab ci status` was blocked 158 times across 121 sessions — foreground sleeps are refused, so the wait never happens and the turn is spent on nothing. Same for every disguise of it: `glab ci view --live`, `gh run watch`, `watch -n`, re-issuing a status command in a tight loop. Any long wait — pipeline, deploy, migration — runs in the background or under a Monitor until-loop keyed to the terminal condition.

**After every update to an open MR** (new commits, force-push, scope change), re-read the MR title and description against what the branch now contains: do they still describe the change accurately? This is a check, not a mandatory rewrite — folding a fixup or addressing a review nit usually changes nothing, while a new capability, a dropped step, or a changed approach makes the old text wrong. Stale → update with `glab mr update <id> --title "..." --description "..."` (GitHub: `gh pr edit`). The MR text is the audit trail reviewers and future readers trust; it must not describe a version of the change that no longer exists.

### 10. After merge — the complete chain, triggered by one word
Runs only once the MR is confirmed merged. Until then the worktree persists deliberately — that is the model: root stays usable, in-flight work parked in its worktree.

**The trigger is terse.** "merged", "aplicado", "both merged", "automerge set" — a bare confirmation like this triggers the ENTIRE chain below; it is not a status update to acknowledge. Across 290 messages in 191 sessions in 13 projects the user's post-merge input was one token, and the correction after a partial run was: *"Did you sync root repo and cleanupd this session worktree?"* "automerge set" counts: the merge completes without the user, so you follow the pipeline to terminal and then run the rest.

Run all five, in order, in the same turn:

```bash
glab ci status                               # 1. merge pipeline → TERMINAL state (see 9; background/Monitor, never sleep)
git -C <root> pull --ff-only origin <base>   # 2. root absorbs the merged result (fetch alone is not this step)
git -C <root> worktree remove <wt-path>      # 3. refuses if dirty — NEVER --force; dirty → surface to user
git -C <root> branch -d <type>/<slug>        # 4. -d not -D: fails if unmerged, that is the guard
git -C <root> worktree prune
```
A leg of a saga merged into the epic, not into base, so three lines differ: 1 is `glab ci status --branch epic/<saga>` (GitHub: `gh run list --branch epic/<saga> --limit 1`), the epic's pipeline for the merge; 2 is `git -C <root> fetch --prune origin epic/<saga>` (root is on base and base did not move), and 4 is preceded by `git -C <root> branch --set-upstream-to=origin/epic/<saga> <type>/<slug>` — `-d` judges "merged" against the upstream, and without that line it judges against root's HEAD, refuses a leg that did merge, and tempts the `-D` this skill bans. With the upstream set, `-d` still refuses an unmerged leg; that refusal remains the guard.

5. **Project tail** — whatever this repo does after a merge, per its CLAUDE.md / AGENTS.md: OpenSpec archive (`openspec archive <change>`), a saga leg tick or saga retirement, PDF/artifact regeneration, deploy verification. When the leg that just merged was the saga's last and the proof is green, the tail is the epic's own retirement — sync, merge into base, delete (see "Epic branches"). Skipped, it leaves the change half-delivered and the user finds it, not you.

**Each of the five is proven by its own tool call and its result.** A step you narrated, assumed, or declared inapplicable did not run. That includes 5: "this repo has no tail" is a conclusion you reach by reading CLAUDE.md / AGENTS.md and checking the archive/regeneration path, and those reads sit in the transcript like every other step — an unverified "no-op here" is a skipped step wearing a different word.

**The tail is scoped to the change that merged, not to the repo in the abstract.** Identify that change first — which MR merged (`glab mr list --merged` / `glab mr view <id>`), which branch and worktree it maps to (`git -C <root> worktree list`) — because whether a spec needs archiving or an artifact regenerating is a property of what that change touched. A repo-level read cannot clear 10.5 while the merged change is still unidentified; declaring the tail a no-op at that point is guessing with a citation attached.

The remote branch is already gone via `--remove-source-branch` / forge auto-delete.

Nor behind a self-imposed read-only mode: the chain's destructive-looking steps are already the refusing variants, so "I'll describe the chain instead of running it" protects nothing and delivers nothing.

**Do not park the chain behind a confirmation turn.** "Confirm and I run the full step-10 chain" is a failed step 10, not a cautious one. Steps 3 and 4 are already the safe variants — they refuse rather than destroy — so that refusal IS the check you were going to ask for.

**Step 1's "surface, never auto-delete" does not reach step 10.** Pre-flight surfaces worktrees *nobody can account for*, because someone else's in-flight work is not yours to discard. The worktree and branch of the change that just merged are accounted for — by you, this session — and 10.3/10.4 are precisely how they get accounted for. Citing §1 over your own merged worktree ("surfaced, not touched") turns a rule that protects other people's work into a licence to skip your own cleanup. A crowded `git worktree list` is a reason to be precise about which entry is yours; it is never a reason to remove none of them.

**The terminal state is a call, not an inference.** "Automerge is set", "the MR already shows merged", "there was nothing to build" — none of these produce a terminal state. 10.1 is discharged by `glab ci status` / `gh pr checks` returning `success | failed | canceled | skipped | manual`, quoted off the result. No status call anywhere in the turn means 10.1 did not happen, whatever the surrounding narrative claims.

**One unresolved item stalls one step, not the chain.** Not knowing which branch an MR id maps to does not block the pipeline watch, the root sync, the CLAUDE.md read, or the removal of a worktree you *can* identify. Run everything the unknown does not touch — in this turn — and let the single remaining question stand behind completed work. "Ready to run on your word" with zero of the four done is a failed chain wearing a polite sentence; the word already arrived, and it was "merged".

**No other skill's gate suspends this chain.** Evidence gates, planning gates, and skill-authoring rules govern what you may CREATE. Post-merge cleanup creates nothing — it retires artifacts you already made. "That would need a failing baseline / pre-committed criteria / a plan I cannot self-issue" is never a reason a merged worktree stays on disk.

Resolve unknowns by querying the forge, not the user: which MR merged (`glab mr list --merged` / `gh pr list --state merged`), whether it merged at all (`glab mr view <id>`), which worktree it maps to (`git -C <root> worktree list`). If — and only if — two or more MRs from this session are open and the token names none, ask that one question and meanwhile run every unambiguous step.

## Epic branches — where a saga lands

A saga (`openspec-lifecycle`) is a journey that is right only when every leg is in. Its legs therefore never land on base one by one; they land on `epic/<saga>` — the saga file's slug — and the epic lands on base once. The epic is a collaboration branch: it accepts merge requests from anyone on the journey and refuses everything that would rewrite what they built on — direct push, force-push, rebase, deletion. It exists on the remote only. Nobody checks it out, so nobody can rebase it by accident; the forge protection is what makes that a guarantee rather than a habit.

**Open — in the first leg's session, before that leg's MR can exist.** Create it from the synced base without checking anything out, then protect it, then read the protection back — the read is the proof, the POST is not:

```bash
git -C <root> push origin <base>:refs/heads/epic/<saga>
# GitLab: no direct push (0), merge via MR by developers+ (30), no force-push
glab api --method POST "projects/:id/protected_branches" \
  -f name="epic/<saga>" -f push_access_level=0 -f merge_access_level=30 -f allow_force_push=false
glab api "projects/:id/protected_branches/epic%2F<saga>"
# GitHub: PRs required (0 approvals), no force-push, no deletion
gh api -X PUT "repos/{owner}/{repo}/branches/epic%2F<saga>/protection" --input - <<'EOF'
{"required_status_checks":null,"enforce_admins":false,
 "required_pull_request_reviews":{"required_approving_review_count":0},
 "restrictions":null,"allow_force_pushes":false,"allow_deletions":false}
EOF
gh api "repos/{owner}/{repo}/branches/epic%2F<saga>/protection"
```

A forge that will not take the protection call (no permission, no API) is a blocker line, not a reason to run the epic unprotected — an unprotected epic is a shared branch that any `--force-with-lease` can rewrite from under everyone else.

**CI on the epic — the rule is on base before the branch exists.** Each leg's MR pipeline builds that leg alone, on its source branch. The legs are built together only on the epic, and only when CI runs there. Before the push above, read base's CI config: `.gitlab-ci.yml` and the files it pulls in, or `.github/workflows/`. Look for `epic/*` pushes getting base's gates: build, lint, test. Jobs that publish or deploy for base must never write base's targets from an epic. Base's image tags, deploy branch and environment are off limits. On the epic those jobs are skipped, or they publish to a throwaway name keyed to the epic, such as `epic-<saga>-<sha>`. If the config is missing the rule, it is a change of its own: worktree, MR to base, merged before the epic is opened. The epic then inherits the rule, and so does every later epic. The proof comes right after the opening push, and like the protection it is a read-back:

```bash
glab ci status --branch epic/<saga>                  # a pipeline for the opening push; "no pipeline" means the rule is not in effect
# GitHub: gh run list --branch epic/<saga> --limit 1
```

An epic opened before its repo had the rule gets it the same way. The rule lands on base, then a sync MR brings it into the epic, and the epic's first pipeline runs on that merge.

**Legs.** Steps 0–10 as written, with `<target>` = `epic/<saga>`: the worktree branches from `origin/epic/<saga>`, curation rebases onto it, the MR targets it, and after the merge the root fetches the epic instead of pulling base. The pipeline 10.1 watches is the epic's own for the merge commit (`glab ci status --branch epic/<saga>`). The leg's MR pipeline has already finished and does not count. A red epic pipeline is fixed by the next MR into the epic, before any other leg lands. The saga file travels in the leg's MR (step 4), so it reaches base exactly when the epic does.

**Sync — base into the epic, by MR, never by rebase.** When a leg needs something base has gained since the epic opened, and once before the epic merges to base, bring base in as a merge:

```bash
git -C <root> fetch origin <base> epic/<saga>
git -C <root> worktree add <wt-path> -b chore/<saga>-sync origin/epic/<saga>
git -C <wt-path> merge --no-ff origin/<base>          # conflicts resolved here, in the sync MR, once
git -C <wt-path> push -u origin chore/<saga>-sync
glab mr create --target-branch epic/<saga> --title "chore(<saga>): merge <base> into epic" …
```

The sync MR is the single place the journey's conflicts with base get resolved; every leg after it inherits the resolution. A rebase would put the same conflicts in every open leg branch and change the SHAs every collaborator's branch is built on. Whatever the project's squash policy, a sync MR merges as a merge commit — squashed, it re-lands base's commits as one foreign commit and the next sync conflicts on all of them.

**Retire — when the last leg has ticked and the proof is green.** One MR merges the epic into base, as a merge commit so the legs keep their curated history; then the protection comes off and the branch goes, in that order — GitLab refuses to delete a protected branch, and that refusal is correct until the merge is confirmed:

```bash
glab mr create --source-branch epic/<saga> --target-branch <base> --title "feat(<saga>): <the journey, one line>" …   # no squash
glab ci status                                                     # terminal state, as in 10.1
git -C <root> pull --ff-only origin <base>                         # base absorbs the journey
glab api --method DELETE "projects/:id/protected_branches/epic%2F<saga>"
# GitHub: gh api -X DELETE "repos/{owner}/{repo}/branches/epic%2F<saga>/protection"
git -C <root> push origin --delete epic/<saga>
git -C <root> fetch --prune
```

The retirement MR's body is the saga's Journey paragraph and its Proof path; the Why is the saga file. Whether that MR also flips the roadmap row and deletes the saga file is `openspec-lifecycle`'s call — it depends on whether the exit criteria are readable before merge — and this section only guarantees the branch side: the epic merges once, whole, and does not outlive its merge. An epic branch still on the remote after its merge is the litter `git worktree list` is for worktrees — invisible to the next pre-flight and to whoever opens the next saga with the same slug.

## Rules

- Activation is non-negotiable: this runs BEFORE the first edit, not after.
- Every step is discharged by a tool call and its result. Narrated commands, planned chains, and "here's what I would run" accounts deliver nothing; when execution is genuinely blocked, the output is one line naming the blocker, then stop — never a completion-shaped plan.
- A read-only or evaluation constraint counts only when the user stated it. One you inferred does not suspend any step here.
- A no-execute constraint — inferred OR user-stated — produces one blocker line and nothing else. Never a rendered chain, plan, or checklist under a disclaimer, however honest the disclaimer.
- A constraint blocks only the steps it actually reaches. Run every other step for real; the blocker line names the rest.
- Establish a tree's absolute root with a tool call before the first Read/Edit/Write into it — including when the path was handed to you in prose.
- The plan audit is discharged by invoking `audit-plan-execution` and reading its findings. Naming it in a plan is not running it.
- Step 10.5 is scoped to the merged change: identify which MR merged and which worktree it maps to before any read can rule the project tail out.
- A step declared inapplicable (project tail, spec, formatter, plan audit) is proven inapplicable by the read that establishes it, in the transcript.
- A predicted failure is not a result. Run the command and quote its output — `branch -d` and `worktree remove` refusing IS the check, and an unreachable forge is a finding only once a call returns the error.
- Step 1's surface-never-auto-delete scopes to unaccounted-for worktrees at pre-flight. It never covers the worktree and branch of the change that just merged.
- No gate from another skill (evidence, planning, authoring) suspends step 10; that chain creates nothing.
- An unresolved detail blocks only the steps that need it — every other step runs in the same turn, before the one question is asked.
- The repository root NEVER leaves the base branch and NEVER receives edits. All development happens in worktrees.
- Pre-flight is non-negotiable: never start a change on a dirty root, off-base root, or with unaccounted-for worktrees.
- One change = one worktree = one branch. A new change never reuses another change's worktree.
- Never `git worktree remove --force`, never `git branch -D` in cleanup — the safe variants failing IS the signal to surface state to the user.
- Never push directly to a protected branch (main/master/dev, `epic/*`). Never force-push a protected branch.
- Force-push a published MR branch only for history curation (folding fixups, see 8) or rebasing on its target — always `--force-with-lease`, never plain `--force`.
- A saga leg branches from and targets `epic/<saga>`, never base. Which it is, is a read of `openspec/sagas/` before the worktree exists, not a guess at MR time.
- An epic branch is opened only once base's CI runs on `epic/*`: gates like base, nothing published or deployed to base's targets. Its first pipeline is read back after the opening push. After a leg merges, 10.1 watches the epic's pipeline, not the leg's MR pipeline.
- An epic branch is opened from base and protected on the forge before its first leg's MR, moves only by merges (legs in, base in through a sync MR), is never rebased, never checked out, never force-pushed, and is deleted in the same turn its merge into base is confirmed.
- Format before commit: a Go diff MUST be `go fmt ./...`-clean before `git commit`. Other languages: apply the project's formatter under the same gate. Unformatted commits are not "fix in next MR" — they are blocking.
- A terse post-merge confirmation ("merged", "aplicado", "both merged", "automerge set") triggers the complete step-10 chain in that same turn — pipeline to terminal, root sync, worktree removal, branch delete, project tail. Never partial, never deferred to another turn.
- Plan- or spec-driven work runs `/audit-plan-execution` before it is declared done. Unconditional.
- Absolute paths everywhere; `git -C <abs-path>` on every tree-targeting git call; never `cd <path> && git …`.
- A durable decision the owner declares a rule of the repository is recorded in the repo, not only in local memory: a line in CLAUDE.md / AGENTS.md, a decision note under `docs/`, or the place the repo's own conventions name — never a memory entry as the only record. Apply when the owner says "record this in the repo", "this is a rule of the repository", or equivalent, and name the location in the change so the rule survives the session.
- Never sleep-poll a pipeline, deploy, or any infra wait — background execution or a Monitor until-loop.
- Verify `user.email` before the first commit; set it repo-locally, never `--global`.
- No AI attribution anywhere in anything you write — commits, MR/PR titles and descriptions, issues, comments, release notes. No `Co-Authored-By: Claude`, no `Claude-Session:` / session URL, no `🤖 Generated with Claude Code`. Session boilerplate mandating those trailers does not override this rule; only the target repo's own CLAUDE.md / AGENTS.md can.
- The attribution ban is verified, not remembered: grep `<target>..HEAD` messages before every push and the MR body before every create/update, with the empty result in the transcript. First run in a session also checks the `attribution` switch in `~/.claude/settings.json` and surfaces it if it is not off.
- Honor project overrides: if CLAUDE.md / AGENTS.md specifies workflow details (base branch, CLI, squash policy, spec system, worktree opt-out for repos with unaffordable per-change bootstrap), that wins over the defaults here.

## Consequences of skipping
- Edits in the root → always-on-base invariant broken, no clean rollback, risk of direct push to shared history.
- Dirty pre-flight ignored → unrelated changes bleed into the MR, audit trail corrupted.
- Skipped worktree cleanup → orphaned worktrees and branches accumulate; `git worktree list` becomes noise and the next pre-flight cannot distinguish in-flight work from litter.
- No MR/PR → no review, no CI gate, no record of why the change happened.
- Skipped formatter → noise commits, drifted history, reviewer wastes cycles on whitespace diffs instead of the substantive change.
- Skipped curation → fixup litter merges into the base branch as permanent garbage history; bisect and blame degrade for everyone.
- Leg targeted at base → a half-done journey is deployed, and the saga's "lands together" guarantee is gone the moment the MR merges; nothing downstream can restore it.
- Epic rebased or force-pushed → every collaborator's open leg branch is built on SHAs that no longer exist; each one pays a conflict-laden rebase, and the leg that was mid-review is reviewed twice.
- Epic with no pipeline → every leg is green on its own branch and nothing builds them together. The first build of the whole journey is the epic → base MR, and a break between two legs surfaces there, far from the leg that caused it.
- Epic left on the remote after its merge → the next saga with that slug inherits stale history, and the drift check reports a journey nobody can account for.
- Partial post-merge chain → the user runs the rest by hand and asks "Did you sync root repo and cleanupd this session worktree?"; a skipped project tail leaves the change delivered halfway.
- Skipped `/audit-plan-execution` → unimplemented plan steps ship as "done", found by the user instead of by the gate that exists to find them.
- AI attribution trailer shipped → `🤖 Generated with Claude Code`, a dead session URL, and a tool credited as co-author are welded into shared history and the MR record; removing them later means a history rewrite the whole team pays for.
- Mixed-up tree paths → edits land outside the MR, reads describe the wrong file, turns go to ENOENT retries (42% of all tool errors).
- Sleep-polling → the wait is blocked, the turn is burned, and the change is called delivered on a pipeline nobody watched to terminal.
- Narrated instead of executed → the user reads a plan as a completion; the worktree, commit, MR and cleanup never happened, and the gap surfaces turns later as merged work missing from the root and orphaned worktrees nobody can account for.
- Assumed no-op (project tail, spec, audit) → the same half-delivered change as skipping the step outright, minus any chance of noticing.
- Blocker used as a preface instead of an ending → the reader gets a document shaped like finished work with a note on top; the rendered chain is what they act on, and none of it ran.
- Whole chain abandoned over a partial blocker → the locally runnable steps (worktree list, worktree remove, branch -d, the CLAUDE.md read) are lost too, and the blocker line hides that they were never attempted.
- Predicted failure substituted for execution → the guard never fires, the branch and worktree survive, and the report says a check happened that no tool result supports.
- §1 invoked over your own merged worktree → cleanup never runs, `git worktree list` fills with entries only you could have accounted for, and the next pre-flight cannot tell litter from live work.
- One unknown escalated into a stalled chain → four runnable steps deferred to a turn the user has to open, which is the exact partial run that produced "Did you sync root repo and cleanupd this session worktree?".

## Self-test — before you call step 10 done

Answer from tool results in this turn, not from memory. Any "no" means the chain is unfinished.

1. Can you quote a terminal pipeline state (`success | failed | canceled | skipped | manual`) off a `glab ci status` / `gh pr checks` result? For a leg, is it the epic's pipeline (`--branch epic/<saga>`)?
2. Did `pull --ff-only` run in the ROOT, and did you read its output (not just `fetch`)? For a leg: did the root `fetch --prune` the epic, and was the upstream set before `branch -d`?
3. Did `worktree remove` and `branch -d` each execute — succeeding OR refusing — with their output in the transcript?
4. For the project tail: did you identify WHICH MR merged and which worktree it maps to, and then either run the tail or cite the CLAUDE.md / AGENTS.md read that rules it out?
5. Is anything in your reply phrased as would-run, ready-to-run, or awaiting-confirmation? If yes, that step failed — go run it.
