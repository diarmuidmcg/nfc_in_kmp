package model

import utils.currentLocale

class NFCWriteMessageKMP(
    public val textMessage: String?,
    public val url: String?,
    public val uri: String?,
    public val locale: String,
) {
    companion object {
        fun fromTextMessage(textMessage: String, locale: String = currentLocale): NFCWriteMessageKMP {
            return NFCWriteMessageKMP(textMessage, null, null, locale)
        }

        fun fromUrl(url: String, locale: String = currentLocale): NFCWriteMessageKMP {
            return NFCWriteMessageKMP(null, url, null, locale)
        }

        fun fromUri(uri: String, locale: String = currentLocale): NFCWriteMessageKMP {
            return NFCWriteMessageKMP(null, null, uri, locale)
        }
    }
}