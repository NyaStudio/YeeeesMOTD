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


package cn.rtast.yeeeesmotd.velocity

import cn.rtast.yeeeesmotd.IYeeeesMOTD
import cn.rtast.yeeeesmotd.IYeeeesMOTD.Companion.faviconManager
import cn.rtast.yeeeesmotd.velocity.command.YeeeesMOTDCommand.createCommand
import cn.rtast.yeeeesmotd.velocity.listeners.LoginEventListener
import cn.rtast.yeeeesmotd.velocity.listeners.ProxyPingEventListener
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.minimessage.MiniMessage
import org.slf4j.Logger

class YeeeesMOTDPlugin @Inject constructor(logger: Logger, private val proxy: ProxyServer) : IYeeeesMOTD {

    companion object {
        val miniMessage = MiniMessage.miniMessage()
    }

    init {
        faviconManager.setValidIcons()
    }

    @Subscribe
    @Suppress("UNUSED_PARAMETER")
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        this.proxy.eventManager.register(this, ProxyPingEventListener())
        this.proxy.eventManager.register(this, LoginEventListener(this.proxy))

        val commandManager = proxy.commandManager
        val commandMeta = commandManager.metaBuilder("yeeeesmotd")
            .plugin(this)
            .build()
        val yesMOTDCommand = createCommand()
        commandManager.register(commandMeta, yesMOTDCommand)
    }
}
