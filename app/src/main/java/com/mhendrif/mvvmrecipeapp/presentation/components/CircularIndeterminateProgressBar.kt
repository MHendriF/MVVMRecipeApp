package com.mhendrif.mvvmrecipeapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CircularIndeterminateProgressBar(isDisplayed: Boolean, verticalBias: Float) {
    if (isDisplayed) {
        ConstraintLayout(
                modifier = Modifier.fillMaxSize(),
        ){
            val (progressBar) = createRefs()
            val topBias = createGuidelineFromTop(verticalBias)
            CircularProgressIndicator(
                    modifier = Modifier
                            .constrainAs(progressBar) {
                                top.linkTo(topBias)
                                end.linkTo(parent.end)
                                start.linkTo(parent.start)
                            },
                    color = MaterialTheme.colors.primary
            )
        }
    }
}
