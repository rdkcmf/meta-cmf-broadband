SRC_URI_remove = "${RDKB_CCSP_ROOT_GIT}/RdkPppManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=PppManager"
SRC_URI += "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/RdkPppManager;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=PppManager"

inherit coverity

DEPENDS += "breakpad breakpad-wrapper"

CFLAGS += "-I${STAGING_INCDIR}/breakpad "
CXXFLAGS += "-I${STAGING_INCDIR}/breakpad "

LDFLAGS += "-lbreakpadwrapper -lpthread"
