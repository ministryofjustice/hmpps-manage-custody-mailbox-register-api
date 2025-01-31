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

class AuditLoggingUpdatingTest {

  private var savedEntry: AuditLogEntry? = null
  private val auditLogEntryRepository = mock<AuditLogEntryRepository>()

  private val auditableEntity = DummyAuditableEntity(
    name = "Mr Goodname",
    email = "goodemail@example.com",
    age = 35,
  )
  private val updatedAditableEntity = DummyAuditableEntity(
    name = "Mr Goodname",
    email = "good.email@example.com",
    age = 37,
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
  fun `Logs update actions as UPDATE`() {
    AuditLog(auditLogEntryRepository).logUpdatesTo(auditableEntity, updatedAditableEntity)
    assertThat(savedEntry?.action).isEqualTo(AuditAction.UPDATE)
  }

  @Test
  fun `Update actions have each the current and previous values of only the auditable fields which have changed`() {
    AuditLog(auditLogEntryRepository).logUpdatesTo(auditableEntity, updatedAditableEntity)

    assertThat(savedEntry?.updates).isEqualTo(
      mapOf(
        "email" to listOf("goodemail@example.com", "good.email@example.com"),
        "age" to listOf(35, 37),
      ),
    )
  }

  @Test
  fun `username by default comes from the the SecurityContext`() {
    AuditLog(auditLogEntryRepository).logUpdatesTo(auditableEntity, updatedAditableEntity)
    assertThat(savedEntry?.username).isEqualTo("A_LOGGED_IN_USER")
  }

  @Test
  fun `username can be overridden`() {
    AuditLog(auditLogEntryRepository).logUpdatesTo(auditableEntity, updatedAditableEntity, "A_GIVEN_USERNAME")
    assertThat(savedEntry?.username).isEqualTo("A_GIVEN_USERNAME")
  }
}
