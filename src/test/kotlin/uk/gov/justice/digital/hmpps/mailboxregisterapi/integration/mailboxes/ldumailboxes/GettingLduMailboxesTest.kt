package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.ldumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_MAILBOXES_RO
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_USER
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailbox
import java.util.*

@DisplayName("GET /local-delivery-unit-mailboxes/:id")
class GettingLduMailboxesTest : IntegrationTestBase() {

  private val localDeliveryUnitMailboxId = UUID.fromString("e33358f0-bdf9-4db6-9313-ef2d71fc4043")
  private val apiUrl = "/local-delivery-unit-mailboxes/$localDeliveryUnitMailboxId"

  @Sql(
    "classpath:test_data/reset.sql",
    "classpath:test_data/some_ldu_mailboxes.sql",
  )
  @Test
  fun `should return the mailbox details if it exists`() {
    val mailbox = webTestClient.get()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_USER, ROLE_MAILBOXES_RO)))
      .exchange()
      .expectStatus()
      .isOk.expectBody(object : ParameterizedTypeReference<LocalDeliveryUnitMailbox>() {})
      .returnResult().responseBody!!

    Assertions.assertThat(mailbox).isNotNull
    mailbox.apply {
      Assertions.assertThat(id).isEqualTo(localDeliveryUnitMailboxId)
      Assertions.assertThat(unitCode).isEqualTo("UNIT_CODE_1")
      Assertions.assertThat(areaCode).isEqualTo("AREA_CODE_1")
      Assertions.assertThat(name).isEqualTo("Test Mailbox 1")
      Assertions.assertThat(emailAddress).isEqualTo("ldu1@example.com")
      Assertions.assertThat(country).isEqualTo("England")
      Assertions.assertThat(createdAt).isNotNull
      Assertions.assertThat(updatedAt).isNotNull
    }
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri(apiUrl)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri(apiUrl)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return not found if mailbox does not exist`() {
    webTestClient.get()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_USER, ROLE_MAILBOXES_RO)))
      .exchange()
      .expectStatus()
      .isNotFound
  }
}
