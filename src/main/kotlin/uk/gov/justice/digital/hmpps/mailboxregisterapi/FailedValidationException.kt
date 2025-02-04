package uk.gov.justice.digital.hmpps.mailboxregisterapi

class FailedValidationException(
  val responseBody: Map<String, String?>,
) : Exception("Validation Failed")
