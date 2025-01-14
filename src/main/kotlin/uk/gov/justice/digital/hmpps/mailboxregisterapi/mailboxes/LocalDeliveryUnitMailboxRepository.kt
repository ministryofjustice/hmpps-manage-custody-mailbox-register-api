package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LocalDeliveryUnitMailboxRepository : JpaRepository<LocalDeliveryUnitMailbox, String>
