/*
 * Copyright 2024 RTAkland
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package cn.rtast.yeeeesmotd.paper.listeners

import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.configManager
import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.defaultIcon
import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.faviconManager
import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.hitokotoManager
import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.pingRecordManager
import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.skinHeadManager
import cn.rtast.yeeeesmotd.paper.YeeeesMOTDPlugin.Companion.miniMessage
import cn.rtast.yeeeesmotd.utils.nextBoolean
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerListPingEvent
import java.io.ByteArrayInputStream
import java.util.*
import javax.imageio.ImageIO

class ServerListPingEventListener(private val server: Server) : Listener {

    @EventHandler
    fun onServerListPingEvent(event: ServerListPingEvent) {
        val ip = event.address.hostName

        var favicon = server.loadServerIcon(defaultIcon)
        val showHead = false;

        val randomDescription = configManager.getRandomDescription()
        var finalDescription = Component.text()

        if (randomDescription == null) {
            finalDescription.append(event.motd())
        } else {
            finalDescription
                .append(miniMessage.deserialize(randomDescription.line1))
                .append(Component.text("\n"))
                .append(miniMessage.deserialize(randomDescription.line2))
        }

        val p = configManager.hitokoto().probability
        val showHitokoto = nextBoolean(p)
        if (configManager.hitokoto().enabled && showHitokoto) {
            val color = configManager.hitokoto().color
            val hitokoto = hitokotoManager.getSentence()
            finalDescription = Component.text()
                .append(miniMessage.deserialize("<$color>${hitokoto.line1}"))
                .append(Component.text("\n"))
                .append(miniMessage.deserialize("<$color>${hitokoto.line2}"))
        }


        if (showHead && skinHeadManager.exists(ip) && configManager.ipFingerprint().enabled) {
            val userData = skinHeadManager.getHead(ip)
            val decodedHead = Base64.getDecoder().decode(userData.head)
            val bufferedHead = ImageIO.read(ByteArrayInputStream(decodedHead))
            favicon = server.loadServerIcon(bufferedHead)

            val randomBuildInDesc =
                configManager.getRandomBuildInDescription().split("\$player")
            finalDescription = Component.text()
                .append(miniMessage.deserialize(randomBuildInDesc[0]))
                .append(
                    Component.text(userData.name)
                        .style { style: Style.Builder -> style.color(TextColor.color(0xEE82EE)) })
                .append(miniMessage.deserialize(randomBuildInDesc[randomBuildInDesc.size - 1]))
        } else {
            val randomIcon = faviconManager.getRandomIcon()
            if (randomIcon != null) {
                favicon = server.loadServerIcon(randomIcon)
            }
        }

        event.motd(finalDescription.build())
        event.setServerIcon(favicon)
        if (configManager.pingList().maximumPlayerEnabled) {
            event.maxPlayers = configManager.pingList().maximumPlayer
        }

        if (configManager.pingPass().enabled) {
            if (pingRecordManager.exists(ip)) {
                pingRecordManager.removeRecord(ip)
            }
            pingRecordManager.addRecord(ip)
        }
    }
}