package com.example.playlistmaker.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography

import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

val YsDisplayRegular = FontFamily(Font(R.font.ys_display_regular))
val YsDisplayMedium = FontFamily(Font(R.font.ys_display_medium))

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = Dimens.HeaderSize,
        lineHeight = Dimens.CommonLineHeight.value.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.TextCommonSize
    ),
    bodySmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.TrackArtistNameAndTimeSize
    ),
    titleMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.SearchScreenTextSize
    ),
    labelMedium = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.PlayerCharacteristicsSize
    ),
    labelSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.PlaylistItemTextSize
    ),
    titleSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.PlaylistDescriptionTextSize
    )
)
