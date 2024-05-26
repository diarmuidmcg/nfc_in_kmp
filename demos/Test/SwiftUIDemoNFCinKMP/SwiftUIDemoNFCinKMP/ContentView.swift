//
//  ContentView.swift
//  SwiftUIDemoNFCinKMP
//
//  Created by didi on 26/05/2024.
//

import SwiftUI
import nfc_in_kmp
import CoreNFC

struct ContentView: View {
    
    @SwiftUI.State var payloadOnTag: String? = nil
    @SwiftUI.State var uuid: String? = nil
    @SwiftUI.State var errorFromTag: String? = nil
    
    func demoCompletionHandlerExecution(record: NFCRecordKMP?, error: NFCErrorKMP?) {
        if let record {
            payloadOnTag = record.payload
            uuid = record.identifier
        } else {
            payloadOnTag = nil
            uuid = nil
        }
        if let error { errorFromTag = error.message} else { errorFromTag = nil }
    }
    
    func launchNFCReading() {
        // Kotlin Suspend functions must be called from the main thread
        DispatchQueue.main.async {
            // we use Tasks due to the async nature of Kotlin Suspend Functions
            Task {
                do {
                    try await NFCInKMP().startReading(
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    func launchNFCWritingText() {
        DispatchQueue.main.async {
            Task {
                do {
                    try await NFCInKMP().startWritingText(
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
        DispatchQueue.main.async {
            Task {
                do {
                    try await NFCInKMP().startWritingURL(
                        url: "www.fonzmusic.com",
                        customAlertMessage: nil,
                        customErrorMessage: nil,
                        completionHandler: demoCompletionHandlerExecution)
                }
            }
        }
    }
    
    func launchNFCWritingURI() {
        DispatchQueue.main.async {
            Task {
                do {
                    try await NFCInKMP().startWritingURI(
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
            Button(action: launchNFCReading, label: { Text("Read NFC") })
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

