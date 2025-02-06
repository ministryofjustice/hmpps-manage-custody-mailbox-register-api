package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.probationteams

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.reactive.server.expectBodyList
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams.ProbationTeam

@Sql(
  "classpath:test_data/reset.sql",
  "classpath:test_data/some_ldu_mailboxes.sql",
  "classpath:test_data/some_probation_teams.sql",
)
@DisplayName("GET /probation-teams")
class ListingProbationTeamsTest : IntegrationTestBase() {

  private val apiUrl = "/probation-teams"

  @Test
  fun `returns all the probation teams`() {
    val results = webTestClient.get()
      .uri(apiUrl)
      .headers(setAuthorisation(roles = listOf("MANAGE_CUSTODY_MAILBOX_REGISTER_ADMIN")))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk
      .expectBodyList<ProbationTeam>()
      .returnResult().responseBody

    assertThat(results.size).isEqualTo(2)
    assertThat(results[0].emailAddress).isEqualTo("probation.team1@email.com")
    assertThat(results[1].emailAddress).isEqualTo("probation.team2@email.com")
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
