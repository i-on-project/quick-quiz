package pt.isel.ps.qq.utils

import pt.isel.ps.qq.service.MainDataService


fun getAppHost(): String = System.getenv("QQ_HOST") ?: "http://localhost:8080"

fun calculateLastPage(total: Long): Int = ((total.toDouble() / MainDataService.PAGE_SIZE) + 0.5).toInt()

fun getCurrentTimeSeconds(): Long = System.currentTimeMillis() / 1000
