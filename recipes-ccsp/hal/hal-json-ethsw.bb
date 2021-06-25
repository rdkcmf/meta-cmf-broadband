SUMMARY = "Json based hal for ethagent"
HOMEPAGE = "https://github.com/rdkcmf/rdkb-raspberrypi-hal"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "hal-ethsw json-hal-lib hal-platform halinterface"

SRC_URI = " \
        ${CMF_GIT_ROOT}/rdkb/devices/raspberrypi/hal;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=jsonethsw-raspberrypi \
"
SRCREV_jsonethsw-raspberrypi = "${AUTOREV}"
SRCREV_FORMAT = "jsonethsw"

S = "${WORKDIR}/git/source/json-ethsw"

inherit autotools systemd

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/ccsp \
    "

LDFLAGS += "-ljson_hal_server -lhal_ethsw -ljson-c -pthread"

do_install() {
    install -d ${D}${bindir}
    install -d ${D}${systemd_unitdir}/system
    install -m 700 ${B}/hal_json_ethsw ${D}${bindir}
    install -D -m 0644 ${S}/systemd/hal-json-ethsw.service ${D}${systemd_unitdir}/system/hal-json-ethsw.service
}

SYSTEMD_SERVICE_${PN} += "hal-json-ethsw.service"

FILES_${PN} += "/usr/bin ${systemd_unitdir}/system/hal-json-ethsw.service"
