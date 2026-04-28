package com.quickcheck.proxy.data

object ProxyParser {

    fun parse(raw: String, format: ProxyFormat, type: ProxyType): ProxyEntry? {
        val s = raw.trim()
        if (s.isEmpty()) return null
        return when (format) {
            ProxyFormat.AUTO -> autoDetect(s, type)
            ProxyFormat.IP_PORT_USER_PASS -> ipPortUserPass(s, type)
            ProxyFormat.IP_PORT -> ipPort(s, type)
            ProxyFormat.HTTP_USER_PASS_AT_IP_PORT -> httpUserPassAtIpPort(s, type)
            ProxyFormat.HTTP_IP_PORT_AT_USER_PASS -> httpIpPortAtUserPass(s, type)
            ProxyFormat.USER_PASS_AT_IP_PORT -> userPassAtIpPort(s, type)
            ProxyFormat.IP_PORT_AT_USER_PASS -> ipPortAtUserPass(s, type)
            ProxyFormat.USER_PASS_IP_PORT -> userPassIpPort(s, type)
        }
    }

    private fun autoDetect(s: String, type: ProxyType): ProxyEntry? {
        val cleaned = stripScheme(s)
        if (cleaned.contains("@")) {
            val (left, right) = cleaned.split("@", limit = 2)
            return when {
                isHostPort(right) -> {
                    val (h, pt) = right.split(":", limit = 2)
                    val (u, p) = splitCreds(left) ?: return null
                    build(s, h, pt, u, p, type)
                }
                isHostPort(left) -> {
                    val (h, pt) = left.split(":", limit = 2)
                    val (u, p) = splitCreds(right) ?: return null
                    build(s, h, pt, u, p, type)
                }
                else -> null
            }
        }
        val parts = cleaned.split(":")
        return when (parts.size) {
            2 -> build(s, parts[0], parts[1], null, null, type)
            4 -> {
                val secondNumeric = parts[1].toIntOrNull() != null
                val fourthNumeric = parts[3].toIntOrNull() != null
                when {
                    secondNumeric && !fourthNumeric ->
                        build(s, parts[0], parts[1], parts[2], parts[3], type)
                    !secondNumeric && fourthNumeric ->
                        build(s, parts[2], parts[3], parts[0], parts[1], type)
                    secondNumeric ->
                        build(s, parts[0], parts[1], parts[2], parts[3], type)
                    else -> null
                }
            }
            else -> null
        }
    }

    private fun stripScheme(s: String): String =
        if (s.startsWith("http://", true)) s.substring(7)
        else if (s.startsWith("https://", true)) s.substring(8)
        else if (s.startsWith("socks5://", true)) s.substring(9)
        else if (s.startsWith("socks://", true)) s.substring(8)
        else s

    private fun isHostPort(s: String): Boolean {
        val p = s.split(":")
        return p.size == 2 && p[0].isNotEmpty() && p[1].toIntOrNull() != null
    }

    private fun splitCreds(s: String): Pair<String, String>? {
        val p = s.split(":", limit = 2)
        return if (p.size == 2) p[0] to p[1] else null
    }

    private fun build(raw: String, host: String, port: String, user: String?, pass: String?, type: ProxyType): ProxyEntry? {
        val portInt = port.toIntOrNull() ?: return null
        if (host.isBlank() || portInt !in 1..65535) return null
        return ProxyEntry(raw, host, portInt, user?.takeIf { it.isNotEmpty() }, pass?.takeIf { it.isNotEmpty() }, type)
    }

    private fun ipPortUserPass(s: String, type: ProxyType): ProxyEntry? {
        val p = s.split(":")
        if (p.size != 4) return null
        return build(s, p[0], p[1], p[2], p[3], type)
    }

    private fun ipPort(s: String, type: ProxyType): ProxyEntry? {
        val cleaned = stripScheme(s)
        val p = cleaned.split(":")
        if (p.size != 2) return null
        return build(s, p[0], p[1], null, null, type)
    }

    private fun httpUserPassAtIpPort(s: String, type: ProxyType): ProxyEntry? {
        val body = stripScheme(s)
        if (!body.contains("@")) return null
        val (creds, hp) = body.split("@", limit = 2)
        val (u, p) = splitCreds(creds) ?: return null
        val hpp = hp.split(":", limit = 2)
        if (hpp.size != 2) return null
        return build(s, hpp[0], hpp[1], u, p, type)
    }

    private fun httpIpPortAtUserPass(s: String, type: ProxyType): ProxyEntry? {
        val body = stripScheme(s)
        if (!body.contains("@")) return null
        val (hp, creds) = body.split("@", limit = 2)
        val hpp = hp.split(":", limit = 2)
        val (u, p) = splitCreds(creds) ?: return null
        if (hpp.size != 2) return null
        return build(s, hpp[0], hpp[1], u, p, type)
    }

    private fun userPassAtIpPort(s: String, type: ProxyType): ProxyEntry? {
        if (!s.contains("@")) return null
        val (creds, hp) = s.split("@", limit = 2)
        val (u, p) = splitCreds(creds) ?: return null
        val hpp = hp.split(":", limit = 2)
        if (hpp.size != 2) return null
        return build(s, hpp[0], hpp[1], u, p, type)
    }

    private fun ipPortAtUserPass(s: String, type: ProxyType): ProxyEntry? {
        if (!s.contains("@")) return null
        val (hp, creds) = s.split("@", limit = 2)
        val (u, p) = splitCreds(creds) ?: return null
        val hpp = hp.split(":", limit = 2)
        if (hpp.size != 2) return null
        return build(s, hpp[0], hpp[1], u, p, type)
    }

    private fun userPassIpPort(s: String, type: ProxyType): ProxyEntry? {
        val p = s.split(":")
        if (p.size != 4) return null
        return build(s, p[2], p[3], p[0], p[1], type)
    }
}
