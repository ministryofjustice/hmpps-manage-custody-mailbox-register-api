package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.ldumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_USER
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLogEntryRepository
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxRepository

private const val BASE_URI: String = "/local-delivery-unit-mailboxes"

@Sql("classpath:test_data/reset.sql")
@DisplayName("POST /local-delivery-unit-mailboxes")
class CreatingLduMailboxesTest : IntegrationTestBase() {

  @Autowired
  lateinit var localDeliveryUnitMailboxes: LocalDeliveryUnitMailboxRepository

  @Autowired
  lateinit var auditLogEntries: AuditLogEntryRepository

  private lateinit var attributes: HashMap<String, String?>

  @BeforeEach
  fun setup() {
    attributes = HashMap<String, String?>().apply {
      put("unitCode", "UNIT_CODE")
      put("areaCode", "AREA_CODE")
      put("name", "Mailbox Name")
      put("emailAddress", "ldu@example.com")
      put("country", "England")
    }
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.post()
      .uri(BASE_URI)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation())
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `are created by submitting the correct details`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_USER)))
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isCreated

    Assertions.assertThat(localDeliveryUnitMailboxes.count()).isOne()

    localDeliveryUnitMailboxes.findAll().first().apply {
      Assertions.assertThat(unitCode).isEqualTo("UNIT_CODE")
      Assertions.assertThat(areaCode).isEqualTo("AREA_CODE")
      Assertions.assertThat(name).isEqualTo("Mailbox Name")
      Assertions.assertThat(emailAddress).isEqualTo("ldu@example.com")
      Assertions.assertThat(country).isEqualTo("England")
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["unitCode", "areaCode", "emailAddress"])
  fun `without the required fields mailboxes are not created`(nullFieldName: String) {
    val invalidAttributes = attributes.toMutableMap().apply {
      this[nullFieldName] = null
    }

    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_USER)))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(invalidAttributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.$nullFieldName").isEqualTo("must not be blank")

    Assertions.assertThat(localDeliveryUnitMailboxes.count()).isZero
  }

  @Test
  fun `creation is audit logged`() {
    webTestClient.post()
      .uri(BASE_URI)
      .headers(setAuthorisation(username = "mailboxUser", roles = listOf(ROLE_SYSTEM_USER)))
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isCreated

    val createdMailbox = localDeliveryUnitMailboxes.findAll().first()
    auditLogEntries.findAll().first().apply {
      Assertions.assertThat(subjectId).isEqualTo(createdMailbox.id)
      Assertions.assertThat(subjectType).isEqualTo("LocalDeliveryUnitMailbox")
      Assertions.assertThat(username).isEqualTo("mailboxUser")
      Assertions.assertThat(action).isEqualTo(AuditAction.CREATE)
    }
  }
}
