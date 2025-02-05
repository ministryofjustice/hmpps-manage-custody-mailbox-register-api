package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.localdeliveryunits

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
  fun createMailbox(newMailbox: LocalDeliveryUnitMailboxForm): LocalDeliveryUnitMailbox {
    return repository.saveAndFlush(newMailbox.asEntity())
  }

  @Transactional
  fun getMailboxById(id: UUID): LocalDeliveryUnitMailbox {
    return repository.findById(id).orElseThrow { NoResourceFoundException(HttpMethod.GET, id.toString()) }
  }

  @Transactional
  fun listMailboxes(): List<LocalDeliveryUnitMailbox> {
    return repository.findAll(Sort.by(Sort.Direction.ASC, LocalDeliveryUnitMailbox::createdAt.name))
  }

  @Transactional
  fun updateMailbox(id: UUID, mailbox: LocalDeliveryUnitMailboxForm): LocalDeliveryUnitMailbox {
    val existingMailbox = getMailboxById(id)

    existingMailbox.apply {
      unitCode = mailbox.unitCode
      areaCode = mailbox.areaCode
      emailAddress = mailbox.emailAddress
      country = mailbox.country
      name = mailbox.name
    }

    return repository.saveAndFlush(existingMailbox)
  }

  @Transactional
  fun deleteMailbox(id: UUID) {
    repository.delete(getMailboxById(id))
  }
}
