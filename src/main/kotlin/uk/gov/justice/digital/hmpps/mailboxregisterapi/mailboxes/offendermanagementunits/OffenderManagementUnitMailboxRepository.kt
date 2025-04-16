package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OffenderManagementUnitMailboxRepository :
  JpaRepository<OffenderManagementUnitMailbox, UUID>,
  QueryByExampleExecutor<OffenderManagementUnitMailbox>
