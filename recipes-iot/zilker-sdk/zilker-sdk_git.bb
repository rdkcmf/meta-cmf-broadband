SUMMARY = "The zilker-sdk component provides various services for an IoT framework to have the specific functionalities that can be accessed via APIs."
HOMEPAGE = "https://github.com/rdkcentral/zilker-sdk"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea5a749efe973d47a6a2a55449162843"

SRC_URI = "${CMF_GITHUB_ROOT}/zilker-sdk;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GITHUB_MAIN_BRANCH};name=zilker-sdk"
SRCREV_zilker-sdk = "${AUTOREV}"
SRCREV_FORMAT .= "_zilker-sdk"

SRC_URI += "git://github.com/staticlibs/ccronexpr.git;protocol=https;destsuffix=git/source/services/automation/core/src/ccronexpr;name=zilker-ccronexpr"
SRCREV_zilker-ccronexpr = "${AUTOREV}"
SRCREV_FORMAT .= "_zilker-ccronexpr"

SRC_URI += "https://services.gradle.org/distributions/gradle-4.8.1-bin.zip;name=zilker-gradle"
SRC_URI[zilker-gradle.md5sum] = "90db63b9cd864a6e6bc8d0c5e72ca5a2"
SRC_URI[zilker-gradle.sha256sum] = "af334d994b5e69e439ab55b5d2b7d086da5ea6763d78054f49f147b06370ed71"

S = "${WORKDIR}/git"

DEPENDS = "ccsp-common-library utopia dbus cjson libxml2 mosquitto16 curl sqlite3 mbedtls iksemel util-linux libparodus nanomsg duktape linenoise littlesheens"
RDEPENDS_${PN}_append_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'dunfell', ' bash', '', d)}"
DEPENDS_append_dunfell = " nettle libgpg-error libgcrypt unzip-native coreutils-native zlog"

require recipes-ccsp/ccsp/ccsp_common.inc

# use CMake for building, should perform "make all install"
inherit cmake systemd pythonnative pkgconfig

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'telemetry2_0', '-DENABLE_FEATURE_TELEMETRY2_0', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'telemetry2_0', ' -ltelemetry_msgsender ', '', d)}"

# add variables to pass into CMake.  the OE_SYSROOT passes in the effective 'SYSROOT'
# so we can append the proper include/lib directory paths for this environment

OECMAKE_SITEFILE = "-C ${S}/buildTools/cmake/products/pi.cmake"

EXTRA_OECMAKE = "\
    -DOE_SYSROOT:STRING=${STAGING_DIR_HOST} \
     "
#integrations
do_configure_prepend () {
    cd ${S}
    #cleaning the previous build of gradle
    export PATH=$PATH:/usr/bin
    ${WORKDIR}/gradle-4.8.1/bin/gradle clean
    cd -
    if [ ! -f ${WORKDIR}/Zilker_configure_compilation_errors_fixed ]; then
    sed -i "/set \-x/a export PATH=\$PATH:\${WORKDIR}/gradle-4.8.1/bin:/usr/bin" ${S}/tools/ipcGenerator/ipcGenerator.sh
    sed -i "s/ON DEPENDS/OFF DEPENDS/g" ${S}/buildTools/cmake/capabilities.cmake
    sed -i "s/set(ZILKER_BUILD_3RDPARTY ON CACHE INTERNAL \"\")/set(ZILKER_BUILD_3RDPARTY OFF CACHE INTERNAL \"\")/g" ${S}/buildTools/cmake/products/pi.cmake
    sed -i "s/set(ZILKER_VERIFY_3RDPARTY ON CACHE INTERNAL \"\")/set(ZILKER_VERIFY_3RDPARTY OFF CACHE INTERNAL \"\")/g" ${S}/buildTools/cmake/products/pi.cmake
    sed -i "s/set(ZILKER_INSTALL_LIBS ON CACHE INTERNAL \"\")/set(ZILKER_INSTALL_LIBS OFF CACHE INTERNAL \"\")/g" ${S}/buildTools/cmake/products/pi.cmake
    echo "# RDK build environment setup" >> ${S}/buildTools/cmake/products/pi.cmake
    echo "set(OE_INSTALL_PREFIX \"/opt/zilker\" CACHE INTERNAL \"\")" >> ${S}/buildTools/cmake/products/pi.cmake
    #fix compilation errors
    sed -i "s/ccronexpr.h/ccronexpr\/ccronexpr.h/g" ${S}/source/services/automation/core/src/actions/timer.c
    sed -i "/littlesheens/{N;s/\n.*//;}" ${S}/source/services/automation/core/CMakeLists.txt
    touch ${WORKDIR}/Zilker_configure_compilation_errors_fixed
    fi
    ln -fs ${S}/buildTools/cmake/products/pi.cmake ${WORKDIR}/site-file.cmake
    (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/source/services/rdkIntegration/config/XHFW.xml ${S}/source/services/rdkIntegration/src/XHFW_Ssp/dm_pack_datamodel.c)
}

# root dir we should install to when deployed
ZILKER_INSTALL_PREFIX = "/opt/zilker"

do_install_append () {
    # move libraries to /usr/lib
    install -m 0755 -d ${D}/usr/lib

    mv ${D}${ZILKER_INSTALL_PREFIX}/lib/* ${D}/usr/lib

    install -m 0755 -d ${D}/usr/ccsp/xhfw
    cp ${S}/source/services/rdkIntegration/config/XHFW.xml ${D}/usr/ccsp/xhfw
}

FILES_SOLIBSDEV = ""

FILES_${PN} += " \
        ${ZILKER_INSTALL_PREFIX}/bin \
        ${ZILKER_INSTALL_PREFIX}/lib \
        ${ZILKER_INSTALL_PREFIX}/include \
        ${ZILKER_INSTALL_PREFIX}/sbin \
        ${ZILKER_INSTALL_PREFIX}/etc \
        ${ZILKER_INSTALL_PREFIX}/stock \
        ${ZILKER_INSTALL_PREFIX}/ccsp \
        /usr/lib/* \
        /usr/ccsp/xhfw \
        "

FILES_${PN}-dbg += " \
    ${ZILKER_INSTALL_PREFIX}/bin/.debug \
    ${ZILKER_INSTALL_PREFIX}/sbin/.debug \
    ${ZILKER_INSTALL_PREFIX}/lib/.debug/*.so* \
    "

