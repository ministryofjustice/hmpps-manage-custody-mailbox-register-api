package audit

import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditableEntity
import java.util.UUID

class DummyAuditableEntity(
  val id: UUID = UUID.randomUUID(),
  val name: String,
  val email: String,
  val age: Int,
) : AuditableEntity {
  override fun auditableSubjectId(): UUID = id
  override fun auditableSubjectType(): String = "DummyAuditable"
  override fun auditableFields(): Map<String, Any?> = mapOf(
    "name" to name,
    "email" to email,
    "age" to age,
  )
}
