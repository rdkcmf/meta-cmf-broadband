do_install_append_class-target () {
    	install -D -m 0644 ${S}/systemd_units/ccspwifiagent.service ${D}${systemd_unitdir}/system/ccspwifiagent.service
	sed -i "s/ExecStart=\/usr\/bin\/CcspWifiSsp -subsys \$Subsys/ExecStart=\/bin\/sh -c '\/usr\/bin\/CcspWifiSsp -subsys \$Subsys 2\&\>\/rdklogs\/logs\/wifihal.log'/g" ${D}/lib/systemd/system/ccspwifiagent.service
}
