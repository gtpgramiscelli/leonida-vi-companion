package com.leonida.gtavicompanion.data

import com.leonida.gtavicompanion.model.SyncDetection
import java.text.Normalizer

/**
 * Parses text read by ML Kit OCR from:
 *  1) PS5 TV Screens (Options > Progress / Stats / 100% Completion / Story Recap / WAiNK)
 *  2) PS5 TV Trophy List OR PlayStation App Trophy Screenshots (English & Portuguese)
 */
object GameScreenOcrParser {

    private fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
        return decomposed
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()
    }

    fun parse(rawText: String, sourceLabel: String = "Câmera na TV (OCR)"): SyncDetection {
        if (rawText.isBlank()) {
            return SyncDetection(sourceLabel = sourceLabel, rawOcrText = rawText)
        }

        val normalizedFull = normalize(rawText)
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        var detectedChapter: Int? = null
        var detectedWaink: Int? = null
        var detectedAnimals: Int? = null
        var detectedStickUps: Int? = null
        var detectedShootingGold: Int? = null
        val checklistIds = mutableSetOf<String>()
        val secretIds = mutableSetOf<String>()
        val cheatIds = mutableSetOf<String>()
        val trophyIds = mutableSetOf<String>()
        val highlights = mutableListOf<String>()

        fun bumpChapter(ch: Int) {
            detectedChapter = maxOf(detectedChapter ?: 0, ch.coerceIn(1, 6))
        }

        // 1. Detect explicit Chapter headers ("Chapter 3", "Capítulo 3", "Cap. 4")
        val chapterRegex = Regex("""\b(?:chapter|capitulo|cap\.?)\s*([1-5])\b""")
        chapterRegex.findAll(normalizedFull).forEach { match ->
            val num = match.groupValues[1].toIntOrNull()
            if (num != null) bumpChapter(num)
        }

        // 2. Detect Main Story mission completion "91/91" or "100% Completion"
        val storyMissions = extractFractionNear(
            lines,
            keywords = listOf("main story", "story missions", "historia principal", "missoes principais"),
            expectedDenoms = setOf(91)
        )
        if (storyMissions != null && storyMissions >= 91) {
            bumpChapter(6)
            checklistIds.add("story_main")
            highlights.add("História Principal: 91/91 concluída")
        }

        // 3. Detect Trophy names (EN & PT-BR — handles PS5 Trophy TV screen & PS App screenshots!)
        GtaViCatalog.ps5Trophies.forEach { trophy ->
            val enKey = normalize(trophy.nameEn).replace(Regex("[^a-z0-9 ]"), "")
            val ptKey = normalize(trophy.namePt).replace(Regex("[^a-z0-9 ]"), "")
            val cleanFull = normalizedFull.replace(Regex("[^a-z0-9\\s]"), " ")
            val matched = (enKey.length >= 6 && cleanFull.contains(enKey)) ||
                (ptKey.length >= 6 && cleanFull.contains(ptKey))
            if (matched) {
                trophyIds.add(trophy.id)
                highlights.add("Troféu: ${trophy.nameEn}")
            }
        }

        // 4. Detect numeric field counters
        extractFractionNear(
            lines,
            keywords = listOf("waink", "vehicle scan", "vehicles scanned", "veiculos escaneados", "escaneamento"),
            expectedDenoms = setOf(200, 277, 100)
        )?.let { count ->
            detectedWaink = count.coerceIn(0, 277)
            highlights.add("WAiNK: $detectedWaink veículos")
        }

        extractFractionNear(
            lines,
            keywords = listOf("animal", "animais", "fauna", "species", "especies", "binoculo"),
            expectedDenoms = setOf(100, 255)
        )?.let { count ->
            detectedAnimals = count.coerceIn(0, 255)
            highlights.add("Animais estudados: $detectedAnimals")
        }

        extractFractionNear(
            lines,
            keywords = listOf("stick up", "stick-up", "stickups", "assaltos rapidos", "conveniencia", "hold-up", "holdup"),
            expectedDenoms = setOf(50, 91)
        )?.let { count ->
            detectedStickUps = count.coerceIn(0, 91)
            highlights.add("Stick Ups: $detectedStickUps lojas")
        }

        extractFractionNear(
            lines,
            keywords = listOf("shooting range", "gun club", "estande de tiro", "clube de tiro"),
            expectedDenoms = setOf(42)
        )?.let { count ->
            detectedShootingGold = count.coerceIn(0, 42)
            highlights.add("Estande de Tiro: $detectedShootingGold/42 ouros")
        }

        // 5. Detect 100% Checklist thresholds from the official Progress > 100% menu
        checkThreshold(lines, listOf("optional task", "tarefas opcionais", "stranger"), setOf(100, 200, 300), 100) {
            checklistIds.add("story_optional")
            highlights.add("Tarefas Opcionais: meta 100+ concluída")
        }
        checkThreshold(lines, listOf("gang operation", "operacoes de gang", "gangue"), setOf(39), 39) {
            checklistIds.add("story_gangs")
            highlights.add("Operações de Gangues: 39/39")
        }
        checkThreshold(lines, listOf("bank job", "assaltos a banco", "bancos"), setOf(22), 22) {
            checklistIds.add("story_banks")
            highlights.add("Assaltos a Bancos: 22/22")
        }
        checkThreshold(lines, listOf("payday loan", "emprestimos"), setOf(7), 7) {
            checklistIds.add("story_payday")
            highlights.add("Payday Loans: 7/7")
        }
        checkThreshold(lines, listOf("smash", "joalheria", "penhor"), setOf(34, 35), 34) {
            checklistIds.add("story_smash")
            highlights.add("Smash 'n' Grabs: 34+/35")
        }
        checkThreshold(lines, listOf("kenton", "shipwreck", "naufragio"), setOf(5, 7), 5) {
            checklistIds.add("act_kenton")
            highlights.add("Naufrágios de Kenton: 5+/7")
        }
        checkThreshold(lines, listOf("bret", "hunting", "caca"), setOf(15), 15) {
            checklistIds.add("act_bret")
            highlights.add("Contratos de Caça do Bret: 15/15")
        }
        checkThreshold(lines, listOf("pool", "sinuca", "billiard"), setOf(5, 7), 5) {
            checklistIds.add("act_pool")
            highlights.add("Oponentes de Sinuca: 5+/7")
        }
        checkThreshold(lines, listOf("mini golf", "minigolfe"), setOf(2, 3), 2) {
            checklistIds.add("act_minigolf")
            highlights.add("Minigolfe: 2+/3")
        }
        checkThreshold(lines, listOf("challenge", "desafios gerais", "desafios"), setOf(70, 80), 70) {
            checklistIds.add("act_challenges")
            highlights.add("Desafios Gerais: 70+/80")
        }
        checkThreshold(lines, listOf("special vehicle", "manobras especiais", "maneuver"), setOf(300), 300) {
            checklistIds.add("act_maneuvers")
            highlights.add("Manobras Especiais: 300/300")
        }
        checkThreshold(lines, listOf("drone race", "corridas de drone", "drone"), setOf(100), 100) {
            checklistIds.add("act_drone")
            highlights.add("Corridas de Drone concluídas")
        }

        // If chapter was detected directly and wasn't already listed via trophy
        detectedChapter?.let { ch ->
            val label = if (ch >= 6) "Pós-Game (História Concluída)" else "Capítulo $ch"
            highlights.add(0, "Progresso detectado: $label")
        }

        return SyncDetection(
            sourceLabel = sourceLabel,
            detectedChapter = detectedChapter,
            detectedWaink = detectedWaink,
            detectedAnimals = detectedAnimals,
            detectedStickUps = detectedStickUps,
            detectedShootingGold = detectedShootingGold,
            detectedChecklistIds = checklistIds,
            detectedSecretIds = secretIds,
            detectedCheatIds = cheatIds,
            detectedTrophyIds = trophyIds,
            highlights = highlights.distinct(),
            rawOcrText = rawText
        )
    }

    private fun checkThreshold(
        lines: List<String>,
        keywords: List<String>,
        expectedDenoms: Set<Int>,
        minValue: Int,
        onMet: () -> Unit
    ) {
        val found = extractFractionNear(lines, keywords, expectedDenoms)
        if (found != null && found >= minValue) {
            onMet()
        }
    }

    /**
     * Looks for a keyword on line `i` and extracts a fraction `X / Y` on line `i` or `i + 1`
     * (because PS5 menus often align labels on the left and `124 / 200` on the right / next OCR block).
     */
    private fun extractFractionNear(
        lines: List<String>,
        keywords: List<String>,
        expectedDenoms: Set<Int>
    ): Int? {
        val fractionRegex = Regex("""(\d{1,3})\s*[/\\|]\s*(\d{1,3})""")
        val standaloneRegex = Regex("""\b(\d{1,3})\b""")

        for (i in lines.indices) {
            val normLine = normalize(lines[i])
            if (keywords.none { normLine.contains(it) }) continue

            // Check same line and next line for an explicit fraction X/Y
            val window = buildString {
                append(lines[i])
                if (i + 1 < lines.size) {
                    append(" ")
                    append(lines[i + 1])
                }
            }
            val matches = fractionRegex.findAll(window).toList()
            for (m in matches) {
                val num = m.groupValues[1].toIntOrNull() ?: continue
                val den = m.groupValues[2].toIntOrNull() ?: continue
                if (den in expectedDenoms && num <= den) {
                    return num
                }
            }

            // Fallback: exact percentage "100%" on the same/next line when expectedDenoms includes 100
            if (100 in expectedDenoms && window.contains("100%")) {
                return 100
            }

            // Fallback: standalone number after colon/dash on same line (e.g. "WAiNK: 84")
            val afterKeyword = normLine.substringAfter(":", "").ifBlank {
                normLine.substringAfter("-", "")
            }
            if (afterKeyword.isNotBlank()) {
                val n = standaloneRegex.find(afterKeyword)?.groupValues?.get(1)?.toIntOrNull()
                val maxDenom = expectedDenoms.maxOrNull() ?: 300
                if (n != null && n in 1..maxDenom) {
                    return n
                }
            }
        }
        return null
    }
}
