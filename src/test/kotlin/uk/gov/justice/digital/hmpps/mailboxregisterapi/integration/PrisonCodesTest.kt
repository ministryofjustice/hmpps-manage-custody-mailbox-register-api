package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN

private const val BASE_URI: String = "/prison-codes"

@DisplayName("GET /prison-codes")
class PrisonCodesTest : IntegrationTestBase() {

  @Test
  fun `returns a list of prison codes and names`() {
    webTestClient.get()
      .uri(BASE_URI)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN)))
      .exchange()
      .expectStatus().isOk
      .expectBody().jsonPath("$.prisons").isEqualTo(
        PrisonCode.entries.associate { it.name to it.prisonName },
      )
  }
}
