FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://udhcpc.script"
SRC_URI += "file://udhcpc.vendor_specific"
SRC_URI += "file://dhcpswitch.sh"

SRC_URI  += " ${@bb.utils.contains('DISTRO_FEATURES', 'device_gateway_association', 'file://Device_Gateway_Association.patch;apply=no', '', d)}"

DEPENDS += " nanomsg"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-D_WAN_MANAGER_ENABLED_', '', d)}"

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

# rdkgponmanager requires rpc_client.h
do_install_append () {
    install -d ${D}${includedir}/rpc-cli
    touch ${D}${includedir}/rpc-cli/rpc_client.h
}
