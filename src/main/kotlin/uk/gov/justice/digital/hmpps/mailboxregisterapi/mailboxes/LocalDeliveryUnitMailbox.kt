package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.ColumnDefault
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "local_delivery_unit_mailboxes")
class LocalDeliveryUnitMailbox(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  var id: UUID? = null,

  @NotNull
  var unitCode: String = "",

  @NotNull
  var areaCode: String = "",

  @NotNull
  var emailAddress: String = "",

  var country: String? = null,

  var name: String? = null,

  @ColumnDefault("CURRENT_TIMESTAMP")
  var createdAt: OffsetDateTime? = null,

  @ColumnDefault("CURRENT_TIMESTAMP")
  var updatedAt: OffsetDateTime? = null
)