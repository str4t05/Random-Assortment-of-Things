package assortment_of_things.abyss.hullmods

import activators.ActivatorManager
import assortment_of_things.abyss.activators.ParticleStreamActivator
import com.fs.starfarer.api.combat.BaseHullMod
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.combat.ShipAPI.HullSize

class QualityAssuranceHullmod : BaseHullMod() {


    var modID = "rat_quality_assurance"

    var deploymentModID = "deployment_points_mod"

    var dp = mapOf(HullSize.FRIGATE to 2f, HullSize.DESTROYER to 3f, HullSize.CRUISER to 4f, HullSize.CAPITAL_SHIP to 6f)
    var armor = mapOf(HullSize.FRIGATE to 100f, HullSize.DESTROYER to 200f, HullSize.CRUISER to 300f, HullSize.CAPITAL_SHIP to 450f)
    var speed = mapOf(HullSize.FRIGATE to 25f, HullSize.DESTROYER to 20f, HullSize.CRUISER to 15f, HullSize.CAPITAL_SHIP to 10f)
    var diss = mapOf(HullSize.FRIGATE to 50f, HullSize.DESTROYER to 100f, HullSize.CRUISER to 150f, HullSize.CAPITAL_SHIP to 250f)

    override fun applyEffectsAfterShipCreation(ship: ShipAPI?, id: String?) {
        super.applyEffectsAfterShipCreation(ship, id)

        if (ship == null) return
        ActivatorManager.addActivator(ship, ParticleStreamActivator(ship))
    }

    override fun applyEffectsBeforeShipCreation(hullSize: ShipAPI.HullSize?, stats: MutableShipStatsAPI?, id: String?) {
        super.applyEffectsBeforeShipCreation(hullSize, stats, id)

        if (hullSize == ShipAPI.HullSize.CAPITAL_SHIP)
        {
            stats!!.maxCombatReadiness.modifyFlat(modID, -0.10f)
        }
        else
        {
            stats!!.maxCombatReadiness.modifyFlat(modID, -0.05f)
        }

        stats.getSuppliesToRecover().modifyFlat(modID, dp.get(hullSize)!!);
        stats.getDynamic().getMod(deploymentModID).modifyFlat(modID, dp.get(hullSize)!!);

        stats.armorBonus.modifyFlat(modID, armor.get(hullSize)!!)
        stats.maxSpeed.modifyFlat(modID, speed.get(hullSize)!!)
        stats.fluxDissipation.modifyFlat(modID, diss.get(hullSize)!!)

       /* stats.ballisticWeaponFluxCostMod.modifyMult(modID, 0.9f)
        stats.energyWeaponFluxCostMod.modifyMult(modID, 0.9f)
        stats.missileWeaponFluxCostMod.modifyMult(modID, 0.9f)*/
    }

    override fun shouldAddDescriptionToTooltip(hullSize: ShipAPI.HullSize?, ship: ShipAPI?, isForModSpec: Boolean): Boolean {
        return false
    }

    override fun addPostDescriptionSection(tooltip: TooltipMakerAPI?, hullSize: ShipAPI.HullSize?, ship: ShipAPI?, width: Float, isForModSpec: Boolean) {



        var hc = Misc.getHighlightColor()
        var nc = Misc.getNegativeHighlightColor()

        tooltip!!.addSpacer(5f)
        var label = tooltip.addPara("The ship receives improved maintenance and uses better performing components, increasing the ships deployment cost by 2/3/4/6. \n\n" +
                "In turn, the ship performs better within combat. The ship gains an additional 100/200/300/450 units of armor. Its max speed is increased by 25/20/15/10 and " +
                "it receives an increase in flux dissipation by 50/100/150/250.", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        label.setHighlight("deployment cost", "2/3/4/6", "100/200/300/450", "armor", "max speed", "25/20/15/10","flux dissipation","50/100/150/250")
        label.setHighlightColors(nc, nc, hc,hc,hc,hc,hc,hc,hc,hc, )

        tooltip.addSpacer(10f)

        tooltip.addPara("Hull Alterations decrease the ships maximum CR by 5%%/5%%/5%%/10%% based on its hullsize.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor())

    }
}