FILESEXTRAPATHS_prepend := "${THISDIR}/ccsp-eth-agent:"
SRC_URI += " \
    file://0001-REFPLTB-1488-RPI-B-not-getting-erouter-IP-with-topic.patch;apply=0 \
"

# we need to patch to code for RPi ccsp-eth-agent
do_ccsp_eth_agent_patches() {
    cd ${S}
    if [ ! -e patch_applied ]; then
        patch -p1 < ${WORKDIR}/0001-REFPLTB-1488-RPI-B-not-getting-erouter-IP-with-topic.patch
        touch patch_applied
    fi
}
addtask ccsp_eth_agent_patches after do_unpack before do_compile
