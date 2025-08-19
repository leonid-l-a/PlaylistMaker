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
    // Обычный текст
    bodyLarge = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.TextCommonSize
    ),
    // Мелкий текст (артист и время)
    bodySmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.TrackArtistNameAndTimeSize
    ),
    // Текст поиска
    titleMedium = TextStyle(
        fontFamily = YsDisplayMedium,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.SearchScreenTextSize
    ),
    // Характеристики в плеере
    labelMedium = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.PlayerCharacteristicsSize
    ),
    // Элемент плейлиста
    labelSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.PlaylistItemTextSize
    ),
    // Описание плейлиста
    titleSmall = TextStyle(
        fontFamily = YsDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = Dimens.PlaylistDescriptionTextSize
    )
)
