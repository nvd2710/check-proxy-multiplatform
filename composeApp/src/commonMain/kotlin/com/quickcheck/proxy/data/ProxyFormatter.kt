package com.quickcheck.proxy.data

object ProxyFormatter {

    private val IP_PORT_OPT_AUTH = Regex(
        """\b\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}(?::[^:\s]+:[^:\s]+)?\b"""
    )

    private val IP_PORT_ONLY = Regex(
        """\b\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}\b"""
    )

    private val USER_PASS_AT_IP_PORT = Regex(
        """(?:https?://)?[^\s:@,;]+:[^\s:@,;]+@\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}\b"""
    )

    private val IP_PORT_AT_USER_PASS = Regex(
        """(?:https?://)?\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}@[^\s:@,;]+:[^\s:@,;]+"""
    )

    private val USER_PASS_IP_PORT_PATTERN = Regex(
        """\b[^\s:@,;]+:[^\s:@,;]+:\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}\b"""
    )

    private val AUTO_COMBINED = Regex(
        """(?:https?://)?[^\s:@,;]+:[^\s:@,;]+@\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}""" +
        """|(?:https?://)?\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}@[^\s:@,;]+:[^\s:@,;]+""" +
        """|[^\s:@,;]+:[^\s:@,;]+:\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}""" +
        """|\d{1,3}(?:\.\d{1,3}){3}:\d{1,5}(?::[^:\s]+:[^:\s]+)?"""
    )

    fun format(raw: String, format: ProxyFormat): String {
        if (raw.isBlank()) return ""
        val cleaned = raw.replace('\t', ' ')
        val pattern = when (format) {
            ProxyFormat.AUTO -> AUTO_COMBINED
            ProxyFormat.IP_PORT_USER_PASS -> IP_PORT_OPT_AUTH
            ProxyFormat.IP_PORT -> IP_PORT_ONLY
            ProxyFormat.HTTP_USER_PASS_AT_IP_PORT,
            ProxyFormat.USER_PASS_AT_IP_PORT -> USER_PASS_AT_IP_PORT
            ProxyFormat.HTTP_IP_PORT_AT_USER_PASS,
            ProxyFormat.IP_PORT_AT_USER_PASS -> IP_PORT_AT_USER_PASS
            ProxyFormat.USER_PASS_IP_PORT -> USER_PASS_IP_PORT_PATTERN
        }
        return pattern.findAll(cleaned)
            .map { it.value }
            .toList()
            .joinToString("\n")
    }
}
