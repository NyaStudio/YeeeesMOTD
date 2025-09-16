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


package cn.rtast.yeeeesmotd

import cn.rtast.yeeeesmotd.utils.Favicon
import cn.rtast.yeeeesmotd.utils.SamplePlayerGenerator
import cn.rtast.yeeeesmotd.utils.file.*

interface IYeeeesMOTD {

    companion object {
        val faviconManager = FaviconManager()
        val skinHeadManager = SkinHeadManager()
        val pingRecordManager = PingRecordManager()
        val configManager = ConfigManager()
        val hitokotoManager = HitokotoManager(configManager.hitokoto().type)
        val defaultIcon = Favicon.getDefaultIcon()
        val fakeSamplePlayerGenerator = SamplePlayerGenerator()

        var PING_FIRST_TEXT = configManager.pingPass().pingFirstText
        var PING_AGAIN_TEXT = configManager.pingPass().pingAgainText
    }
}