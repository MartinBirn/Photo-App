package pl.intexsoft.photoapp.core.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import pl.intexsoft.photoapp.core.ui.R

@Composable
fun PhotoCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Surface(
        onClick = {
            onCheckedChange(!checked)
        },
        shape = CircleShape,
        color = Color.Unspecified,
        contentColor = Color.Unspecified,
        modifier = modifier.semantics { role = Role.Checkbox },
        interactionSource = interactionSource
    ) {
        AnimatedContent(
            targetState = checked,
            transitionSpec = {
                scaleIn().togetherWith(scaleOut())
            },
            label = "PhotoCheckbox"
        ) {
            if (it) {
                Image(
                    painter = painterResource(id = R.drawable.ic_checked),
                    contentDescription = stringResource(
                        id = R.string.checked
                    )
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_unchecked),
                    contentDescription = stringResource(
                        id = R.string.unchecked
                    )
                )
            }
        }
    }
}