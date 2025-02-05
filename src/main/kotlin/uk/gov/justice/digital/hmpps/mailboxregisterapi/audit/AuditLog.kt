package uk.gov.justice.digital.hmpps.mailboxregisterapi.audit

import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuditLog(
  private val auditLogEntryRepository: AuditLogEntryRepository,
) {
  @Transactional
  fun logCreationOf(auditableEntity: AuditableEntity, givenUsername: String? = null): AuditLogEntry {
    return auditLogEntryRepository.saveAndFlush(
      AuditLogEntry(
        action = AuditAction.CREATE,
        subjectId = auditableEntity.auditableSubjectId(),
        subjectType = auditableEntity.auditableSubjectType(),
        username = username(givenUsername),
        updates = differenceBetween(null, auditableEntity),
      ),
    )
  }

  @Transactional
  fun logUpdatesTo(
    initial: AuditableEntity,
    updated: AuditableEntity,
    givenUsername: String? = null,
  ): AuditableEntity {
    auditLogEntryRepository.saveAndFlush(
      AuditLogEntry(
        action = AuditAction.UPDATE,
        subjectId = initial.auditableSubjectId(),
        subjectType = initial.auditableSubjectType(),
        username = username(givenUsername),
        updates = differenceBetween(initial, updated),
      ),
    )
    return updated
  }

  @Transactional
  fun logDeletionOf(auditableEntity: AuditableEntity, givenUsername: String? = null) {
    auditLogEntryRepository.saveAndFlush(
      AuditLogEntry(
        action = AuditAction.DELETE,
        subjectId = auditableEntity.auditableSubjectId(),
        subjectType = auditableEntity.auditableSubjectType(),
        username = username(givenUsername),
      ),
    )
  }

  fun entriesRegarding(auditableEntity: AuditableEntity): List<AuditLogEntry> {
    return auditLogEntryRepository.getAllBySubjectIdAndSubjectType(
      auditableEntity.auditableSubjectId()!!,
      auditableEntity.auditableSubjectType(),
    )
  }

  private fun differenceBetween(a: AuditableEntity? = null, b: AuditableEntity): Map<String, Any> {
    val aChanges = a?.auditableFields() ?: emptyMap()
    val bChanges = b.auditableFields()

    return bChanges.keys
      .associateByTo(mutableMapOf(), { it }, { changedPair(aChanges[it], bChanges[it]) })
      .filterValues { it.isNotEmpty() }
  }

  private fun changedPair(a: Any?, b: Any?): List<Any?> =
    if (a == b) emptyList() else listOf(a, b)

  private fun username(givenUsername: String? = null) =
    givenUsername ?: SecurityContextHolder.getContext().authentication.principal as String
}
