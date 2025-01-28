package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import jakarta.transaction.Transactional
import org.springframework.data.domain.Sort
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.util.UUID

@Service
class OffenderManagementUnitMailboxService(
  private val repository: OffenderManagementUnitMailboxRepository,
) {
  @Transactional
  fun createMailbox(newMailbox: OffenderManagementUnitMailboxForm): OffenderManagementUnitMailbox {
    return repository.saveAndFlush(newMailbox.asEntity())
  }

  @Transactional
  fun getMailboxById(id: UUID): OffenderManagementUnitMailbox {
    return repository.findById(id).orElseThrow { NoResourceFoundException(HttpMethod.GET, id.toString()) }
  }

  @Transactional
  fun listMailboxes(): List<OffenderManagementUnitMailbox> {
    return repository.findAll(Sort.by(Sort.Direction.ASC, OffenderManagementUnitMailbox::createdAt.name))
  }

  @Transactional
  fun deleteMailbox(id: UUID) {
    repository.delete(getMailboxById(id))
  }
}
