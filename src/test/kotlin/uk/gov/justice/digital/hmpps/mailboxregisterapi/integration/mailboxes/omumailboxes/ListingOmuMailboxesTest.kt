package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.omumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitMailbox

private const val BASE_URI: String = "/offender-management-unit-mailboxes"

@DisplayName("GET /offender-management-unit-mailboxes")
class ListingOmuMailboxesTest : IntegrationTestBase() {

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

  @Sql(
    "classpath:test_data/reset.sql",
    "classpath:test_data/some_omu_mailboxes.sql",
  )
  @Test
  fun `should return a list of existing mailboxes sorted by createdAt ASC`() {
    val mailboxes = webTestClient.get()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("MANAGE_CUSTODY_MAILBOX_REGISTER_ADMIN")))
      .exchange()
      .expectStatus().isOk
      .expectBody(object : ParameterizedTypeReference<List<OffenderManagementUnitMailbox>>() {})
      .returnResult().responseBody!!

    Assertions.assertThat(mailboxes).hasSize(2)
    Assertions.assertThat(mailboxes[0].name).isEqualTo("Test OMU Mailbox 1")
    Assertions.assertThat(mailboxes[1].name).isEqualTo("Test OMU Mailbox 2")
  }
}
