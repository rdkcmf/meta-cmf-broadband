SUMMARY = "Cron expression parsing in ANSI C"
HOMEPAGE = "https://github.com/staticlibs/ccronexpr"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=646c743a776a3dc373f94d63fb2f1a71"

SRC_URI += "git://github.com/staticlibs/ccronexpr.git;protocol=https"
SRCREV = "${AUTOREV}"

SRC_URI += "file://Makefile"
S = "${WORKDIR}/git"

inherit autotools

do_rpi_fix () {
	mv ${S}/LICENSE.txt ${S}/LICENSE
        cp ${WORKDIR}/Makefile   ${S}/
}
addtask rpi_fix after do_unpack before do_configure

do_compile () {
    oe_runmake -C ${S}/ -f Makefile clean
    oe_runmake -C ${S}/ -f Makefile library
}

SOVER = "0.0.0"
do_install () {
    install -d ${D}${libdir} 
    install -d ${D}${includedir}
    install -m 0755 ${S}/libccronexpr.so.${SOVER} ${D}${libdir}
    ln -s libccronexpr.so.${SOVER} ${D}${libdir}/libccronexpr.so
    ln -s libccronexpr.so.${SOVER} ${D}${libdir}/libccronexpr.so.0
    cp ${S}/*.h ${D}${includedir}
}

FILES_${PN} += "${libdir}/*"
