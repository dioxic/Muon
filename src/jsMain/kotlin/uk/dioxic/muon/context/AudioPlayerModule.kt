package uk.dioxic.muon.context

import react.*
import uk.dioxic.muon.Routes
import uk.dioxic.muon.model.Track
import web.events.EventType
import web.html.Audio
import web.html.HTMLAudioElement

val PlayTrackContext = createContext<StateInstance<Track?>>()
val IsPlayingContext = createContext<Boolean>()
val TogglePlayStateContext = createContext<() -> Unit>()

val AudioPlayerModule = FC<PropsWithChildren> { props ->
    val playTrackState = useState<Track?>(null)
    val (playTrack, _) = playTrackState
    val (isPlaying, setIsPlaying) = useState(false)
    val audioRef = useRef<HTMLAudioElement>(null)

    fun attachListeners(audio: Audio) {
        with(audio) {
            addEventListener(type = EventType("ended"), callback = {
                setIsPlaying(false)
            })
        }
    }

    useEffect(playTrack) {
        audioRef.current?.pause()

        if (playTrack != null) {
            audioRef.current = Audio(Routes.trackAudio(playTrack)).also {
                attachListeners(it)
                it.play()
            }
            setIsPlaying(true)
        }
    }

    useEffect(*emptyArray()) {
        cleanup { audioRef.current?.pause() }
    }

    fun togglePlayState() {
        if (!isPlaying) {
            audioRef.current?.play()
            setIsPlaying(true)
        } else {
            audioRef.current?.pause()
            setIsPlaying(false)
        }
    }

    TogglePlayStateContext.Provider(::togglePlayState) {
        PlayTrackContext(playTrackState) {
            IsPlayingContext(isPlaying) {
                +props.children
            }
        }
    }
}
