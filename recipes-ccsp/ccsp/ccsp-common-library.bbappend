do_install_append_class-target () {
    install -m 644 ${S}/WebConfig_Framework/*.h ${D}/usr/include/ccsp
}
