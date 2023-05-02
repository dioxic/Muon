package uk.dioxic.muon.context

import react.*
import uk.dioxic.muon.Routes
import uk.dioxic.muon.model.Track
import web.html.Audio
import web.html.HTMLAudioElement

val PlayTrackContext = createContext<StateInstance<Track?>>()
val IsPlayingContext = createContext<StateInstance<Boolean>>()

val AudioPlayerModule = FC<PropsWithChildren> { props ->
    val playTrackState = useState<Track?>(null)
    val isPlayingState = useState(false)
    val (playTrack, _) = playTrackState
    val (isPlaying, _) = isPlayingState
    val audioRef = useRef<HTMLAudioElement>(null)

    useEffect(playTrack) {
        audioRef.current?.pause()

        if (playTrack != null) {
            audioRef.current = Audio(Routes.trackAudio(playTrack))
        }
    }

    useEffect(isPlaying, playTrack) {
        if (isPlaying) {
            audioRef.current?.play()
        } else {
            audioRef.current?.pause()
        }
    }

    PlayTrackContext(playTrackState) {
        IsPlayingContext(isPlayingState) {
            +props.children
        }
    }
}
