package uk.gov.justice.digital.hmpps.mailboxregisterapi

const val ROLE_SYSTEM_ADMIN = "MANAGE_CUSTODY_MAILBOX_REGISTER_ADMIN"
const val ROLE_MAILBOXES_RO = "ROLE_MANAGE_CUSTODY_MAILBOX_REGISTER__MAILBOXES__RO"

const val HAS_SYSTEM_ADMIN = """hasAnyRole("$ROLE_SYSTEM_ADMIN")"""
const val HAS_READ_MAILBOXES = """hasAnyRole("$ROLE_SYSTEM_ADMIN", "$ROLE_MAILBOXES_RO")"""
