package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.probationteams

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeam
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeamsService
import java.util.*

@Sql(
  "classpath:test_data/reset.sql",
  "classpath:test_data/some_ldu_mailboxes.sql",
  "classpath:test_data/some_probation_teams.sql",
)
@DisplayName("GET /probation-teams/:id")
class DeletingProbationTeamsTest : IntegrationTestBase() {

  @Autowired
  private lateinit var probationTeamsService: ProbationTeamsService

  @Autowired
  private lateinit var auditLog: AuditLog

  private val probationTeamIdToDelete = UUID.fromString("cdfce37a-9db3-4786-be9e-38b3d05bbf4b")
  private val apiUrl = "/probation-teams/$probationTeamIdToDelete"

  @Test
  fun `deletes the probation team with the given id`() {
    webTestClient.delete()
      .uri(apiUrl)
      .headers(
        setAuthorisation(
          roles = listOf(ROLE_SYSTEM_ADMIN),
          username = "dummy-username",
        ),
      )
      .exchange()
      .expectStatus().isOk

    assertThat(probationTeamsService.runCatching { byId(probationTeamIdToDelete) }.getOrNull()).isNull()

    val auditLogEntries = auditLog.entriesRegarding(ProbationTeam(id = probationTeamIdToDelete))
    assertThat(auditLogEntries.count()).isEqualTo(1)
    assertThat(auditLogEntries.first().subjectId).isEqualTo(probationTeamIdToDelete)
    assertThat(auditLogEntries.first().action).isEqualTo(AuditAction.DELETE)
    assertThat(auditLogEntries.first().username).isEqualTo("dummy-username")
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.delete()
      .uri(apiUrl)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.delete()
      .uri(apiUrl)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.delete()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }
}
