package com.hanscauwenbergh.filter

import com.adamratzman.spotify.models.AudioFeatures

interface AudioFeatureFilterRule<V> {

    val limitFilterRules: List<LimitFilterRule<V>>

    fun filtersOut(audioFeatures: AudioFeatures): Boolean {
        return limitFilterRules.any { limitFilterRule -> limitFilterRule.filtersOut(getValue(audioFeatures)) }
    }

    fun getValue(audioFeatures: AudioFeatures): V
}

// A confidence measure from 0.0 to 1.0 of whether the track is acoustic. 1.0 represents high confidence the track is acoustic.
// (>= 0 <= 1)
data class AcousticnessFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.acousticness
}

// Danceability describes how suitable a track is for dancing based on a combination of musical elements including tempo, rhythm stability, beat strength, and overall regularity. A value of 0.0 is least danceable and 1.0 is most danceable.
// (>= 0 <= 1)
data class DanceabilityFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.danceability
}

// Energy is a measure from 0.0 to 1.0 and represents a perceptual measure of intensity and activity. Typically, energetic tracks feel fast, loud, and noisy. For example, death metal has high energy, while a Bach prelude scores low on the scale. Perceptual features contributing to this attribute include dynamic range, perceived loudness, timbre, onset rate, and general entropy.
// (>= 0 <= 1)
data class EnergyFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.energy
}

// Predicts whether a track contains no vocals. "Ooh" and "aah" sounds are treated as instrumental in this context. Rap or spoken word tracks are clearly "vocal". The closer the instrumentalness value is to 1.0, the greater likelihood the track contains no vocal content. Values above 0.5 are intended to represent instrumental tracks, but confidence is higher as the value approaches 1.0.
// (>= 0 <= 1)
data class InstrumentalnessFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.instrumentalness
}

// Detects the presence of an audience in the recording. Higher liveness values represent an increased probability that the track was performed live. A value above 0.8 provides strong likelihood that the track is live.
// (>= 0 <= 1)
data class LivenessFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.liveness
}

// The overall loudness of a track in decibels (dB). Loudness values are averaged across the entire track and are useful for comparing relative loudness of tracks. Loudness is the quality of a sound that is the primary psychological correlate of physical strength (amplitude). Values typically range between -60 and 0 db.
// (>= -60 <= 0)
data class LoudnessFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.loudness
}

// Mode indicates the modality (major or minor) of a track, the type of scale from which its melodic content is derived. Major is represented by 1 and minor is 0.
// (>= 0 <= 1)
data class ModeFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Int>>
) : AudioFeatureFilterRule<Int> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.mode
}

// Speechiness detects the presence of spoken words in a track. The more exclusively speech-like the recording (e.g. talk show, audio book, poetry), the closer to 1.0 the attribute value. Values above 0.66 describe tracks that are probably made entirely of spoken words. Values between 0.33 and 0.66 describe tracks that may contain both music and speech, either in sections or layered, including such cases as rap music. Values below 0.33 most likely represent music and other non-speech-like tracks.
// (>= 0 <= 1)
data class SpeechinessFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.speechiness
}

// The overall estimated tempo of a track in beats per minute (BPM). In musical terminology, tempo is the speed or pace of a given piece and derives directly from the average beat duration.
// (>= 0)
data class TempoFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.tempo
}

// An estimated time signature. The time signature (meter) is a notational convention to specify how many beats are in each bar (or measure). The time signature ranges from 3 to 7 indicating time signatures of "3/4", to "7/4".
// (>= 3 <= 7)
data class TimeSignatureFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Int>>
) : AudioFeatureFilterRule<Int> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.timeSignature
}

// A measure from 0.0 to 1.0 describing the musical positiveness conveyed by a track. Tracks with high valence sound more positive (e.g. happy, cheerful, euphoric), while tracks with low valence sound more negative (e.g. sad, depressed, angry).
// (>= 0 <= 1)
data class ValenceFilterRule(
    override val limitFilterRules: List<LimitFilterRule<Float>>
) : AudioFeatureFilterRule<Float> {
    override fun getValue(audioFeatures: AudioFeatures) = audioFeatures.valence
}

interface LimitFilterRule<V> {
    fun filtersOut(value: V): Boolean
}

data class MaxLimitFilterRule<V : Comparable<V>>(private val maxValue: V) : LimitFilterRule<V> {
    override fun filtersOut(value: V): Boolean {
        return value > maxValue
    }
}

data class MinLimitFilterRule<V : Comparable<V>>(private val minValue: V) : LimitFilterRule<V> {
    override fun filtersOut(value: V): Boolean {
        return value < minValue
    }
}