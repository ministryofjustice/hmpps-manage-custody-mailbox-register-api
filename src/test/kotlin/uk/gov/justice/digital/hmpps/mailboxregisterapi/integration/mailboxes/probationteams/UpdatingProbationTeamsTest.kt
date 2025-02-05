package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.probationteams

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeamsRepository
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.UUID

@Sql(
  "classpath:test_data/reset.sql",
  "classpath:test_data/some_ldu_mailboxes.sql",
  "classpath:test_data/some_probation_teams.sql",
)
@DisplayName("PUT /probation-teams/:id")
class UpdatingProbationTeamsTest : IntegrationTestBase() {

  @Autowired
  private lateinit var probationTeamsRepository: ProbationTeamsRepository

  @Autowired
  private lateinit var auditLog: AuditLog

  // From the test_data/some_ldu_mailboxes.sql seeds
  private var existingLocalDeliveryUnitMailboxId = UUID.fromString("03173d0f-aa89-4750-a1d4-9c00ef9796b3")
  private var changeToLocalDeliveryUnitMailboxId = UUID.fromString("e33358f0-bdf9-4db6-9313-ef2d71fc4043")

  // From the test_data/some_probation_teams.sql seeds
  private var existingProbationTeamId = UUID.fromString("4478a4d9-b53f-4519-857c-1f85c5fc869a")
  private var existingProbationTeamUpdatedAt = LocalDateTime
    .parse("2025-01-01 12:30:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    .atOffset(ZoneOffset.UTC)

  private var apiUrl = "/probation-teams/$existingProbationTeamId"
  private var attributes: HashMap<String, Any?> = hashMapOf(
    "emailAddress" to "pt1@example.com",
    "teamCode" to "TeamCode123",
    "localDeliveryUnitMailboxId" to changeToLocalDeliveryUnitMailboxId,
  )

  @Test
  fun `Updating a probation team`() {
    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN"), username = "dummy-username"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isOk

    val updatedProbationTeam = probationTeamsRepository.findById(existingProbationTeamId).get()
    assertThat(updatedProbationTeam.emailAddress).isEqualTo("pt1@example.com")
    assertThat(updatedProbationTeam.teamCode).isEqualTo("TeamCode123")
    assertThat(updatedProbationTeam.localDeliveryUnitMailbox?.id).isEqualTo(changeToLocalDeliveryUnitMailboxId)
    assertThat(updatedProbationTeam.updatedAt).isAfter(existingProbationTeamUpdatedAt)

    val auditLogEntries = auditLog.entriesRegarding(updatedProbationTeam)
    assertThat(auditLogEntries).hasSize(1)
    assertThat(auditLogEntries.first().username).isEqualTo("dummy-username")
    assertThat(auditLogEntries.first().action).isEqualTo(AuditAction.UPDATE)
  }

  @ParameterizedTest
  @CsvSource(
    delimiter = '|',
    textBlock = """
      teamCode                   |                                      | must not be blank
      emailAddress               |                                      | must not be blank
      emailAddress               | notValid                             | must be a well-formed email address
      localDeliveryUnitMailboxId |                                      | must not be blank
      localDeliveryUnitMailboxId | 56f6fcc6-5bd1-4a37-b0fd-9ece7bd9a8c4 | must be a valid LDU""",
  )
  fun `Cannot update probation teams without all the required fields`(fieldName: String, fieldValue: Any?, expectedValidationMessage: String) {
    attributes[fieldName] = fieldValue

    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.$fieldName").isEqualTo(expectedValidationMessage)

    val updatedProbationTeam = probationTeamsRepository.findById(existingProbationTeamId).get()
    assertThat(updatedProbationTeam).isNotNull
    assertThat(updatedProbationTeam.emailAddress).isEqualTo("probation.team2@email.com")
    assertThat(updatedProbationTeam.teamCode).isEqualTo("ABC")
    assertThat(updatedProbationTeam.localDeliveryUnitMailbox?.id).isEqualTo(existingLocalDeliveryUnitMailboxId)
    assertThat(updatedProbationTeam.updatedAt).isEqualTo(existingProbationTeamUpdatedAt)

    val auditLogEntries = auditLog.entriesRegarding(updatedProbationTeam)
    assertThat(auditLogEntries).hasSize(0)
  }

  @Test
  fun `returns unauthorized when no token provided`() {
    webTestClient.put()
      .uri(apiUrl)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `returns forbidden when no role provided`() {
    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation())
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `returns forbidden when providing incorrect auth role`() {
    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }
}
