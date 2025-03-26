package uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.mailboxes.omumailboxes

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import uk.gov.justice.digital.hmpps.mailboxregisterapi.ROLE_SYSTEM_ADMIN
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLogEntryRepository
import uk.gov.justice.digital.hmpps.mailboxregisterapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits.OffenderManagementUnitMailboxRepository

private const val DUMMY_MAILBOX_ID = "8d044b2e-96b1-45ef-a2ce-cce9c6f6a0c2"
private const val BASE_URI: String = "/offender-management-unit-mailboxes"

@DisplayName("DELETE /offender-management-unit-mailboxes/:id")
class DeletingOmuMailboxesTest : IntegrationTestBase() {

  @Autowired
  lateinit var offenderManagementUnitMailboxes: OffenderManagementUnitMailboxRepository

  @Autowired
  lateinit var auditLogEntryRepository: AuditLogEntryRepository

  @Test
  fun `should return unauthorized if no token`() {
    webTestClient.delete()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .exchange()
      .expectStatus()
      .isUnauthorized
  }

  @Test
  fun `should return forbidden if no role`() {
    webTestClient.delete()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return forbidden if wrong role`() {
    webTestClient.delete()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation(roles = listOf("ROLE_WRONG")))
      .exchange()
      .expectStatus()
      .isForbidden
  }

  @Test
  fun `should return not found if mailbox does not exist`() {
    webTestClient.delete()
      .uri("$BASE_URI/$DUMMY_MAILBOX_ID")
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN)))
      .exchange()
      .expectStatus()
      .isNotFound
  }

  @Sql(
    "classpath:test_data/reset.sql",
    "classpath:test_data/some_omu_mailboxes.sql",
  )
  @Test
  fun `should delete the mailbox if it exists`() {
    val mailboxId = offenderManagementUnitMailboxes.findAll().first().id!!
    Assertions.assertThat(mailboxId).isNotNull

    webTestClient.delete()
      .uri("$BASE_URI/$mailboxId")
      .headers(setAuthorisation(roles = listOf(ROLE_SYSTEM_ADMIN)))
      .exchange()
      .expectStatus()
      .isOk

    Assertions.assertThat(offenderManagementUnitMailboxes.findById(mailboxId)).isEmpty

    val auditLogEntry = auditLogEntryRepository.findBySubjectId(mailboxId)
    Assertions.assertThat(auditLogEntry).isNotNull
    auditLogEntry?.apply {
      Assertions.assertThat(subjectType).isEqualTo("OffenderManagementUnitMailbox")
      Assertions.assertThat(action).isEqualTo(AuditAction.DELETE)
    }
  }
}
