package utils

import java.util.Locale

actual val currentLocale:String
    get() = Locale.getDefault().language