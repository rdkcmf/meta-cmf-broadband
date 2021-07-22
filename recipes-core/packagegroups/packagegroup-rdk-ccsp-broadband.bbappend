RDEPENDS_packagegroup-rdk-ccsp-broadband_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' rdk-wanmanager json-hal-lib ', '', d)} "
