package uk.dioxic.muon

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
//import org.spekframework.spek2.Spek
//import org.spekframework.spek2.style.gherkin.Feature

//object SerializationFeature : Spek({
//    Feature("Serialization") {
//
//        Scenario("Serialize Config") {
//            lateinit var config: Config
//
//            When("serializing config") {
//                config = Config.Default
//            }
//
//            Then("serialization should be a string") {
//                assertThat(Json.encodeToString(config)).isInstanceOf(String::class.java)
//            }
//
//            Then("serialization should be equal to xxx") {
//                assertThat(Json.encodeToString(config)).isEqualTo("""
//                    {"importColumnConfig":[{"id":"OriginalArtist","label":"Original Artist","visible":false},{"id":"OriginalTitle","label":"Original Title","visible":false},{"id":"OriginalGenre","label":"Original Genre","visible":false},{"id":"OriginalComment","label":"Original Comment","visible":false},{"id":"OriginalLyricist","label":"Original Lyricist","visible":false},{"id":"OriginalYear","label":"Original Year","visible":false},{"id":"OriginalAlbum","label":"Original Album","visible":false},{"id":"OriginalFilename","label":"Original Filename","visible":false},{"id":"StandardizedArtist","label":"Artist","visible":true},{"id":"StandardizedTitle","label":"Title","visible":true},{"id":"StandardizedGenre","label":"Genre","visible":true},{"id":"StandardizedComment","label":"Comment","visible":true},{"id":"StandardizedLyricist","label":"Lyricist","visible":true},{"id":"StandardizedYear","label":"Year","visible":true},{"id":"StandardizedAlbum","label":"Album","visible":true},{"id":"StandardizedFilename","label":"Filename","visible":true},{"id":"Length","label":"Length","visible":true},{"id":"Bitrate","label":"Bitrate","visible":true},{"id":"VBR","label":"VBR","visible":true},{"id":"Type","label":"Type","visible":true}]}
//                """.trimIndent())
//            }
//        }
//
//        Scenario("Deserialize Audio File Import Config") {
//            lateinit var configJson: String
//
//            When("deserializing config") {
//                configJson = """
//                    {"importColumnConfig":[{"id":"OriginalArtist","label":"Original Artist","visible":false},{"id":"OriginalTitle","label":"Original Title","visible":false},{"id":"OriginalGenre","label":"Original Genre","visible":false},{"id":"OriginalComment","label":"Original Comment","visible":false},{"id":"OriginalLyricist","label":"Original Lyricist","visible":false},{"id":"OriginalYear","label":"Original Year","visible":false},{"id":"OriginalAlbum","label":"Original Album","visible":false},{"id":"OriginalFilename","label":"Original Filename","visible":false},{"id":"StandardizedArtist","label":"Artist","visible":true},{"id":"StandardizedTitle","label":"Title","visible":true},{"id":"StandardizedGenre","label":"Genre","visible":true},{"id":"StandardizedComment","label":"Comment","visible":true},{"id":"StandardizedLyricist","label":"Lyricist","visible":true},{"id":"StandardizedYear","label":"Year","visible":true},{"id":"StandardizedAlbum","label":"Album","visible":true},{"id":"StandardizedFilename","label":"Filename","visible":true},{"id":"Length","label":"Length","visible":true},{"id":"Bitrate","label":"Bitrate","visible":true},{"id":"VBR","label":"VBR","visible":true},{"id":"Type","label":"Type","visible":true}]}
//                """.trimIndent()
//            }
//
//            Then("serialization should be a Config class") {
//                assertThat(Json.decodeFromString<Config>(configJson)).isInstanceOf(Config::class.java)
//            }
//
//            Then("serialization should be equal to xxx") {
//                assertThat(Json.decodeFromString<Config>(configJson)).isEqualTo(Config.Default)
//            }
//        }
//    }
//})