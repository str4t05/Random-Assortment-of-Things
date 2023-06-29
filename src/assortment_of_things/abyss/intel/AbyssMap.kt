package assortment_of_things.abyss.intel

import assortment_of_things.abyss.AbyssUtils
import assortment_of_things.abyss.procgen.AbyssProcgen
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.MapParams
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipCreator
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaSpriteElement
import lunalib.lunaUI.elements.LunaElement
import lunalib.lunaUI.elements.LunaSpriteElement
import org.lazywizard.lazylib.ext.plus
import org.lwjgl.util.vector.Vector2f

class AbyssMap : BaseIntelPlugin() {


    override fun getName(): String {
        return "Abyss Map"
    }

    override fun hasLargeDescription(): Boolean {
        return true
    }

    override fun hasSmallDescription(): Boolean {
        return true
    }

    override fun createLargeDescription(panel: CustomPanelAPI?, width: Float, height: Float) {
       // super.createLargeDescription(panel, width, height)

        var element = panel!!.createUIElement(width, height, false)
        panel.addUIElement(element)

        var sprite = element.addLunaSpriteElement("graphics/backgrounds/abyss/Abyss2ForMap.jpg", LunaSpriteElement.ScalingTypes.STRETCH_SPRITE, width * 0.8f, height)

        sprite.getSprite().alphaMult = 0.5f

        var center = Vector2f(width * 0.45f, height * 0.35f)

        var systems = AbyssUtils.getAllAbyssSystems()

        var positions = HashMap<StarSystemAPI, Vector2f>()

        var icons = ArrayList<LunaElement>()
        for (system in systems)
        {

            if (!system.isEnteredByPlayer)
            {
                if (Global.getSector().playerFleet.containingLocation != system)
                {
                    continue
                }
            }

            var loc = AbyssUtils.getSystemLocation(system)

            var locOnMap = center.plus(loc)



            var icon = AbyssZoneMapIcon(system, AbyssUtils.ABYSS_COLOR, element, 30f, 30f).apply {
                position.inTL(locOnMap.x, locOnMap.y)
            }
            icons.add(icon)


            var unexplored = 0
            var neighbours = AbyssUtils.getNeighbouringSystems(system) + AbyssUtils.getFarNeighbouringSystems(system)
            for (sys in neighbours)
            {
                if (!sys.isEnteredByPlayer) unexplored++
            }

            var unidentified = system.customEntities.filter { it.customEntitySpec.id == "rat_abyss_icon" }.count()

            element.addTooltipToPrevious( object: TooltipCreator {
                override fun isTooltipExpandable(tooltipParam: Any?): Boolean {
                    return false
                }

                override fun getTooltipWidth(tooltipParam: Any?): Float {
                   return 350f
                }

                override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
                    tooltip!!.addPara("${system.baseName}", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "${system.baseName}")
                    tooltip.addSpacer(5f)

                    var tier = AbyssUtils.getTier(system)
                    if (system.baseName == "Midnight")
                    {
                        tooltip!!.addPara("The root of the abyss.", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "")
                        tooltip.addSpacer(5f)
                    }
                    else if (tier == AbyssProcgen.Tier.Low)
                    {
                        tooltip!!.addPara("A brightly illuminated part of the abyss, its matter not being dense enough to diminish the light.", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "")
                        tooltip.addSpacer(5f)
                    }
                    else if (tier == AbyssProcgen.Tier.Mid)
                    {
                        tooltip!!.addPara("This part of the abyss barely sees any light.", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "")
                        tooltip.addSpacer(5f)
                    }
                    else if (tier == AbyssProcgen.Tier.High)
                    {
                        tooltip!!.addPara("There is almost no light visible within this zone.", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "")
                        tooltip.addSpacer(5f)
                    }

                    if (unexplored != 0)
                    {
                        var prefix = "are"
                        var plural = "s"
                        if (unexplored == 1) prefix = "is"
                        if (unexplored == 1) plural = ""
                        tooltip!!.addPara("There $prefix $unexplored unexplored path$plural connected to this location.", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "")
                        tooltip.addSpacer(2f)
                    }

                    if (unidentified != 0)
                    {
                        var prefix = "are"
                        var plural = "s"
                        if (unidentified == 1) prefix = "is"
                        if (unidentified == 1) plural = ""
                        tooltip!!.addPara("There $prefix $unidentified unidentified structure$plural in this zone.", 0f, Misc.getTextColor(), AbyssUtils.ABYSS_COLOR, "")
                        tooltip.addSpacer(2f)
                    }
                }

            }, TooltipMakerAPI.TooltipLocation.BELOW)

            positions.put(system, Vector2f(icon.position.x, icon.position.y))
            // para.position.inTL(locOnMap.x, locOnMap.y)
        }

        MapLine(panel, positions, element, width, height)
        for (icon in icons)
        {
            element.bringComponentToTop(icon.elementPanel)
        }
    }

    override fun getIntelTags(map: SectorMapAPI?): MutableSet<String> {
        return mutableSetOf("Abyss")
    }

    override fun getIcon(): String {
        Global.getSettings().loadTexture("graphics/icons/intel/rat_map_icon.png")
        return "graphics/icons/intel/rat_map_icon.png"
    }

}