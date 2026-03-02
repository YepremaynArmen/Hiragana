package com.example.hiragana.data

data class Hiragana(val symbol: String, val romaji: String)

object HiraganaData {
    val levels = listOf(
        // Уровень 1: a i u e o
        listOf(
            Hiragana("あ", "a"), Hiragana("い", "i"),
            Hiragana("う", "u"), Hiragana("え", "e"),
            Hiragana("お", "o")
        ),
        // Уровень 2: ka ki ku ke ko
        listOf(
            Hiragana("か", "ka"), Hiragana("き", "ki"),
            Hiragana("く", "ku"), Hiragana("け", "ke"),
            Hiragana("こ", "ko")
        ),
        // Уровень 3: sa shi su se so
        listOf(
            Hiragana("さ", "sa"), Hiragana("し", "shi"),
            Hiragana("す", "su"), Hiragana("せ", "se"),
            Hiragana("そ", "so")
        ),
        // ... продолжаем все 10+ уровней (46 символов)
        // Уровень 4: ta chi tsu te to
        listOf(Hiragana("た", "ta"), Hiragana("ち", "chi"), Hiragana("つ", "tsu"), Hiragana("て", "te"), Hiragana("と", "to")),
        // Уровень 5: na ni nu ne no
        listOf(Hiragana("な", "na"), Hiragana("に", "ni"), Hiragana("ぬ", "nu"), Hiragana("ね", "ne"), Hiragana("の", "no")),
        // Уровень 6: ha hi fu he ho
        listOf(Hiragana("は", "ha"), Hiragana("ひ", "hi"), Hiragana("ふ", "fu"), Hiragana("へ", "he"), Hiragana("ほ", "ho")),
        // Уровень 7: ma mi mu me mo
        listOf(Hiragana("ま", "ma"), Hiragana("み", "mi"), Hiragana("む", "mu"), Hiragana("め", "me"), Hiragana("も", "mo")),
        // Уровень 8: ya yu yo
        listOf(Hiragana("や", "ya"), Hiragana("ゆ", "yu"), Hiragana("よ", "yo")),
        // Уровень 9: ra ri ru re ro
        listOf(Hiragana("ら", "ra"), Hiragana("り", "ri"), Hiragana("る", "ru"), Hiragana("れ", "re"), Hiragana("ろ", "ro")),
        // Уровень 10: wa wo n
        listOf(Hiragana("わ", "wa"), Hiragana("を", "wo"), Hiragana("ん", "n"))
    )
}
