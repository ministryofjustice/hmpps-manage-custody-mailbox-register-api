package uk.gov.justice.digital.hmpps.mailboxregisterapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MailboxRegisterApi

fun main(args: Array<String>) {
  runApplication<MailboxRegisterApi>(*args)
}
