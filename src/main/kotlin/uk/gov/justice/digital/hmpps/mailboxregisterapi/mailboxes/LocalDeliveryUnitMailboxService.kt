package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes

import jakarta.transaction.Transactional
import org.springframework.data.domain.Sort
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.util.UUID

@Service
class LocalDeliveryUnitMailboxService(
  private val repository: LocalDeliveryUnitMailboxRepository,
) {
  @Transactional
  fun createMailbox(newMailbox: LocalDeliveryUnitMailbox): LocalDeliveryUnitMailbox {
    return repository.saveAndFlush(newMailbox)
  }

  @Transactional
  fun getMailboxById(id: UUID): LocalDeliveryUnitMailbox {
    return repository.findById(id).firstOrNull()
      ?: throw NoResourceFoundException(HttpMethod.GET, id.toString())
  }

  @Transactional
  fun listMailboxes(): List<LocalDeliveryUnitMailbox> {
    return repository.findAll(Sort.by(Sort.Direction.ASC, LocalDeliveryUnitMailbox::createdAt.name))
  }
}
