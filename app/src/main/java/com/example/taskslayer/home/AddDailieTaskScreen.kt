package com.example.taskslayer.home

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskslayer.ui.theme.TaskSlayerTheme


@Composable
fun AddDailieTaskRoute(){
    AddDailieTaskContent()
}


@Composable
fun AddDailieTaskContent(){

}


@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddDailieTaskContentPreview(){
    TaskSlayerTheme {
        AddDailieTaskContent()
    }
}