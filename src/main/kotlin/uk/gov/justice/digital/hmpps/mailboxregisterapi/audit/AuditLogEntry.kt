package uk.gov.justice.digital.hmpps.mailboxregisterapi.audit

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.*

enum class AuditAction {
  CREATE,
  UPDATE,
  DELETE,
}

@Entity
@Table(name = "audit_log_entries")
data class AuditLogEntry(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  var id: UUID? = null,

  @Enumerated(EnumType.STRING)
  var action: AuditAction? = null,

  @NotNull
  var subjectType: String? = null,

  @NotNull
  var subjectId: UUID? = null,

  @NotNull
  var username: String? = null,

  @JdbcTypeCode(SqlTypes.JSON)
  var updates: Map<String, Any>? = null,

  @CreationTimestamp
  var createdAt: OffsetDateTime? = null,
)
