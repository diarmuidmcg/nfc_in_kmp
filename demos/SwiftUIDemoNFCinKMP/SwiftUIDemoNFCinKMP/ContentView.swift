//
//  ContentView.swift
//  SwiftUIDemoNFCinKMP
//
//  Created by didi on 26/05/2024.
//

import SwiftUI
import nfc_in_kmp

struct ContentView: View {
    
    @SwiftUI.State var payloadOnTag: String? = nil
    @SwiftUI.State var uuid: String? = nil
    @SwiftUI.State var errorFromTag: String? = nil
    let nfcInKMP = NFCInKMP()
    
    func demoCompletionHandlerExecution(result: NFCResult) {
        switch result {
        case let success as NFCResult.Success:
            payloadOnTag = success.record.payload
            uuid = success.record.identifier
        case let error as NFCResult.Failure:
            errorFromTag = error.error.message
        default:
            break
        }
    }
    
    func removePreviousRecordAndError() {
        payloadOnTag = nil
        uuid = nil
        errorFromTag = nil
    }
    
    func launchGeneralNFCReading() {
        removePreviousRecordAndError()
        // Kotlin Suspend functions must be called from the main thread
        DispatchQueue.main.async {
            // we use Tasks due to the async nature of Kotlin Suspend Functions
            Task {
                do {
                    try await nfcInKMP.startGeneralNFCReading(
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    func launchMifareNFCReading() {
        removePreviousRecordAndError()
        // Kotlin Suspend functions must be called from the main thread
        DispatchQueue.main.async {
            // we use Tasks due to the async nature of Kotlin Suspend Functions
            Task {
                do {
                    try await nfcInKMP.startReadingMifare(
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    func launchNFCWritingText() {
        removePreviousRecordAndError()
        DispatchQueue.main.async {
            Task {
                do {
                    try await nfcInKMP.startWritingText(
                        message: "Bonjour",
                        locale: "en_UK",
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    func launchNFCWritingURL() {
        removePreviousRecordAndError()
        DispatchQueue.main.async {
            Task {
                do {
                    try await nfcInKMP.startWritingURL(
                        url: "www.fonzmusic.com",
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    func launchNFCWritingURI() {
        removePreviousRecordAndError()
        DispatchQueue.main.async {
            Task {
                do {
                    try await nfcInKMP.startWritingURI(
                        uri: "/diarmuiddevs/sources/nfc_in_kmp",
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    var body: some View {
        VStack(spacing: 10) {
            Image(systemName: "sensor.tag.radiowaves.forward.fill")
                .imageScale(.large)
            Button(action: launchGeneralNFCReading, label: { Text("General Read NFC") })
            Button(action: launchMifareNFCReading, label: { Text("Mifare Read NFC") })
            Button(action: launchNFCWritingText, label: { Text("Write NFC with Text") })
            Button(action: launchNFCWritingURL, label: { Text("Write NFC with URL") })
            Button(action: launchNFCWritingURI, label: { Text("Write NFC with URI") })
            if let payloadOnTag {
                Text("Payload from previous tag is:")
                Text(payloadOnTag)
            }
            if let errorFromTag { Text("Error from previous tag is: \(errorFromTag)") }
            if let uuid { Text("UUID from previous tag is: \(uuid)") }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}

