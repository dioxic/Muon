package uk.dioxic.muon

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.*
import com.ccfraser.muirwik.components.list.*
import com.ccfraser.muirwik.components.styles.lighten
import com.ccfraser.muirwik.components.table.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.*
import react.dom.div
import styled.StyleSheet
import styled.css
import styled.styledDiv
import uk.dioxic.muon.MusicFileField.*
import kotlin.math.min

private val scope = MainScope()

private data class Column(
    val id: MusicFileField,
    val rightAligned: Boolean,
    val disablePadding: Boolean,
    val label: String,
    val visible: Boolean = true
)

private val columns = listOf(
    Column(Artist, rightAligned = false, disablePadding = true, label = "Artist"),
    Column(Title, rightAligned = false, disablePadding = false, label = "Title"),
    Column(Genre, rightAligned = false, disablePadding = false, label = "Genre"),
    Column(Comment, rightAligned = false, disablePadding = false, label = "Comment"),
    Column(Length, rightAligned = true, disablePadding = false, label = "Length"),
    Column(Bitrate, rightAligned = true, disablePadding = false, label = "Bitrate"),
    Column(VBR, rightAligned = true, disablePadding = false, label = "VBR"),
    Column(Type, rightAligned = false, disablePadding = false, label = "Type"),
    Column(Filename, rightAligned = false, disablePadding = false, label = "Filename"),
)

private fun comparator(a: MusicFile, b: MusicFile, order: MTableCellSortDirection, orderBy: MusicFileField) =
    when (order) {
        MTableCellSortDirection.asc -> MusicFile.comparator(a, b, orderBy)
        else -> MusicFile.comparator(b, a, orderBy)
    }

val MusicTable = functionalComponent<RProps> {
    val (musicList, setMusicList) = useState(emptyList<MusicFile>())
    val (selected, setSelected) = useState(emptyList<String>())
    val (order, setOrder) = useState(MTableCellSortDirection.asc)
    val (orderBy, setOrderByColumn) = useState(Artist)
    val (page, setPage) = useState(0)
    val (rowsPerPage, setRowsPerPage) = useState(100)
    val (genreDialogOpen, setGenreDialogOpen) = useState(false)
    val (backdropOpen, setBackdropOpen) = useState(false)

    useEffect(dependencies = listOf()) {
        setBackdropOpen(true)
        scope.launch {
            setMusicList(getMusicList())
        }.invokeOnCompletion {
            setBackdropOpen(false)
        }
    }

    fun handleSelectAll(selectAll: Boolean) {
        if (selectAll) {
            setSelected(musicList.map { music -> music.path })
        } else {
            setSelected(emptyList())
        }
    }

    fun handleRequestSort(field : MusicFileField) {
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

    fun handleGenreSelect(genre :String) {
        setGenreDialogOpen(false)
        setMusicList(musicList.map { if (selected.contains(it.path)) it.copy(genre = genre) else it }.toList())
    }

    fun handleClearComments() {
        setMusicList(musicList.map { if (selected.contains(it.path)) it.copy(comment = "") else it }.toList())
    }

    fun handleRefresh() {
        setBackdropOpen(true)
        scope.launch {
            setMusicList(getMusicList())
        }.invokeOnCompletion {
            setBackdropOpen(false)
        }
    }

    mPaper {
        css {
            width = 100.pct
            marginTop = 3.spacingUnits
        }

        enhancedTableToolbar(selected.size, ::handleGenreButtonClick, ::handleClearComments, ::handleRefresh)
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
                    onSelectAllClick = ::handleSelectAll,
                    onRequestSort = ::handleRequestSort,
                )
                mTableBody {
                    musicList.sortedWith { a, b -> comparator(a, b, order, orderBy) }
                        .subList(page * rowsPerPage, min((page + 1) * rowsPerPage, musicList.size))
                        .forEach { music ->
                            val isSelected = selected.contains(music.path)
                            mTableRow(music.path, isSelected, true, onClick = { handleRowClick(music.path) }) {
                                mTableCell(padding = MTableCellPadding.checkbox) {
                                    mCheckbox(isSelected)
                                }
                                columns.filter { it.visible }.forEach { column ->
                                    mTableCell(
                                        key = column.id,
                                        align = if (column.rightAligned) MTableCellAlign.right else MTableCellAlign.left,
                                        padding = if (column.disablePadding) MTableCellPadding.none else MTableCellPadding.default,
                                    ) { +music.get(column.id) }
                                }
                            }
                        }
                    val emptyRows = rowsPerPage - min(rowsPerPage, musicList.size - page * rowsPerPage)
                    if (emptyRows > 0) {
                        mTableRow {
                            css { height = (49 * emptyRows).px }
                            mTableCell(colSpan = 6)
                        }
                    }
                }
            }
        }
        mTablePagination(
            count = musicList.size,
            rowsPerPage = rowsPerPage,
            page = page,
            onChangePage = { _, newPage -> setPage(newPage) },
            onChangeRowsPerPage = {
                setRowsPerPage(it.target.asDynamic().value as Int)
                setPage(0)
            }
        )
    }
    genreDialog(genreDialogOpen, ::handleGenreDialogClose, ::handleGenreSelect)
    backdrop(backdropOpen)
}

private fun RBuilder.backdrop(
    open: Boolean
) {
    themeContext.Consumer { theme ->
        val styles = object: StyleSheet("BackdropStyles", isStatic = true) {
            val backdrop by css  {
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
    onRefreshClick: () -> Unit,
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
    orderBy: MusicFileField,
    rowCount: Int,
    onSelectAllClick: (checked: Boolean) -> Unit,
    onRequestSort: (field: MusicFileField) -> Unit
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
            columns.filter { it.visible }.forEach { column ->
                mTableCell(
                    key = column.id,
                    align = if (column.rightAligned) MTableCellAlign.right else MTableCellAlign.left,
                    padding = if (column.disablePadding) MTableCellPadding.none else MTableCellPadding.default,
                    sortDirection = if (orderBy == column.id) order else MTableCellSortDirection.False
                ) {
                    mTooltip(
                        title = "Sort",
                        placement = if (column.rightAligned) TooltipPlacement.bottomEnd else TooltipPlacement.bottomStart,
                        enterDelay = 300
                    ) {
                        mTableSortLabel(
                            label = column.label,
                            active = orderBy == column.id,
                            direction = if (order == MTableCellSortDirection.asc) MTableSortLabelDirection.asc else MTableSortLabelDirection.desc,
                            onClick = { onRequestSort(column.id) }
                        )
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
        val styles = object : StyleSheet("DialogStyles", isStatic = true) {
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