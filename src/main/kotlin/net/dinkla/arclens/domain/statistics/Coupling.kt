package net.dinkla.arclens.domain.statistics

import kotlinx.serialization.Serializable

@Serializable
data class Coupling(
    val afferentCoupling: Int,
    val efferentCoupling: Int,
    val instability: Double,
) {
    constructor(afferentCoupling: Int, efferentCoupling: Int) : this(
        afferentCoupling,
        efferentCoupling,
        if (efferentCoupling + afferentCoupling > 0) {
            efferentCoupling.toDouble() / (efferentCoupling + afferentCoupling)
        } else {
            0.0
        },
    )
}
