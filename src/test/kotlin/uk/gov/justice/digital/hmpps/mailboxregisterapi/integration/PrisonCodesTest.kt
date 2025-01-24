package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode

private const val BASE_URI: String = "/prison-codes"

@DisplayName("GET /prison-codes")
class PrisonCodesTest : IntegrationTestBase() {

  @Test
  fun `returns a list of prison codes and names`() {
    webTestClient.get()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .exchange()
      .expectStatus().isOk
      .expectBody().jsonPath("$.prisons").isEqualTo(
        PrisonCode.entries.associate { it.name to it.prisonName },
      )
  }
}
