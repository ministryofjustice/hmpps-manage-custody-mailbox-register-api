package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditableEntity
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "offender_management_unit_mailboxes")
class OffenderManagementUnitMailbox(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  var id: UUID? = null,

  @field:NotBlank @field:Email
  var emailAddress: String? = "",

  var name: String? = null,

  @Enumerated(EnumType.STRING) @field:NotNull(message = "must not be blank")
  var prisonCode: PrisonCode? = null,

  @Enumerated(EnumType.STRING) @field:NotNull(message = "must not be blank")
  var role: OffenderManagementUnitRole? = null,

  @CreationTimestamp
  var createdAt: OffsetDateTime? = null,

  @UpdateTimestamp
  var updatedAt: OffsetDateTime? = null,
) : AuditableEntity {
  override fun auditableSubjectId(): UUID? = id
  override fun auditableSubjectType(): String = "OffenderManagementUnitMailbox"
  override fun auditableFields(): Map<String, Any?> = mapOf(
    "emailAddress" to emailAddress,
    "name" to name,
    "prisonCode" to prisonCode.toString(),
    "role" to role.toString(),
  )
}
