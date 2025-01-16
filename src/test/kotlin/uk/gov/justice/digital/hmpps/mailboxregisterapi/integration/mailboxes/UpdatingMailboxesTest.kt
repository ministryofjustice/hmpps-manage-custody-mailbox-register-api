package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailbox
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailboxService

private const val DUMMY_MAILBOX_ID = "8d044b2e-96b1-45ef-a2ce-cce9c6f6a0c2"
private const val BASE_URI: String = "/local-delivery-unit-mailboxes"

class UpdatingMailboxesTest {
  private lateinit var attributes: HashMap<String, String?>

  @Nested
  @DisplayName("PUT /local-delivery-unit-mailboxes/:id")
  inner class LduMailboxes : IntegrationTestBase() {
    @Autowired
    lateinit var localDeliveryUnitMailboxService: LocalDeliveryUnitMailboxService

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
      val newMailbox = LocalDeliveryUnitMailbox(
        unitCode = "UNIT_CODE",
        areaCode = "AREA_CODE",
        name = "Mailbox Name",
        emailAddress = "ldu@example.com",
        country = "England",
      )
      val mailboxId = localDeliveryUnitMailboxService.createMailbox(newMailbox).id
      assertThat(mailboxId).isNotNull

      if (mailboxId != null) {
        webTestClient.put()
          .uri("$BASE_URI/$mailboxId")
          .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
          .bodyValue(attributes)
          .exchange()
          .expectStatus().isOk

        localDeliveryUnitMailboxService.getMailboxById(mailboxId).apply {
          assertThat(unitCode).isEqualTo("UPDATED_UNIT_CODE")
          assertThat(areaCode).isEqualTo("UPDATED_AREA_CODE")
          assertThat(name).isEqualTo("Updated Mailbox Name")
          assertThat(emailAddress).isEqualTo("updated-ldu@example.com")
          assertThat(country).isEqualTo("Wales")
        }
      }
    }

    @ParameterizedTest
    @ValueSource(strings = ["unitCode", "areaCode", "emailAddress"])
    fun `without the required fields mailboxes are not updated`(nullFieldName: String) {
      val invalidAttributes = attributes.toMutableMap().apply {
        this[nullFieldName] = null
      }

      val newMailbox = LocalDeliveryUnitMailbox(
        unitCode = "UNIT_CODE",
        areaCode = "AREA_CODE",
        name = "Mailbox Name",
        emailAddress = "ldu@example.com",
        country = "England",
      )
      val mailboxId = localDeliveryUnitMailboxService.createMailbox(newMailbox).id
      assertThat(mailboxId).isNotNull

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
}
