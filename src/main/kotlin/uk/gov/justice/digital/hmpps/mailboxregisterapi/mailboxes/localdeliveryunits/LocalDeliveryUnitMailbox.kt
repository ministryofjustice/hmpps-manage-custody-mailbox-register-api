package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "local_delivery_unit_mailboxes")
class LocalDeliveryUnitMailbox(
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @ColumnDefault("gen_random_uuid()")
  var id: UUID? = null,

  @field:NotBlank
  var unitCode: String? = "",

  @field:NotBlank
  var areaCode: String? = "",

  @field:NotBlank @field:Email
  var emailAddress: String? = "",

  var country: String? = null,

  var name: String? = null,

  @CreationTimestamp
  var createdAt: OffsetDateTime? = null,

  @UpdateTimestamp
  var updatedAt: OffsetDateTime? = null,
)
