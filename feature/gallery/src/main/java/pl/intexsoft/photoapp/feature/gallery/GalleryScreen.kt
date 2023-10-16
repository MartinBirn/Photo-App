package pl.intexsoft.photoapp.feature.gallery

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import pl.intexsoft.photoapp.core.navigation.SharedScreen
import pl.intexsoft.photoapp.core.ui.R
import pl.intexsoft.photoapp.core.ui.component.PhotoCheckbox
import pl.intexsoft.photoapp.core.ui.model.PhotoUi
import pl.intexsoft.photoapp.core.ui.model.TagUi
import pl.intexsoft.photoapp.core.ui.theme.PhotoAppColor
import pl.intexsoft.photoapp.core.ui.theme.body2Medium
import pl.intexsoft.photoapp.core.ui.theme.bodyBold
import pl.intexsoft.photoapp.core.ui.theme.headerMedium

object GalleryScreen : Screen {

    private fun readResolve(): Any = GalleryScreen

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<GalleryScreenModel>()
        val screenState by screenModel.state.collectAsState()

        val navigator = LocalNavigator.current
        val cameraScreen = rememberScreen(SharedScreen.Camera)

        when (val result = screenState) {
            is GalleryScreenModel.State.Loading -> {
                LoadingContent()
            }

            is GalleryScreenModel.State.Default -> {
                ContainerContent {
                    TitleHeader(
                        onFilterClicked = screenModel::onFilterClicked
                    )
                    GalleryContent(
                        photos = result.photos,
                        onPhotoLongClicked = screenModel::onPhotoClicked
                    )
                    BottomActionMenu(
                        onCameraClicked = screenModel::onCameraClicked
                    )
                }
            }

            is GalleryScreenModel.State.FilterSelection -> {
                ContainerContent {
                    FiltersHeader(
                        tags = result.tags,
                        onFilterSelected = screenModel::onFilterSelected,
                        onCloseClicked = screenModel::onCloseFiltersClicked
                    )
                    GalleryContent(
                        photos = result.photos,
                        onPhotoLongClicked = screenModel::onPhotoClicked
                    )
                    BottomActionMenu()
                }
            }

            is GalleryScreenModel.State.PhotoSelection -> {
                ContainerContent {
                    SelectionsHeader(
                        selections = result.selections,
                        onCheckAllClicked = screenModel::onCheckAllClicked
                    )
                    GalleryContent(
                        photos = result.photos,
                        onPhotoLongClicked = screenModel::onPhotoClicked,
                        onCheckedChanged = screenModel::onPhotoClicked
                    )
                    BottomActionMenu(
                        onCancelClicked = screenModel::onCancelClicked,
                        onDeleteClicked = screenModel::onDeleteClicked
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            launch {
                screenModel.openCamera.receiveAsFlow().collectLatest {
                    navigator?.popUntil { screen -> screen == cameraScreen }
                }
            }
            launch {
                screenModel.openPreviousScreen.receiveAsFlow().collectLatest {
                    navigator?.pop()
                }
            }
        }

        BackHandler(true) {
            screenModel.onBackPressed()
        }
    }

    @Composable
    fun ContainerContent(
        modifier: Modifier = Modifier,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Column(
            modifier = modifier
                .background(PhotoAppColor.White),
            content = content
        )
    }

    @Composable
    fun LoadingContent(
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = PhotoAppColor.RedLight
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ColumnScope.GalleryContent(
        photos: List<PhotoUi>,
        modifier: Modifier = Modifier,
        onPhotoLongClicked: (String) -> Unit,
        onCheckedChanged: ((String) -> Unit)? = null,
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            verticalItemSpacing = 20.dp,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier.weight(1f)
        ) {
            val size = photos.size
            items(size) { index: Int ->
                val photo = photos[index]

                SubcomposeAsyncImage(
                    model = photo.contentUri,
                    contentDescription = photo.name,
                    modifier = Modifier
                        .aspectRatio(
                            ratio = photo.scale,
                            matchHeightConstraintsFirst = true
                        )
                        .clip(
                            shape = RoundedCornerShape(
                                size = 10.dp
                            )
                        ),
                    loading = {
                        Box(
                            modifier = modifier
                                .aspectRatio(photo.scale),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = PhotoAppColor.RedLight
                            )
                        }
                    },
                    success = {
                        Box(
                            modifier = Modifier
                                .combinedClickable(
                                    onLongClick = {
                                        onPhotoLongClicked(photo.name)
                                    },
                                    onClick = {
                                        onCheckedChanged?.invoke(photo.name)
                                    }
                                )
                        ) {
                            this@SubcomposeAsyncImage.SubcomposeAsyncImageContent()
                            if (onCheckedChanged != null) {
                                PhotoCheckbox(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clip(shape = CircleShape),
                                    checked = photo.isChecked,
                                    onCheckedChange = {
                                        onCheckedChanged(photo.name)
                                    }
                                )
                            }
                        }
                    },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun FiltersHeader(
        tags: List<TagUi>,
        modifier: Modifier = Modifier,
        onFilterSelected: () -> Unit,
        onCloseClicked: () -> Unit
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = PhotoAppColor.RedLight,
                    shape = RoundedCornerShape(
                        bottomStart = 10.dp,
                        bottomEnd = 10.dp
                    )
                )
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tags.forEach { tag ->
                    val selectedModifier = Modifier.apply {
                        if (tag.isSelected) {
                            this.border(
                                width = 4.dp,
                                color = Color(0xFFFFFFFF),
                                shape = RoundedCornerShape(size = 10.dp)
                            )
                        }
                    }
                    Text(
                        text = tag.name,
                        modifier = Modifier
                            .background(
                                color = Color(0x33FFFFFF),
                                shape = RoundedCornerShape(size = 10.dp)
                            )
                            .clip(shape = RoundedCornerShape(size = 10.dp))
                            .then(selectedModifier)
                            .clickable(onClick = onFilterSelected)
                            .padding(
                                start = 19.dp,
                                top = 10.dp,
                                end = 20.dp,
                                bottom = 11.dp
                            ),
                        style = MaterialTheme.typography.bodyBold
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.ic_close
                ),
                contentDescription = stringResource(
                    id = R.string.close_filters
                ),
                modifier = Modifier.clickable(onClick = onCloseClicked),
                tint = Color.Unspecified
            )
        }
    }

    @Composable
    private fun TitleHeader(
        modifier: Modifier = Modifier,
        onFilterClicked: () -> Unit
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stringResource(id = R.string.all_images),
                style = MaterialTheme.typography.headerMedium
            )
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.ic_filter
                ),
                contentDescription = stringResource(
                    id = R.string.show_filters
                ),
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .clickable(onClick = onFilterClicked),
                tint = Color.Unspecified
            )
        }
    }

    @Composable
    private fun SelectionsHeader(
        selections: Int,
        modifier: Modifier = Modifier,
        onCheckAllClicked: () -> Unit
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    top = 20.dp,
                    end = 20.dp
                ),
        ) {
            Text(
                text = stringResource(
                    id = R.string.selected_placeholder,
                    selections
                ),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2Medium
            )
            Text(
                text = stringResource(id = R.string.select_all),
                style = MaterialTheme.typography.body2Medium
            )
            Spacer(modifier = Modifier.width(9.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_unchecked_2),
                contentDescription = stringResource(
                    id = R.string.check_all
                ),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onCheckAllClicked)
            )
        }
    }

    @Composable
    private fun BottomActionMenu(
        modifier: Modifier = Modifier,
        onCameraClicked: (() -> Unit)? = null,
        onCancelClicked: (() -> Unit)? = null,
        onDeleteClicked: (() -> Unit)? = null
    ) {
        if (onCameraClicked == null && onCancelClicked == null && onDeleteClicked == null) return

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(108.dp)
                .background(
                    color = PhotoAppColor.RedLight,
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp
                    )
                ),
            horizontalArrangement = Arrangement
                .spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterHorizontally
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onCameraClicked != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.ic_camera_2
                    ),
                    contentDescription = stringResource(
                        id = R.string.open_camera
                    ),
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .clickable(onClick = onCameraClicked),
                    tint = Color.Unspecified
                )
            }
            if (onCancelClicked != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.ic_cancel
                    ),
                    contentDescription = stringResource(
                        id = R.string.cancel
                    ),
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .clickable(onClick = onCancelClicked),
                    tint = Color.Unspecified
                )
            }
            if (onDeleteClicked != null) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.ic_delete
                    ),
                    contentDescription = stringResource(
                        id = R.string.delete
                    ),
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .clickable(onClick = onDeleteClicked),
                    tint = Color.Unspecified
                )
            }
        }
    }
}