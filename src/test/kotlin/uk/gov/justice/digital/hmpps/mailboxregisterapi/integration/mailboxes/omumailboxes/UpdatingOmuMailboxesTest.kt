package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.omumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitMailboxForm
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitMailboxService
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitRole

private const val DUMMY_MAILBOX_ID = "8d044b2e-96b1-45ef-a2ce-cce9c6f6a0c2"
private const val BASE_URI: String = "/offender-management-unit-mailboxes"

@DisplayName("PUT /offender-management-unit-mailboxes/:id")
class UpdatingOmuMailboxesTest : IntegrationTestBase() {
  private lateinit var attributes: HashMap<String, String?>

  @Autowired
  lateinit var offenderManagementUnitMailboxService: OffenderManagementUnitMailboxService

  @BeforeEach
  fun setup() {
    attributes = HashMap<String, String?>().apply {
      put("name", "Updated OMU Mailbox Name")
      put("emailAddress", "updated-omu@example.com")
      put("prisonCode", PrisonCode.BFI.name)
      put("role", OffenderManagementUnitRole.HDC.name)
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
    val newMailbox = OffenderManagementUnitMailboxForm(
      name = "OMU Mailbox Name",
      emailAddress = "omu@example.com",
      prisonCode = PrisonCode.LEI,
      role = OffenderManagementUnitRole.CVL,
    )
    val mailboxId = offenderManagementUnitMailboxService.createMailbox(newMailbox).id
    Assertions.assertThat(mailboxId).isNotNull

    if (mailboxId != null) {
      webTestClient.put()
        .uri("$BASE_URI/$mailboxId")
        .headers(setAuthorisation(roles = listOf("MAILBOX_REGISTER_ADMIN")))
        .bodyValue(attributes)
        .exchange()
        .expectStatus().isOk

      offenderManagementUnitMailboxService.getMailboxById(mailboxId).apply {
        Assertions.assertThat(name).isEqualTo(attributes["name"])
        Assertions.assertThat(emailAddress).isEqualTo(attributes["emailAddress"])
        Assertions.assertThat(prisonCode.toString()).isEqualTo(attributes["prisonCode"])
        Assertions.assertThat(role.toString()).isEqualTo(attributes["role"])
      }
    }
  }

  @ParameterizedTest
  @ValueSource(strings = ["role", "prisonCode", "emailAddress"])
  fun `without the required fields mailboxes are not updated`(nullFieldName: String) {
    val invalidAttributes = attributes.toMutableMap().apply {
      this[nullFieldName] = null
    }

    val newMailbox = OffenderManagementUnitMailboxForm(
      name = "OMU Mailbox Name",
      emailAddress = "omu@example.com",
      prisonCode = PrisonCode.LEI,
      role = OffenderManagementUnitRole.CVL,
    )
    val mailboxId = offenderManagementUnitMailboxService.createMailbox(newMailbox).id
    Assertions.assertThat(mailboxId).isNotNull

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
