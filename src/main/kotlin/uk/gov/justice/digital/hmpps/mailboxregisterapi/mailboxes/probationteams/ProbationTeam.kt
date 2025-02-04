package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.probationteams

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import uk.gov.justice.digital.hmpps.mailboxregisterapi.audit.AuditableEntity
import uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits.LocalDeliveryUnitMailbox
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "probation_teams")
class ProbationTeam(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  var id: UUID? = null,

  @NotNull
  var teamCode: String? = null,

  @NotNull
  var emailAddress: String? = null,

  @ColumnDefault("CURRENT_TIMESTAMP")
  var createdAt: OffsetDateTime? = null,

  @ColumnDefault("CURRENT_TIMESTAMP")
  var updatedAt: OffsetDateTime? = null,

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "local_delivery_unit_mailbox_id", nullable = false)
  var localDeliveryUnitMailbox: LocalDeliveryUnitMailbox? = null,
) : AuditableEntity {
  override fun auditableSubjectId() = id
  override fun auditableSubjectType() = "ProbationTeam"
  override fun auditableFields() = mapOf(
    "teamCode" to teamCode,
    "emailAddress" to emailAddress,
    "localDeliveryUnitMailboxId" to localDeliveryUnitMailbox?.id.toString(),
  )
}
