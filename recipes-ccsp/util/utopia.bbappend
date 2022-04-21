FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://udhcpc.script"
SRC_URI += "file://udhcpc.vendor_specific"
SRC_URI += "file://dhcpswitch.sh"

SRC_URI  += " ${@bb.utils.contains('DISTRO_FEATURES', 'device_gateway_association', 'file://Device_Gateway_Association.patch;apply=no', '', d)}"

DEPENDS += " nanomsg"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-D_WAN_MANAGER_ENABLED_', '', d)}"

LDFLAGS += " -lpthread -lhal_platform"

#RDKBDEV-83 -Patch code based on distro
do_utopia_patches() {
if [ "${@bb.utils.contains("DISTRO_FEATURES", "device_gateway_association", "yes", "no", d)}" = "yes" ]; then    
    cd ${S} 
    if [ ! -e patch_applied ]; then
        patch -p1 < ${WORKDIR}/Device_Gateway_Association.patch
        touch patch_applied
    fi
fi
}
addtask utopia_patches after do_unpack before do_compile

RDEPENDS_${PN}_append_dunfell = " ${@bb.utils.contains('DISTRO_FEATURES', 'core-net-lib', ' core-net-lib', " ", d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'core-net-lib', ' core-net-lib', " ", d)}"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'core-net-lib', ' -DCORE_NET_LIB', '', d)}"
EXTRA_OECONF_append = " --enable-core_net_lib_feature_support=${@bb.utils.contains('DISTRO_FEATURES', 'core-net-lib', 'yes', 'no', d)} "
