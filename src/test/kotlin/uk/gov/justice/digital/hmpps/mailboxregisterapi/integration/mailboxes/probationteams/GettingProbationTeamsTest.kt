package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.probationteams

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_MAILBOXES_RO
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeam

@Sql(
  "classpath:test_data/reset.sql",
  "classpath:test_data/some_ldu_mailboxes.sql",
  "classpath:test_data/some_probation_teams.sql",
)
@DisplayName("GET /probation-teams/:id")
class GettingProbationTeamsTest : IntegrationTestBase() {

  private val apiUrl = "/probation-teams/4478a4d9-b53f-4519-857c-1f85c5fc869a"

  @Test
  fun `return a single requested probation team`() {
    val result = webTestClient.get()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN, ROLE_MAILBOXES_RO)))
      .exchange()
      .expectStatus().isOk
      .expectBody(object : ParameterizedTypeReference<ProbationTeam>() {})
      .returnResult().responseBody

    assertThat(result).isNotNull()
    assertThat(result.emailAddress).isEqualTo("probation.team2@email.com")
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
}
