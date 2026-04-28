package com.quickcheck.proxy.data

enum class ProxyFormat(val displayName: String) {
    AUTO("Auto-detect"),
    IP_PORT_USER_PASS("ip:port:user:pass"),
    IP_PORT("ip:port"),
    HTTP_USER_PASS_AT_IP_PORT("http://user:pass@ip:port"),
    HTTP_IP_PORT_AT_USER_PASS("http://ip:port@user:pass"),
    USER_PASS_AT_IP_PORT("user:pass@ip:port"),
    IP_PORT_AT_USER_PASS("ip:port@user:pass"),
    USER_PASS_IP_PORT("user:pass:ip:port"),
}
