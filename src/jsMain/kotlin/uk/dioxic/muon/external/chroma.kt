package uk.dioxic.muon.external

import csstype.Color

@JsModule("chroma-js")
@JsNonModule
external fun chroma(color: Color): ChromaColor

external class ChromaColor {
    fun alpha(alpha: Number): ChromaColor
    fun darken(value: Number = definedExternally): ChromaColor
    fun brighten(value: Number = definedExternally): ChromaColor
    fun saturate(value: Number = definedExternally): ChromaColor
    fun desaturate(value: Number = definedExternally): ChromaColor
    fun hex(mode: String = definedExternally): Color
}