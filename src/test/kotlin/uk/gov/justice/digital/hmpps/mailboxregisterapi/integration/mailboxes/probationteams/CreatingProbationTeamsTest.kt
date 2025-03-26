package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.probationteams

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeamsRepository
import java.util.UUID

@Sql(
  "classpath:test_data/reset.sql",
  "classpath:test_data/some_ldu_mailboxes.sql",
)
@DisplayName("POST /probation-teams")
class CreatingProbationTeamsTest : IntegrationTestBase() {

  private var baseUrl = "/probation-teams"

  @Autowired
  private lateinit var probationTeamsRepository: ProbationTeamsRepository

  @Autowired
  private lateinit var auditLog: AuditLog

  private var existingLocalDeliveryUnitMailboxId = UUID.fromString("03173d0f-aa89-4750-a1d4-9c00ef9796b3")

  private var attributes: HashMap<String, Any?> = hashMapOf(
    "emailAddress" to "pt1@example.com",
    "teamCode" to "ABC",
    "localDeliveryUnitMailboxId" to existingLocalDeliveryUnitMailboxId,
  )

  @Test
  fun `Creating a probation team`() {
    webTestClient.post()
      .uri(baseUrl)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN), username = "dummy-username"))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isCreated

    val createdProbationTeams = probationTeamsRepository.findAll()
    assertThat(createdProbationTeams.count()).isOne()
    val createdProbationTeam = createdProbationTeams.first()
    assertThat(createdProbationTeam).isNotNull
    assertThat(createdProbationTeam.emailAddress).isEqualTo("pt1@example.com")
    assertThat(createdProbationTeam.teamCode).isEqualTo("ABC")
    assertThat(createdProbationTeam.localDeliveryUnitMailbox?.id).isEqualTo(existingLocalDeliveryUnitMailboxId)

    val auditLogEntries = auditLog.entriesRegarding(createdProbationTeam)
    assertThat(auditLogEntries).hasSize(1)
    assertThat(auditLogEntries.first().username).isEqualTo("dummy-username")
    assertThat(auditLogEntries.first().action).isEqualTo(AuditAction.CREATE)
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
  fun `Cannot create probation teams without all the required fields`(fieldName: String, fieldValue: Any?, expectedValidationMessage: String) {
    attributes[fieldName] = fieldValue

    webTestClient.post()
      .uri(baseUrl)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN)))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.$fieldName").isEqualTo(expectedValidationMessage)

    assertThat(probationTeamsRepository.count()).isZero()
  }

  @Test
  fun `returns unauthorized when no token provided`() {
    webTestClient.post()
      .uri(baseUrl)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `returns forbidden when no role provided`() {
    webTestClient.post()
      .uri(baseUrl)
      .headers(setAuthorisation())
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `returns forbidden when providing incorrect auth role`() {
    webTestClient.post()
      .uri(baseUrl)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }
}
