package uk.gov.justice.digital.hmpps.mailboxregisterapi.audit

import java.util.UUID

interface AuditableEntity {
  fun auditableSubjectId(): UUID?
  fun auditableSubjectType(): String
  fun auditableFields(): Map<String, Any?>
}
