firewall {
    all-ping disable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name WAN_IN {
        default-action drop
        description "packets from Internet to LAN & WLAN"
        enable-default-log
        rule 2 {
            action accept
            description "allow established sessions"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 3 {
            action drop
            description "drop invalid state"
            log enable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "packets from Internet to the router"
        enable-default-log
        rule 1 {
            action accept
            description "allow established sessions"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "drop invalid state"
            log enable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    ethernet eth0 {
        address 192.168.1.1/24
        address 2a02:abcd:abcd:1::1/64
        description Int
        duplex auto
        ipv6 {
            dup-addr-detect-transmits 1
            router-advert {
                cur-hop-limit 64
                default-preference high
                link-mtu 1500
                managed-flag true
                max-interval 600
                other-config-flag true
                prefix 2a02:abcd:abcd:1::/64 {
                    autonomous-flag false
                    on-link-flag true
                    valid-lifetime 2592000
                }
                radvd-options "RDNSS 2001:1620:2777::2 2001:8a8:21:4::2 2001:1620:2777:1a::198 {};"
                reachable-time 0
                retrans-timer 60
                send-advert true
            }
        }
    }
    ethernet eth1 {
        address dhcp
        description Ext
        dhcpv6-pd {
            pd 0 {
                interface eth0 {
                    prefix-id :1
                }
                interface eth2 {
                    prefix-id :2
                    service slaac
                }
                prefix-length /48
            }
            rapid-commit enable
        }
        duplex auto
        firewall {
            in {
                name WAN_IN
            }
            local {
                name WAN_LOCAL
            }
        }
        speed auto
    }
    ethernet eth2 {
        address 192.168.2.1/24
        address 2a02:abcd:abcd:2::1/64
        description DMZ
        duplex auto
        ipv6 {
            dup-addr-detect-transmits 1
            router-advert {
                cur-hop-limit 64
                default-preference high
                link-mtu 1500
                managed-flag true
                max-interval 600
                other-config-flag true
                prefix 2a02:abcd:abcd:2::/64 {
                    autonomous-flag false
                    on-link-flag true
                    valid-lifetime 2592000
                }
                radvd-options "RDNSS 2001:1620:2777::2 2001:8a8:21:4::2 2001:1620:2777:1a::198 {};"
                reachable-time 60
                retrans-timer 60
                send-advert true
            }
        }
        speed auto
    }
    loopback lo {
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name DMZ_IPv4 {
            authoritative disable
            subnet 192.168.2.0/24 {
                default-router 192.168.2.1
                dns-server 213.144.129.2
                dns-server 212.55.195.90
                lease 86400
                start 192.168.2.240 {
                    stop 192.168.2.245
                }
                static-mapping RIPE_Atlas_Probe {
                    ip-address 192.168.2.240
                    mac-address c0:4a:00:9f:f4:58
                }
            }
        }
        shared-network-name Local_IPv4 {
            authoritative disable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 213.144.129.2
                dns-server 212.55.195.90
                lease 86400
                start 192.168.1.235 {
                    stop 192.168.1.245
                }
            }
        }
    }
    dhcpv6-server {
        shared-network-name DMZ_IPv6 {
            subnet 2a02:abcd:abcd:2::/64 {
                address-range {
                    start 2a02:abcd:abcd:2::200 {
                        stop 2a02:abcd:abcd:2::FFF
                    }
                }
                lease-time {
                    default 86400
                }
                name-server 2001:1620:2777::2
                name-server 2001:8a8:21:4::2
            }
        }
        shared-network-name Local_IPv6 {
            subnet 2a02:abcd:abcd:1::/64 {
                address-range {
                    start 2a02:abcd:abcd:1::200 {
                        stop 2a02:abcd:abcd:1::FFF
                    }
                }
                lease-time {
                    default 86400
                }
                name-server 2001:1620:2777::2
                name-server 2001:8a8:21:4::2
            }
        }
    }
    dns {
        forwarding {
            cache-size 30
            listen-on eth0
            listen-on eth2
        }
    }
    gui {
        https-port 443
    }
    nat {
        rule 5000 {
            description "Basic INET access"
            log disable
            outbound-interface eth1
            protocol all
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
}
system {
    conntrack {
        expect-table-size 2048
        hash-size 32768
        modules {
            sip {
                disable
            }
        }
        table-size 262144
    }
    host-name EdgeRouter
    login {
        banner {
            pre-login "\n\n\n\t This system is for the use of authorized users only. Individuals using this computer system without authority, or in excess of their authority, are subject to having all of their activities on this system monitored and recorded by system personnel. In the course of monitoring individuals improperly using this system, or in the course of system maintenance, the activities of authorized users may also be monitored.  Anyone using this system expressly consents to such monitoring and is advised that if such monitoring reveals possible evidence of criminal activity, system personnel may provide the evidence of such monitoring to law enforcement officials.\n\n\n"
        }
    }
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    offload {
        ipv4 {
            forwarding enable
        }
        ipv6 {
            forwarding disable
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone Europe/Zurich
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@4:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.6.0.4716006.141031.1731 */
