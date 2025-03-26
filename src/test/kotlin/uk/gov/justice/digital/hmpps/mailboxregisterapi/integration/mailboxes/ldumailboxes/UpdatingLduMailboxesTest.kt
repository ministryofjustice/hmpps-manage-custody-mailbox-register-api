package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.ldumailboxes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailboxService
import java.util.*

@Sql(
  "classpath:test_data/reset.sql",
  "classpath:test_data/some_ldu_mailboxes.sql",
)
@DisplayName("PUT /local-delivery-unit-mailboxes/:id")
class UpdatingLduMailboxesTest : IntegrationTestBase() {

  @Autowired
  lateinit var localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService

  @Autowired
  lateinit var auditLog: AuditLog

  private var existingLocalDeliveryUnitMailboxId = UUID.fromString("e33358f0-bdf9-4db6-9313-ef2d71fc4043")
  private var apiUrl = "/local-delivery-unit-mailboxes/$existingLocalDeliveryUnitMailboxId"

  private var attributes = hashMapOf<String, Any?>(
    "unitCode" to "UPDATED_UNIT_CODE",
    "areaCode" to "UPDATED_AREA_CODE",
    "name" to "Updated Mailbox Name",
    "emailAddress" to "updated-ldu@example.com",
    "country" to "Wales",
  )

  @Test
  fun `should update the mailbox by submitting the correct details`() {
    webTestClient.put()
      .uri(apiUrl)
      .headers(
        setAuthorisation(
          roles = listOf(ROLE_SYSTEM_ADMIN),
          username = "dummy-username",
        ),
      )
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isOk

    val updatedLocalDeliveryUnitMailbox = localDeliveryUnitMailboxService.getMailboxById(existingLocalDeliveryUnitMailboxId)
    assertThat(updatedLocalDeliveryUnitMailbox).isNotNull

    updatedLocalDeliveryUnitMailbox.apply {
      assertThat(unitCode).isEqualTo("UPDATED_UNIT_CODE")
      assertThat(areaCode).isEqualTo("UPDATED_AREA_CODE")
      assertThat(name).isEqualTo("Updated Mailbox Name")
      assertThat(emailAddress).isEqualTo("updated-ldu@example.com")
      assertThat(country).isEqualTo("Wales")

      val auditLogEntries = auditLog.entriesRegarding(this)
      assertThat(auditLogEntries).hasSize(1)
      assertThat(auditLogEntries.first().action).isEqualTo(AuditAction.UPDATE)
      assertThat(auditLogEntries.first().username).isEqualTo("dummy-username")
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["unitCode", "areaCode", "emailAddress"])
  fun `without the required fields mailboxes are not updated`(nullFieldName: String) {
    attributes[nullFieldName] = null

    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN)))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(attributes)
      .exchange()
      .expectStatus().isBadRequest
      .expectBody().jsonPath("$.errors.$nullFieldName").isEqualTo("must not be blank")
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.put()
      .uri(apiUrl)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation())
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.put()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .bodyValue(attributes)
      .exchange()
      .expectStatus()
      .isForbidden
  }
}
