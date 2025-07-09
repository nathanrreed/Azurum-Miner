package com.nred.azurum_miner.common

class MinerCommon {
    companion object {
        fun tierRange(tier: Int): String {
            return if (tier == 5) {
                tier.toString()
            } else {
                "${tier}-5"
            }
        }
    }
}