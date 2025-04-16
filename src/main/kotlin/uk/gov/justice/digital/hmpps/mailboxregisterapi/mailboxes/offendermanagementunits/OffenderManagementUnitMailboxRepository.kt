package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode
import java.util.UUID

@Repository
interface OffenderManagementUnitMailboxRepository : JpaRepository<OffenderManagementUnitMailbox, UUID> {
  fun findAllByPrisonCode(prisonCode: PrisonCode, sort: Sort): List<OffenderManagementUnitMailbox>
}
