# Skill Registry — StayHub V2

Generated: 2026-04-17

## Project Context
- **Stack**: Java 21 + Spring Boot 4.0.4 + PostgreSQL + MapStruct + Lombok + JUnit 5
- **Architecture**: MVC layered (controllers / services / repositories / mappers / DTOs)
- **Testing**: JUnit 5 + MockMvc + Mockito — Strict TDD Mode ENABLED

## User Skills

| Skill | Triggers | Path |
|-------|----------|------|
| go-testing | Go tests, Bubbletea TUI testing | ~/.claude/skills/go-testing/SKILL.md |
| skill-creator | Creating new AI skills | ~/.claude/skills/skill-creator/SKILL.md |
| next-developer | Next.js 14/15, Bun, frontend | ~/.claude/skills/next-developer/SKILL.md |
| context7-mcp | Library docs, framework APIs | ~/.claude/skills/context7-mcp/SKILL.md |
| codebase-to-course | Interactive course from codebase | ~/.claude/skills/codebase-to-course/SKILL.md |
| branch-pr | PR creation workflow | ~/.claude/skills/branch-pr/SKILL.md |
| issue-creation | GitHub issue creation | ~/.claude/skills/issue-creation/SKILL.md |
| judgment-day | Adversarial code review | ~/.claude/skills/judgment-day/SKILL.md |

## Compact Rules

### Java / Spring Boot
- Use Lombok (`@Data`, `@Builder`, `@RequiredArgsConstructor`) — no manual getters/setters
- Use MapStruct for DTO ↔ entity mapping — never map manually in controllers/services
- Custom exceptions in `exception/` package — handle centrally via `GlobalExceptionHandler`
- DTOs named `*RequestDTO` / `*ResponseDTO` — never expose entities directly in API layer
- Services as interfaces + impl only when needed — single impl = no interface required
- Repositories extend `JpaRepository` — add custom `@Query` methods as needed

### Testing (Strict TDD Mode ENABLED)
- Write tests BEFORE implementation — no code without a failing test first
- Unit tests: JUnit 5 + Mockito — mock all external dependencies
- Integration tests: `@WebMvcTest` + MockMvc for controller layer
- Service tests: `@ExtendWith(MockitoExtension.class)` — pure unit tests
- Use AssertJ (`assertThat`) over JUnit assertions
- Test command: `mvn test`

### API / OpenAPI
- OpenAPI contract lives at `src/main/resources/static/openapi.yaml` — treat as source of truth
- All endpoints must match the contract exactly (paths, verbs, request/response schemas)
- Security: Bearer JWT — public endpoints annotated `security: []` in contract
- Paginated responses use Spring `Page<T>` — map to `PageMeta` schema format
