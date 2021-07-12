DEPENDS += "rbus"

DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' json-hal-lib', '', d)} "
