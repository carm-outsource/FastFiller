package com.xbaimiao.fastfiller.core.fill

import com.xbaimiao.fastfiller.FastFiller
import com.xbaimiao.fastfiller.api.filltask.VolumeFiller
import com.xbaimiao.fastfiller.core.BlockCompare
import com.xbaimiao.fastfiller.core.workload.PlaceBlock
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Container
import org.bukkit.entity.Player

/**
 * @author: xbaimiao
 * @date: 2022年03月17日 20:10
 * @description:
 */
class ClearBlocks(
    private val list: List<Material>,
    private val blacklist: Boolean,
    private val player: Player
) : VolumeFiller {

    override fun fill(cornerA: Location, cornerB: Location, material: Material) {
        if (!(cornerA.world == cornerB.world && cornerA.world != null)) {
            throw NullPointerException("world not is null")
        }
        val workloadRunnable = FastFiller.workLoadPool.next()
        val box = BlockCompare(cornerA, cornerB)

        val world = cornerA.world!!

        for (x in box.minX..box.maxX) {
            for (y in box.minY..box.maxY) {
                for (z in box.minZ..box.maxZ) {
                    val block = world.getBlockAt(x, y, z)
                    if (!cleanable(block)) continue

                    val placeBlock = PlaceBlock(world.uid, x, y, z, material)
                    workloadRunnable.addWorkload(placeBlock)
                }
            }
        }
        FastFiller.dataContainer.setInFill(player, false)
    }

    fun cleanable(block: Block): Boolean {
        if (block.state is Container) {
            return false
        }
        if (blacklist && block.type in list) {
            return false
        } else if (!blacklist && block.type !in list) {
            return false
        }
        return true
    }

}
