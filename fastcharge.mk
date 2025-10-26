#
# Copyright (C) 2025 kenway214
# SPDX-License-Identifier: Apache-2.0
#

# FastCharge app
PRODUCT_PACKAGES += \
    FastCharge

# FastCharge init rc
PRODUCT_PACKAGES += \
    init.fastcharge.rc

# FastCharge sepolicy
include packages/apps/FastCharge/sepolicy/SEPolicy.mk