package uk.gov.justice.digital.hmpps.mailboxregisterapi.mailboxes.offendermanagementunits

import jakarta.transaction.Transactional
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Sort
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.servlet.resource.NoResourceFoundException
import uk.gov.justice.digital.hmpps.mailboxregisterapi.PrisonCode
import java.util.UUID

@Service
class OffenderManagementUnitMailboxService(
  private val repository: OffenderManagementUnitMailboxRepository,
) {
  @Transactional
  fun createMailbox(newMailbox: OffenderManagementUnitMailboxForm): OffenderManagementUnitMailbox = repository.saveAndFlush(newMailbox.asEntity())

  @Transactional
  fun getMailboxById(id: UUID): OffenderManagementUnitMailbox = repository.findById(id).orElseThrow { NoResourceFoundException(HttpMethod.GET, id.toString()) }

  @Transactional
  fun updateMailbox(id: UUID, mailbox: OffenderManagementUnitMailboxForm): OffenderManagementUnitMailbox {
    val existingMailbox = getMailboxById(id)

    existingMailbox.apply {
      name = mailbox.name
      emailAddress = mailbox.emailAddress
      prisonCode = mailbox.prisonCode
      role = mailbox.role
    }

    return repository.saveAndFlush(existingMailbox)
  }

  fun listMailboxes(
    prisonCode: PrisonCode?,
    role: OffenderManagementUnitRole?,
  ): List<OffenderManagementUnitMailbox> = repository.findAll(
    Example.of(OffenderManagementUnitMailbox(role = role, prisonCode = prisonCode), FILTER),
    SORTED_BY_CREATED_AT_ASC,
  )

  @Transactional
  fun deleteMailbox(id: UUID) {
    repository.delete(getMailboxById(id))
  }

  companion object {
    val SORTED_BY_CREATED_AT_ASC: Sort = Sort.by(Sort.Direction.ASC, OffenderManagementUnitMailbox::createdAt.name)
    val FILTER: ExampleMatcher = ExampleMatcher.matchingAll()
      .withMatcher("role", ExampleMatcher.GenericPropertyMatchers.exact())
      .withMatcher("prisonCode", ExampleMatcher.GenericPropertyMatchers.exact())
      .withIgnorePaths("id", "name", "emailAddress", "createdAt", "updatedAt")
  }
}
