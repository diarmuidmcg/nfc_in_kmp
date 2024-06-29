## NOTE BENE: This project is still a work in progress. 

# nfc_in_kmp

This open source project provides a Kotlin Multiplatform (iOS & Android) SDK to standardize & simplify using Near Field Communication. 
Due to the restrictive nature of the iOS NFC libraries, it has been designed for iOS first so that the same functionality is available on both platforms. 

## Getting Started 

## Functionality available to you 

The core functionality available to you is being able to read & write NFC & NDEF Tags. 

All functions will return an `NFCResult` object, which on Success contains an `NFCRecordKMP` or on Failure an `NFCErrorKMP`.

To call any method, you will create an instance of `NFCInKMP`, then all methods should be available to you.

All methods accept a `customAlertMessage` & `customErrorMessage`.
If nothing is provided, the system default will be used.

### Read 

Due to iOS restrictions, there are different read methods for each type of NDEF tag. 
A General NFC read tag is provided, but this will NOT return the UUID of the tag, or specific information about the tag.

#### General NFC Tag reading
This method will read both NFC & NDEF tags. 

`suspend fun startGeneralNFCReading(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit)`

#### MiFare
This method will return the UUID, as well as other information related to MiFare tags.
However, it will only work with MiFare tags. 

`startReadingMifare(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) `

#### FeliCa
This method will return the UUID, as well as other information related to FeliCa tags.
However, it will only work with FeliCa tags. 

`startReadingFeliCa(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) `

#### ISO15693
This method will return the UUID, as well as other information related to ISO15693 tags.
However, it will only work with ISO15693 tags. 

`startReadingISO15693(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) `

#### ISO7816
This method will return the UUID, as well as other information related to ISO7816 tags.
However, it will only work with ISO7816 tags. 

`startReadingISO7816(customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit) `


### Write

Writing can only be done on NDEF tags, not generic NFC tags. 
There are different types of formats to write (String, URL, URI), so there is a function for each type. 

On iOS, the write methods will NOT return the UUID.

#### Text
This method accepts the String that you wish to write, as well as a locale.
If no locale is provided, the default locale from the device will be used.

`startWritingText(message: String, locale: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit)`

#### URL
This method accepts the URL as a String. 
The SDK will transform the String into a URL. 

`startWritingURL(url: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit)`

#### URI
This method accepts the URI as a String. 
The SDK will transform the String into a URI. 
If you've forgotten or do not know what a URI is, it is a file path, like `/this/is/my/path`

`startWritingURI(uri: String, customAlertMessage: String? = null, customErrorMessage: String? = null, completionHandler: (NFCResult) -> Unit)`



## Run Locally iOS

To test locally with iOS:

If you're using XCode 15 or above:
`export MODERN_XCODE_LINKER=true`
First:
`./gradlew clean && ./gradlew assemblexcframework`
Then:
`cp -r nfc_in_kmp/build/XCFrameworks/debug/nfc_in_kmp.xcframework .iosBuilds/`

Then, using Swift Package Manager, select the project in local files.
You should be able to `import nfc_in_kmp` in your code.
