package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LocalDeliveryUnitMailboxRepository : JpaRepository<LocalDeliveryUnitMailbox, String> {
  fun findById(id: UUID): LocalDeliveryUnitMailbox?
}
