package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.probationteams

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxForm
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxService
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeamsRepository
import java.util.UUID

@Sql("classpath:test_data/reset.sql")
@DisplayName("POST /probation-teams")
class CreatingProbationTeamsTest : IntegrationTestBase() {

  private var baseUrl = "/probation-teams"

  @Autowired
  private lateinit var probationTeamsRepository: ProbationTeamsRepository

  @Autowired
  private lateinit var localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService

  private var existingLocalDeliveryUnitMailboxId: UUID? = null

  private var attributes: HashMap<String, Any?> = hashMapOf(
    "emailAddress" to "pt1@example.com",
    "teamCode" to "ABC",
    "localDeliveryUnitMailboxId" to null,
  )

  @BeforeEach
  fun setup() {
    existingLocalDeliveryUnitMailboxId =
      localDeliveryUnitMailboxService.createMailbox(
        LocalDeliveryUnitMailboxForm(
          emailAddress = "ldu@email.com",
          unitCode = "UnitCode",
          areaCode = "AreaCode",
          name = "Existing LDU",
        ),
      ).id

    attributes["localDeliveryUnitMailboxId"] = existingLocalDeliveryUnitMailboxId
  }

  @Test
  fun `Creating a probation team`() {
    webTestClient.post()
      .uri(baseUrl)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isCreated

    assertThat(probationTeamsRepository.count()).isOne()
    assertThat(probationTeamsRepository.findByEmailAddressIgnoreCase(attributes["emailAddress"].toString())).isNotNull
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
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
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
