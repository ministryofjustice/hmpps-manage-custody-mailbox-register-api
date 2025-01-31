package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.ldumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLogEntryRepository
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxForm
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxService

private const val DUMMY_MAILBOX_ID = "8d044b2e-96b1-45ef-a2ce-cce9c6f6a0c2"
private const val BASE_URI: String = "/local-delivery-unit-mailboxes"

@DisplayName("PUT /local-delivery-unit-mailboxes/:id")
class UpdatingLduMailboxesTest : IntegrationTestBase() {
  private lateinit var attributes: HashMap<String, String?>

  @Autowired
  lateinit var localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService

  @Autowired
  lateinit var auditLogEntryRepository: AuditLogEntryRepository

  @BeforeEach
  fun setup() {
    attributes = HashMap<String, String?>().apply {
      put("unitCode", "UPDATED_UNIT_CODE")
      put("areaCode", "UPDATED_AREA_CODE")
      put("name", "Updated Mailbox Name")
      put("emailAddress", "updated-ldu@example.com")
      put("country", "Wales")
    }
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.put()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.put()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation())
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.put()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should update the mailbox by submitting the correct details`() {
    val newMailbox = LocalDeliveryUnitMailboxForm(
      unitCode = "UNIT_CODE",
      areaCode = "AREA_CODE",
      name = "Mailbox Name",
      emailAddress = "ldu@example.com",
      country = "England",
    )
    val mailboxId = localDeliveryUnitMailboxService.createMailbox(newMailbox).id
    Assertions.assertThat(mailboxId).isNotNull

    if (mailboxId != null) {
      webTestClient.put()
        .uri("$BASE_URI/$mailboxId")
        .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
        .bodyValue(attributes)
        .exchange()
        .expectStatus().isOk

      localDeliveryUnitMailboxService.getMailboxById(mailboxId).apply {
        Assertions.assertThat(unitCode).isEqualTo("UPDATED_UNIT_CODE")
        Assertions.assertThat(areaCode).isEqualTo("UPDATED_AREA_CODE")
        Assertions.assertThat(name).isEqualTo("Updated Mailbox Name")
        Assertions.assertThat(emailAddress).isEqualTo("updated-ldu@example.com")
        Assertions.assertThat(country).isEqualTo("Wales")
      }

      val auditLogEntry = auditLogEntryRepository.findBySubjectId(mailboxId)
      Assertions.assertThat(auditLogEntry).isNotNull
      auditLogEntry?.apply {
        Assertions.assertThat(subjectType).isEqualTo("LocalDeliveryUnitMailbox")
        Assertions.assertThat(action).isEqualTo(AuditAction.UPDATE)
      }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["unitCode", "areaCode", "emailAddress"])
  fun `without the required fields mailboxes are not updated`(nullFieldName: String) {
    val invalidAttributes = attributes.toMutableMap().apply {
      this[nullFieldName] = null
    }

    val newMailbox = LocalDeliveryUnitMailboxForm(
      unitCode = "UNIT_CODE",
      areaCode = "AREA_CODE",
      name = "Mailbox Name",
      emailAddress = "ldu@example.com",
      country = "England",
    )
    val mailboxId = localDeliveryUnitMailboxService.createMailbox(newMailbox).id
    Assertions.assertThat(mailboxId).isNotNull

    webTestClient.put()
      .uri("$BASE_URI/$mailboxId")
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(invalidAttributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.$nullFieldName").isEqualTo("must not be blank")
  }
}
