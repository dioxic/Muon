package uk.dioxic.muon.common

import mui.material.*

inline var GridProps.xs: Int
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().xs = value
    }

inline var TypographyProps.color: String
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().color = value
    }

inline var BoxProps.noValidate: Boolean
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().noValidate = value
    }

inline var BoxProps.autoComplete: String
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().autoComplete = value
    }

inline var TabsProps.ariaLabel: String
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic()["aria-label"] = value
    }

inline var TextFieldProps.InputProps: InputBaseProps
    get() = TODO("Prop is write-only!")
    set(value) {
        asDynamic().InputProps = value
    }