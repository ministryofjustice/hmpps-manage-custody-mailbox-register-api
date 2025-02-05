package uk.gov.justice.digital.hmpps.mailboxregisterapi.audit

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AuditLogEntryRepository : JpaRepository<AuditLogEntry, UUID> {
  fun findBySubjectId(subjectId: UUID): AuditLogEntry?
  fun getAllBySubjectIdAndSubjectType(subjectId: UUID, subjectType: String): List<AuditLogEntry>
}
