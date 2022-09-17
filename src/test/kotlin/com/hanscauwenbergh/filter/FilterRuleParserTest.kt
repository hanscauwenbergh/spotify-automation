package com.hanscauwenbergh.filter

import com.hanscauwenbergh.common.*
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FilterRuleParserTest {

    private lateinit var filterRuleParser: FilterRuleParser

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        filterRuleParser = FilterRuleParser()
    }

    @Test
    fun `parseFilterRules should return correct filter rules`() {

        val filterRulesAsString = "acousticness: min 0.0225, energy: min 0.6 max 0.9, loudness: max -5.5, speechiness: max 0.2, tempo: max 120, valence: min 0.5"

        val filterRules = filterRuleParser.parseFilterRules(filterRulesAsString)

        assertThat(filterRules).containsExactly(
            AcousticnessFilterRule(
                limitFilterRules = listOf(
                    MinLimitFilterRule(0.0225f),
                ),
            ),
            EnergyFilterRule(
                limitFilterRules = listOf(
                    MinLimitFilterRule(0.6f),
                    MaxLimitFilterRule(0.9f),
                ),
            ),
            LoudnessFilterRule(
                limitFilterRules = listOf(
                    MaxLimitFilterRule(-5.5f),
                ),
            ),
            SpeechinessFilterRule(
                limitFilterRules = listOf(
                    MaxLimitFilterRule(0.2f),
                ),
            ),
            TempoFilterRule(
                limitFilterRules = listOf(
                    MaxLimitFilterRule(120f),
                ),
            ),
            ValenceFilterRule(
                limitFilterRules = listOf(
                    MinLimitFilterRule(0.5f),
                ),
            ),
        )
    }
}