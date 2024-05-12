package utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual val currentLocale:String
    get() = NSLocale.currentLocale.languageCode