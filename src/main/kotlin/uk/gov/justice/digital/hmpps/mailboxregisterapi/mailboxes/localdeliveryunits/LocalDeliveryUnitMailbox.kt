package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditableEntity
import java.time.OffsetDateTime
import java.util.UUID

const val UNIT_CODE_UNIQUE_CONSTRAINT = "ldu_unique_unit_code"

@Entity
@Table(
  name = "local_delivery_unit_mailboxes",
  uniqueConstraints = [
    UniqueConstraint(
      name = UNIT_CODE_UNIQUE_CONSTRAINT,
      columnNames = ["unit_code"],
    ),
  ],
)
class LocalDeliveryUnitMailbox(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  var id: UUID? = null,

  @field:NotBlank
  @Column(unique = true)
  var unitCode: String? = "",

  var areaCode: String? = null,

  @field:NotBlank @field:Email
  var emailAddress: String? = "",

  @field:NotBlank
  var country: String? = "",

  @field:NotBlank
  var name: String? = "",

  @CreationTimestamp
  var createdAt: OffsetDateTime? = null,

  @UpdateTimestamp
  var updatedAt: OffsetDateTime? = null,
) : AuditableEntity {
  override fun auditableSubjectId(): UUID? = id
  override fun auditableSubjectType(): String = "LocalDeliveryUnitMailbox"
  override fun auditableFields(): Map<String, Any?> = mapOf(
    "emailAddress" to emailAddress,
    "unitCode" to unitCode,
    "areaCode" to areaCode,
    "country" to country,
    "name" to name,
  )
}
