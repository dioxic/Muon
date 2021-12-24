package uk.dioxic.muon

//import org.spekframework.spek2.Spek
//import org.spekframework.spek2.style.gherkin.Feature

//object TagFeature : Spek({
//    Feature("Mp3 Tags") {
//        val f = File(ClassLoader.getSystemResource("02-instag8-the_roughest-abb9a9c4-klin.mp3").toURI())
//        val audioFile by memoized { AudioFileIO.read(f) }
//
//        Scenario("File header") {
//            lateinit var header: AudioHeader
//
//            When("reading header") {
//                header = audioFile.audioHeader
//            }
//
//            Then("track length should be 291") {
//                assertThat(header.trackLength).isEqualTo(291)
//            }
//
//            Then("bitrate should be 320") {
//                assertThat(header.bitRateAsNumber).isEqualTo(320)
//            }
//
//            Then("it should not be variable bit rate") {
//                assertThat(header.isVariableBitRate).isFalse()
//            }
//        }
//
//        Scenario("ID3 tags") {
//            lateinit var tag: Tag
//
//            When("read tags") {
//                tag = audioFile.tag
//            }
//
//            Then("title should be 'The Roughest'") {
//                assertThat(tag.title).isEqualTo("The Roughest")
//            }
//
//            Then("artists should contain 'Instag8' and 'artist2'") {
//                assertThat(tag.artists)
//                    .isInstanceOf(List::class.java)
//                    .contains("Instag8", "artist2")
//            }
//
//            Then("comments should equal 'some comments'") {
//                assertThat(tag.comment)
//                    .isEqualTo("some comments")
//            }
//
//            Then("album should equal 'The Unknown'") {
//                assertThat(tag.album)
//                    .isEqualTo("The Unknown")
//            }
//
//            Then ("year should be 2020") {
//                assertThat(tag.year)
//                    .isEqualTo("2020")
//            }
//
//            Then ("genre should be 'Drum & Bass'") {
//                assertThat(tag.genre)
//                    .isEqualTo("Drum & Bass")
//            }
//        }
//    }
//})