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


package cn.rtast.yeeeesmotd.utils.file

import cn.rtast.yeeeesmotd.entity.PingRecord
import java.time.Clock
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PingRecordManager(
    private val clock: Clock = Clock.systemUTC(),
    private val expiration: Duration = Duration.ofMinutes(10),
    cleanupInterval: Duration = Duration.ofMinutes(5),
) {

    private val records: MutableMap<String, Long> = ConcurrentHashMap()
    private val cleanupScheduler = Executors.newSingleThreadScheduledExecutor { runnable ->
        Thread(runnable, "PingRecordCleaner").apply { isDaemon = true }
    }
    private val cleanupIntervalSeconds = cleanupInterval
        .takeIf { !it.isNegative && !it.isZero }
        ?.seconds
        ?.coerceAtLeast(1)
        ?: 60

    init {
        cleanupScheduler.scheduleAtFixedRate(
            { cleanupExpiredRecords() },
            cleanupIntervalSeconds,
            cleanupIntervalSeconds,
            TimeUnit.SECONDS,
        )
    }

    private fun currentEpochSecond(): Long {
        return this.clock.instant().epochSecond
    }

    fun addRecord(ip: String) {
        cleanupExpiredRecords()
        this.records[ip] = this.currentEpochSecond()
    }

    fun removeRecord(ip: String) {
        cleanupExpiredRecords()
        this.records.remove(ip)
    }

    fun exists(ip: String): Boolean {
        cleanupExpiredRecords()
        return this.records.containsKey(ip)
    }

    fun getRecord(ip: String): PingRecord {
        cleanupExpiredRecords()
        val timestamp = this.records[ip] ?: this.currentEpochSecond()
        return PingRecord(ip, timestamp)
    }

    private fun cleanupExpiredRecords() {
        val expirationSeconds = expiration
            .takeIf { !it.isNegative && !it.isZero }
            ?.seconds
            ?.coerceAtLeast(1)
            ?: return
        val threshold = this.currentEpochSecond() - expirationSeconds
        this.records.entries.removeIf { (_, lastPing) -> lastPing <= threshold }
    }
}
