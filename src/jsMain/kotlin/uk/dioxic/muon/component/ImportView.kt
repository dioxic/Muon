package uk.dioxic.muon.component

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MIconButtonSize
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.list.*
import com.ccfraser.muirwik.components.styles.lighten
import com.ccfraser.muirwik.components.table.*
import kotlinext.js.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.*
import react.dom.div
import styled.StyleSheet
import styled.css
import styled.styledDiv
import uk.dioxic.muon.Column
import uk.dioxic.muon.api.getAudioImportConfig
import uk.dioxic.muon.api.getAudioImportList
import uk.dioxic.muon.api.saveAudioFile
import uk.dioxic.muon.api.saveAudioImportConfig
import uk.dioxic.muon.audio.AudioFile
import uk.dioxic.muon.audio.AudioFile.Keys.*
import uk.dioxic.muon.audio.findCommonFields
import uk.dioxic.muon.config.AudioImportConfig
import uk.dioxic.muon.orderDown
import uk.dioxic.muon.orderUp
import kotlin.math.min

private val scope = MainScope()

private fun comparator(
    a: AudioFile,
    b: AudioFile,
    order: MTableCellSortDirection,
    orderBy: AudioFile.Keys
) =
    when (order) {
        MTableCellSortDirection.asc -> AudioFile.comparator(a, b, orderBy)
        else -> AudioFile.comparator(b, a, orderBy)
    }

external interface MusicTableProps : Props {
    var filter: String
}

const val MULTIPLE = "Multiple"

val AudioFile.Companion.MULTI: AudioFile
    get() = build(MULTIPLE)

val MusicTable = fc<MusicTableProps> { props ->
    val (config, setConfig) = useState(AudioImportConfig.Default)
    val (musicList, setMusicList) = useState(emptyList<AudioFile>())
    val (selected, setSelected) = useState(emptyList<String>())
    val (editing, setEditing) = useState(AudioFile.BLANK)
    val (order, setOrder) = useState(MTableCellSortDirection.asc)
    val (orderBy, setOrderByColumn) = useState(Artist)
    val (page, setPage) = useState(0)
    val (rowsPerPage, setRowsPerPage) = useState(25)
    val (genreDialogOpen, setGenreDialogOpen) = useState(false)
    val (columnDialogOpen, setColumnDialogOpen) = useState(false)
    val (editDialogOpen, setEditDialogOpen) = useState(false)
    val (backdropOpen, setBackdropOpen) = useState(false)

    useEffectOnce {
        setBackdropOpen(true)
        scope.launch {
            setMusicList(getAudioImportList())
            setConfig(getAudioImportConfig())
        }.invokeOnCompletion {
            setBackdropOpen(false)
        }
    }

    fun saveConfig(config: AudioImportConfig) {
        println(config)
        setConfig(config)
        scope.launch {
            saveAudioImportConfig(config)
        }
    }

    fun handleSelectAll(selectAll: Boolean) {
        if (selectAll) {
            setSelected(musicList.map { music -> music.id })
        } else {
            setSelected(emptyList())
        }
    }

    fun handleRequestSort(field: AudioFile.Keys) {
        val isAsc = orderBy == field && order == MTableCellSortDirection.asc
        setOrder(if (isAsc) MTableCellSortDirection.desc else MTableCellSortDirection.asc)
        setOrderByColumn(field)
    }

    fun handleRowClick(field: String) {
        if (selected.contains(field)) {
            setSelected(selected.filterNot { it == field })
        } else {
            setSelected(selected + field)
        }
    }

    fun handleGenreButtonClick() {
        setGenreDialogOpen(true)
    }

    fun handleGenreDialogClose() {
        setGenreDialogOpen(false)
    }

    fun handleEditButtonClick(audioFile: AudioFile) {
        setEditing(audioFile)
        setEditDialogOpen(true)
    }

    fun handleGenreSelect(genre: String) {
        setGenreDialogOpen(false)
        setMusicList(musicList.map {
            if (selected.contains(it.id))
                it.copy(tags = it.tags.copy(genre = genre)).also {
                    scope.launch {
                        saveAudioFile(it)
                    }
                }
            else it
        }.toList())
    }

    fun handleEditMultiple() {
        setEditing(AudioFile.MULTI.copy(
            tags = findCommonFields(
                MULTIPLE,
                musicList.filter { selected.contains(it.id) }.map { it.tags }),
            header = findCommonFields(musicList.filter { selected.contains(it.id) }.map { it.header }),
            location = findCommonFields(
                MULTIPLE,
                musicList.filter { selected.contains(it.id) }.map { it.location }
            )
        )
        )
        setEditDialogOpen(true)
    }

    fun handleClearComments() {
        setMusicList(musicList.map {
            if (selected.contains(it.id) && it.tags.comment.isNotEmpty()) {
                it.copy(tags = it.tags.copy(comment = "")).also {
                    scope.launch {
                        saveAudioFile(it)
                    }
                }
            } else it
        }.toList())
    }

    fun handleRefresh() {
        setBackdropOpen(true)
        scope.launch {
            setMusicList(getAudioImportList(reload = true))
        }.invokeOnCompletion {
            setBackdropOpen(false)
        }
    }

    fun handleColumnVisibilityChange(key: AudioFile.Keys, visible: Boolean) {
        saveConfig(config.copy(
            columns = config.columns.toMutableMap().apply {
                this[key] = this[key]!!.copy(visible = visible)
            }
        ))
    }

    fun handleColumnReorderUp(key: AudioFile.Keys) {
        saveConfig(
            config.copy(
                columns = config.columns.orderUp(key)
            )
        )
    }

    fun handleColumnReorderDown(key: AudioFile.Keys) {
        saveConfig(
            config.copy(
                columns = config.columns.orderDown(key)
            )
        )
    }

    mPaper {
        css {
            width = 100.pct
            marginTop = 3.spacingUnits
        }

        enhancedTableToolbar(
            numSelected = selected.size,
            onGenreClick = ::handleGenreButtonClick,
            onClearCommentsClick = ::handleClearComments,
            onEditMultipleClick = ::handleEditMultiple,
            onRefreshClick = ::handleRefresh,
            onFilterClick = { setColumnDialogOpen(true) }
        )
        styledDiv {
            css { overflowX = Overflow.auto }
            mTable {
                attrs.size = MTableCellSize.small
                css { minWidth = 700.px }
                enhancedTableHead(
                    numSelected = selected.size,
                    order = order,
                    orderBy = orderBy,
                    rowCount = musicList.size,
                    columns = config.columns,
                    onSelectAllClick = ::handleSelectAll,
                    onRequestSort = ::handleRequestSort,
                )
                mTableBody {
                    val filtedMusicList = if (props.filter.isNotBlank())
                        musicList.filter { it.matches(props.filter) }
                    else
                        musicList

                    filtedMusicList
                        .sortedWith { a, b -> comparator(a, b, order, orderBy) }
                        .subList(page * rowsPerPage, min((page + 1) * rowsPerPage, filtedMusicList.size))
                        .forEach { music ->
                            val isSelected = selected.contains(music.id)
                            mTableRow(music.id, isSelected, true, onClick = { event ->
                                if (!listOf("edit").contains(event.target.asDynamic().innerText as String)) {
                                    handleRowClick(music.id)
                                }
                            }) {
                                mTableCell(padding = MTableCellPadding.checkbox) {
                                    mCheckbox(isSelected)
                                }
                                config.columns.filter { (_, column) -> column.visible }
                                    .forEach { (id, column) ->
                                        mTableCell(
                                            key = id,
                                            align = if (column.rightAligned) MTableCellAlign.right else MTableCellAlign.left,
                                            padding = if (column.disablePadding) MTableCellPadding.none else MTableCellPadding.default,
                                        ) { +music.get(id) }
                                    }
                                mTableCell(
                                    padding = MTableCellPadding.default,
                                    align = MTableCellAlign.center
                                ) {
                                    mIconButton("edit", size = MIconButtonSize.small, onClick = {
                                        handleEditButtonClick(music)
                                    })
                                }
                            }
                        }
                }
            }
        }
        mTablePagination(
            page = page,
            count = musicList.size,
            rowsPerPage = rowsPerPage,
            onChangePage = { _, newPage -> setPage(newPage) },
            onChangeRowsPerPage = { event ->
                setRowsPerPage(event.target.asDynamic().value as Int)
                setPage(0)
            }
        )
    }
    genreDialog(genreDialogOpen, ::handleGenreDialogClose, ::handleGenreSelect)
    columnDialog(
        columnDialogOpen,
        { setColumnDialogOpen(false) },
        config.columns,
        ::handleColumnVisibilityChange,
        ::handleColumnReorderUp,
        ::handleColumnReorderDown
    )
    backdrop(backdropOpen)
    child(MusicEdit, props = jsObject {
        open = editDialogOpen
        initialState = editing
        showFilename = (editing.id != MULTIPLE)
        title = if (editing.id == MULTIPLE) "Edit Multiple" else "Edit"
        onChange = { audioState -> setEditing(audioState) }
        onSave = {
            if (editing.id == MULTIPLE) {
                setMusicList(musicList.map {
                    if (selected.contains(it.id)) {
                        it.merge(editing, ignoreText = MULTIPLE).also {
                            scope.launch {
                                saveAudioFile(it)
                            }
                        }
                    } else {
                        it
                    }
                }.toList())
            } else {
                setMusicList(musicList.map {
                    if (editing.id == it.id) {
                        editing.also {
                            scope.launch {
                                saveAudioFile(it)
                            }
                        }
                    } else it
                }.toList())
            }
            setEditDialogOpen(false)
        }
        onClose = { setEditDialogOpen(false) }
    })
}

private fun RBuilder.backdrop(
    open: Boolean
) {
    themeContext.Consumer { theme ->
        val styles = object : StyleSheet("BackdropStyles", isStatic = true) {
            val backdrop by css {
                zIndex = theme.zIndex.drawer + 1
                color = Color("#fff")
            }
        }

        mBackdrop(open) {
            css(styles.backdrop)
            mCircularProgress()
        }
    }
}

private fun RBuilder.enhancedTableToolbar(
    numSelected: Int,
    onGenreClick: () -> Unit,
    onClearCommentsClick: () -> Unit,
    onEditMultipleClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    themeContext.Consumer { theme ->
        val styles = object : StyleSheet("ToolbarStyles", isStatic = true) {
            val spacer by css {
                flex(1.0, 1.0, 80.pct)
            }
            val highlight by css {
                if (theme.palette.type == "light") {
                    color = Color(theme.palette.secondary.main)
                    backgroundColor = Color(lighten(theme.palette.secondary.light, 0.85))
                } else {
                    color = Color(theme.palette.text.primary)
                    backgroundColor = Color(theme.palette.secondary.dark)
                }
            }
            val actions by css {
                color = Color(theme.palette.text.secondary)
            }
        }

        mToolbar {
            if (numSelected > 0) css(styles.highlight)
            styledDiv {
                css { flex(0.0, 0.0, FlexBasis.auto) }
                if (numSelected > 0) {
                    mTypography("$numSelected selected", variant = MTypographyVariant.subtitle1)
                } else {
                    mTypography("Music Import", variant = MTypographyVariant.h6)
                }
            }
            styledDiv { css(styles.spacer) }
            styledDiv {
                css(styles.actions)
                if (numSelected > 0) {
                    mTooltip("Edit") {
                        mIconButton("edit_rounded", onClick = { onEditMultipleClick() })
                    }
                    mTooltip("Clear Comments") {
                        mIconButton("comment_rounded", onClick = { onClearCommentsClick() })
                    }
                    mTooltip("Set Genre") {
                        mIconButton("music_note_rounded", onClick = { onGenreClick() })
                    }
                    mTooltip("Rename") {
                        mIconButton("spellcheck_rounded")
                    }
                    mTooltip("Import") {
                        mIconButton("get_app_rounded")
                    }
                } else {
                    mTooltip("Filter") {
                        mIconButton("filter_list_rounded", onClick = { onFilterClick() })
                    }
                    mTooltip("Refresh") {
                        mIconButton("refresh_rounded", onClick = { onRefreshClick() })
                    }
                }
            }
        }
    }
}

private fun RBuilder.enhancedTableHead(
    numSelected: Int,
    order: MTableCellSortDirection,
    orderBy: AudioFile.Keys,
    rowCount: Int,
    columns: Map<AudioFile.Keys, Column>,
    onSelectAllClick: (checked: Boolean) -> Unit,
    onRequestSort: (id: AudioFile.Keys) -> Unit
) {
    mTableHead {
        mTableRow {
            mTableCell(padding = MTableCellPadding.checkbox) {
                mCheckbox(
                    indeterminate = numSelected in 1 until rowCount,
                    checked = numSelected == rowCount,
                    onChange = { _, checked -> onSelectAllClick(checked) }
                )
            }
            columns.filter { (_, column) -> column.visible }.forEach { (id, column) ->
                mTableCell(
                    key = id,
                    align = if (column.rightAligned) MTableCellAlign.right else MTableCellAlign.left,
                    padding = if (column.disablePadding) MTableCellPadding.none else MTableCellPadding.default,
                    sortDirection = if (orderBy == id) order else MTableCellSortDirection.False
                ) {
                    mTooltip(
                        title = "Sort",
                        placement = if (column.rightAligned) TooltipPlacement.bottomEnd else TooltipPlacement.bottomStart,
                        enterDelay = 300
                    ) {
                        mTableSortLabel(
                            label = column.label,
                            active = orderBy == id,
                            direction = if (order == MTableCellSortDirection.asc) MTableSortLabelDirection.asc else MTableSortLabelDirection.desc,
                            onClick = { onRequestSort(id) }
                        )
                    }
                }
            }
            mTableCell(padding = MTableCellPadding.checkbox) { +"Actions" }
        }
    }
}

private fun RBuilder.columnDialog(
    dialogOpen: Boolean,
    onDialogClose: () -> Unit,
    columns: Map<AudioFile.Keys, Column>,
    onVisibilityChange: (AudioFile.Keys, Boolean) -> Unit,
    onMoveUp: (AudioFile.Keys) -> Unit,
    onMoveDown: (AudioFile.Keys) -> Unit,
) {
    themeContext.Consumer { theme ->
        val styles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val listDiv by css {
                display = Display.inlineFlex
                padding(1.spacingUnits)
            }
            val inline by css {
                display = Display.inlineBlock
            }
            val list by css {
                width = 320.px
                backgroundColor = Color(theme.palette.background.paper)
            }
        }
        mDialog(dialogOpen, onClose = { _, _ -> onDialogClose() }) {
            mDialogTitle("Column Config")
            div {
//                css(styles.listDiv)
                mList(dense = true) {
                    css(styles.list)
                    columns.toList()
                        .forEachIndexed { idx, (id, column) ->
                            mListItem(button = false) {
                                mCheckbox(
                                    column.visible,
                                    onChange = { _, visible -> onVisibilityChange(id, visible) })
                                mListItemText(column.label)
                                mListItemSecondaryAction {
                                    if (idx > 0) {
                                        mIconButton(
                                            "arrow_upward_rounded",
                                            size = MIconButtonSize.small,
                                            onClick = { onMoveUp(id) })
                                    }
                                    if (idx < columns.size - 1) {
                                        mIconButton(
                                            "arrow_downward_rounded",
                                            size = MIconButtonSize.small,
                                            onClick = { onMoveDown(id) })
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

private fun RBuilder.genreDialog(
    dialogOpen: Boolean,
    onDialogClose: () -> Unit,
    onGenreSelect: (genre: String) -> Unit
) {
    themeContext.Consumer { theme ->
        val styles = object : StyleSheet("ComponentStyles", isStatic = true) {
            val avatarStyle by css {
                backgroundColor = Color(lighten(theme.palette.secondary.light, 0.85))
                color = Color(theme.palette.text.secondary)
            }
        }
        val genres = listOf("Drum & Bass", "Trance", "Dubstep")
        mDialog(dialogOpen, onClose = { _, _ -> onDialogClose() }) {
            mDialogTitle("Set Genre")
            div {
                mList {
                    genres.forEach { genre ->
                        mListItem(button = true, onClick = { onGenreSelect(genre) }) {
                            mListItemAvatar {
                                mAvatar {
                                    css(styles.avatarStyle)
                                    mIcon("person")
                                }
                            }
                            mListItemText(primary = genre)
                        }
                    }
                    mListItemWithIcon(
                        iconName = "add",
                        primaryText = "add genre",
                        divider = false,
                        useAvatar = true,
                        onClick = { onGenreSelect("add genre") })
                }
            }
        }
    }

}