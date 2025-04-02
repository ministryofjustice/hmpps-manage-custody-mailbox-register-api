package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.ldumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_MAILBOXES_RO
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailbox

private const val BASE_URI: String = "/local-delivery-unit-mailboxes"

@DisplayName("GET /local-delivery-unit-mailboxes")
class ListingLduMailboxesTest : IntegrationTestBase() {

  @Sql(
    "classpath:test_data/reset.sql",
    "classpath:test_data/some_ldu_mailboxes.sql",
  )
  @Test
  fun `should return a list of existing mailboxes sorted by createdAt ASC`() {
    val mailboxes = webTestClient.get()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN, ROLE_MAILBOXES_RO)))
      .exchange()
      .expectStatus().isOk
      .expectBody(object : ParameterizedTypeReference<List<LocalDeliveryUnitMailbox>>() {})
      .returnResult().responseBody!!

    Assertions.assertThat(mailboxes).hasSize(2)
    Assertions.assertThat(mailboxes[0].unitCode).isEqualTo("CODE1")
    Assertions.assertThat(mailboxes[1].unitCode).isEqualTo("CODE2")
  }

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri(BASE_URI)
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri(BASE_URI)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }
}
