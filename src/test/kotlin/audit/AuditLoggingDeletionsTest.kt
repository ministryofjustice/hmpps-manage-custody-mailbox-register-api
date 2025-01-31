package audit

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditAction
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLog
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLogEntry
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditLogEntryRepository

class AuditLoggingDeletionsTest {

  private var savedEntry: AuditLogEntry? = null
  private val auditLogEntryRepository = mock<AuditLogEntryRepository>()

  private val auditableEntity = DummyAuditableEntity(
    name = "Mr Goodname",
    email = "goodemail@example.com",
    age = 35,
  )

  @BeforeEach
  fun setup() {
    SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("A_LOGGED_IN_USER", "pw")
    Mockito.`when`(auditLogEntryRepository.saveAndFlush(any<AuditLogEntry>()))
      .thenAnswer { invocation ->
        savedEntry = invocation.arguments[0] as AuditLogEntry
        savedEntry
      }
  }

  @Test
  fun `Logs delete actions as DELETE`() {
    AuditLog(auditLogEntryRepository).logDeletionOf(auditableEntity)
    assertThat(savedEntry?.action).isEqualTo(AuditAction.DELETE)
  }

  @Test
  fun `username by default comes from the the SecurityContext`() {
    AuditLog(auditLogEntryRepository).logDeletionOf(auditableEntity)
    assertThat(savedEntry?.username).isEqualTo("A_LOGGED_IN_USER")
  }

  @Test
  fun `username can be overridden`() {
    AuditLog(auditLogEntryRepository).logDeletionOf(auditableEntity, "A_GIVEN_USERNAME")
    assertThat(savedEntry?.username).isEqualTo("A_GIVEN_USERNAME")
  }

  @Test
  fun `updates column is left empty`() {
    AuditLog(auditLogEntryRepository).logDeletionOf(auditableEntity)
    assertThat(savedEntry?.updates).isNull()
  }
}
