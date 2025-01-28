package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.omumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitMailbox
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitMailboxRepository
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitRole

private const val DUMMY_MAILBOX_ID = "8d044b2e-96b1-45ef-a2ce-cce9c6f6a0c2"
private const val BASE_URI: String = "/offender-management-unit-mailboxes"

@DisplayName("GET /offender-management-unit-mailboxes/:id")
class GettingOmuMailboxesTest : IntegrationTestBase() {

  @Autowired
  lateinit var offenderManagementUnitMailboxes: OffenderManagementUnitMailboxRepository

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.get()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.get()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.get()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return not found if mailbox does not exist`() {
    webTestClient.get()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Sql(
    "classpath:test_data/reset.sql",
    "classpath:test_data/some_omu_mailboxes.sql",
  )
  @Test
  fun `should return the mailbox details if it exists`() {
    val mailboxId = offenderManagementUnitMailboxes.findAll().first().id
    Assertions.assertThat(mailboxId).isNotNull

    val mailbox = webTestClient.get()
      .uri("$BASE_URI/$mailboxId")
      .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
      .exchange()
      .expectStatus()
      .isOk.expectBody(object : ParameterizedTypeReference<OffenderManagementUnitMailbox>() {})
      .returnResult().responseBody!!

    mailbox.apply {
      Assertions.assertThat(id).isEqualTo(mailboxId)
      Assertions.assertThat(name).isEqualTo("Test OMU Mailbox 1")
      Assertions.assertThat(emailAddress).isEqualTo("omu1@example.com")
      Assertions.assertThat(prisonCode).isEqualTo(PrisonCode.LEI)
      Assertions.assertThat(role).isEqualTo(OffenderManagementUnitRole.CVL)
      Assertions.assertThat(createdAt).isNotNull
      Assertions.assertThat(updatedAt).isNotNull
    }
  }
}
