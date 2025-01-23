package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
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

  @field:NotBlank
  var prisonCode: String? = "",

  @field:NotBlank @field:Pattern(regexp = "CVL|HDC", message = "must be either CVL or HDC")
  var role: String? = null,

  @CreationTimestamp
  var createdAt: OffsetDateTime? = null,

  @UpdateTimestamp
  var updatedAt: OffsetDateTime? = null,
)
