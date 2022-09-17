package com.hanscauwenbergh.filter

class FilterRuleParser {

    fun parseFilterRules(filterRulesAsString: String): List<AudioFeatureFilterRule<*>> {

        return filterRulesAsString
            .split(",")
            .map { filterRuleAsString -> filterRuleAsString.trim() }
            .mapNotNull { trimmedFilterRuleAsString -> parseAudioFeatureFilterRule(trimmedFilterRuleAsString) }
    }

    private fun parseAudioFeatureFilterRule(
        filterRuleAsString: String
    ): AudioFeatureFilterRule<*>? {

        val (audioFeatureAsString, limitFilterRulesAsString) = filterRuleAsString.split(":")

        val unparsedLimitFilterRules = limitFilterRulesAsString
            .trim()
            .split(" ")
            .windowed(2, 2)

        return when (audioFeatureAsString) {
            "acousticness" -> AcousticnessFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "danceability"-> DanceabilityFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "energy" -> EnergyFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "instrumentalness" -> InstrumentalnessFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "liveness" -> LivenessFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "loudness" -> LoudnessFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "mode" -> ModeFilterRule(
                limitFilterRules = parseIntLimitFilterRules(unparsedLimitFilterRules)
            )
            "speechiness" -> SpeechinessFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "tempo" -> TempoFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            "time_signature" -> TimeSignatureFilterRule(
                limitFilterRules = parseIntLimitFilterRules(unparsedLimitFilterRules)
            )
            "valence" -> ValenceFilterRule(
                limitFilterRules = parseFloatLimitFilterRules(unparsedLimitFilterRules)
            )
            else -> null
        }
    }

    private fun parseFloatLimitFilterRules(unparsedLimitFilterRules: List<List<String>>): List<LimitFilterRule<Float>> {
        return unparsedLimitFilterRules.mapNotNull { (limitTypeAsString, limitValueAsString) -> parseLimitFilterRule(limitTypeAsString, limitValueAsString.toFloat()) }
    }

    private fun parseIntLimitFilterRules(unparsedLimitFilterRules: List<List<String>>): List<LimitFilterRule<Int>> {
        return unparsedLimitFilterRules.mapNotNull { (limitTypeAsString, limitValueAsString) -> parseLimitFilterRule(limitTypeAsString, limitValueAsString.toInt()) }
    }

    private fun <V : Comparable<V>> parseLimitFilterRule(limitTypeAsString: String, limitValue: V): LimitFilterRule<V>? {

        return when (limitTypeAsString) {
            "min" -> MinLimitFilterRule(limitValue)
            "max" -> MaxLimitFilterRule(limitValue)
            else -> null
        }
    }
}
