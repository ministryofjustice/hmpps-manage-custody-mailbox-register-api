package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.omumailboxes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.OffenderManagementUnitMailboxRepository

private const val BASE_URI: String = "/offender-management-unit-mailboxes"

@DisplayName("POST /offender-management-unit-mailboxes")
class CreatingOmuMailboxesTest : IntegrationTestBase() {

  private lateinit var attributes: HashMap<String, String?>

  @Autowired
  lateinit var offenderManagementUnitMailboxRepository: OffenderManagementUnitMailboxRepository

  @BeforeEach
  fun setup() {
    attributes = HashMap<String, String?>().apply {
      put("name", "Mailbox Name")
      put("emailAddress", "omu@example.com")
      put("prisonCode", "LEI")
      put("role", "CVL")
    }
  }

  @Test
  fun `returns unauthorized when no token provided`() {
    webTestClient.post()
      .uri(BASE_URI)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `returns forbidden when no role provided`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation())
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `returns forbidden when providing incorrect auth role`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  @Sql("classpath:test_data/reset.sql")
  fun `returns bad request when providing incorrect OMU role`() {
    attributes["role"] = "WRONG"

    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.role").isEqualTo("must be one of CVL,HDC")

    assertThat(offenderManagementUnitMailboxRepository.count()).isZero
  }

  @ParameterizedTest
  @ValueSource(strings = ["prisonCode", "emailAddress"])
  fun `without the required fields mailboxes are not created`(nullFieldName: String) {
    val invalidAttributes = attributes.toMutableMap().apply {
      this[nullFieldName] = null
    }

    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(invalidAttributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.$nullFieldName").isEqualTo("must not be blank")

    assertThat(offenderManagementUnitMailboxRepository.count()).isZero
  }

  @Test
  fun `are created by submitting the correct details`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isCreated

    assertThat(offenderManagementUnitMailboxRepository.count()).isOne()

    offenderManagementUnitMailboxRepository.findAll().first().apply {
      assertThat(name).isEqualTo("Mailbox Name")
      assertThat(emailAddress).isEqualTo("omu@example.com")
      assertThat(prisonCode).isEqualTo("LEI")
      assertThat(role).isEqualTo("CVL")
    }
  }

}