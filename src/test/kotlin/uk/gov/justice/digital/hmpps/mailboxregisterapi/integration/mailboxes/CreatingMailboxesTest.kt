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
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.LocalDeliveryUnitMailboxRepository

private const val BASE_URI: String = "/local-delivery-unit-mailboxes"

class CreatingMailboxesTest {
  private lateinit var attributes: HashMap<String, String?>

  @Nested
  @DisplayName("POST /local-delivery-unit-mailboxes")
  inner class LduMailboxes : IntegrationTestBase() {
    @Autowired
    lateinit var localDeliveryUnitMailboxes: LocalDeliveryUnitMailboxRepository

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
        .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
        .bodyValue(attributes)
        .exchange()
        .expectStatus().isCreated

      assertThat(localDeliveryUnitMailboxes.count()).isOne()

      localDeliveryUnitMailboxes.findAll().first().apply {
        assertThat(unitCode).isEqualTo("UNIT_CODE")
        assertThat(areaCode).isEqualTo("AREA_CODE")
        assertThat(name).isEqualTo("Mailbox Name")
        assertThat(emailAddress).isEqualTo("ldu@example.com")
        assertThat(country).isEqualTo("England")
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
        .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(invalidAttributes)
        .exchange()
        .expectStatus().isBadRequest

      assertThat(localDeliveryUnitMailboxes.count()).isZero
    }
  }
}
