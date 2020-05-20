//
//  ContentView.swift
//  amplify-lib-ios-demo
//
//  Created by Stormacq, Sebastien on 18/05/2020.
//  Copyright © 2020 Stormacq, Sebastien. All rights reserved.
//

import SwiftUI

struct MyAppButton: View {
    
    var action: () -> Void
    var icon: String
    var text: String
    
    init(action: @escaping () -> Void, icon: String, text: String) {
        self.action = action
        self.icon = icon
        self.text = text
    }
    var body: some View {
        Button(action: {
            // What to perform
            self.action()
        }) {
            // How the button looks like
            HStack {
                Image(systemName: self.icon)
                    .font(.subheadline)
                Text(self.text)
                    .font(.subheadline)
            }
            .font(.title)
            .padding()
            .overlay(
                RoundedRectangle(cornerRadius: 5)
                    .stroke(Color.accentColor, lineWidth: 3)
            )
        }

    }
}

struct ContentView: View {
    
    @ObservedObject public var user : UserData
    @State private var toBeTranslated: String = ""

    let app = UIApplication.shared.delegate as! AppDelegate
    
    var body: some View {
        
        
        VStack(spacing: 20) {

            MyAppButton(action: self.app.signIn,
                        icon: "person.badge.plus",
                        text: "Sign In")

            MyAppButton(action: self.app.signOut,
                        icon: "person.badge.minus",
                        text: "Sign Out")

            MyAppButton(action: self.app.create,
                        icon: "plus.circle",
                        text: "Create")

            MyAppButton(action: self.app.query,
                        icon: "doc.text.magnifyingglass",
                        text: "Query")

            
            TextField("Text to translate (english)", text: $toBeTranslated)
                .padding()
                .cornerRadius(4.0)
                .background(Color(UIColor.systemFill))
                .padding(EdgeInsets(top: 0, leading: 0, bottom: 15, trailing: 0))
            
            Button(action: {
                // What to perform
                self.app.translate(text: self.toBeTranslated)
            }) {
                // How the button looks like
                HStack {
                    Image(systemName: "t.bubble")
                        .font(.subheadline)
                    Text("Translate")
                        .font(.subheadline)
                }
                .font(.title)
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 5)
                        .stroke(Color.accentColor, lineWidth: 3)
                )
            }

            Text(user.translatedText).bold().font(.subheadline)
        }

    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        let app = UIApplication.shared.delegate as! AppDelegate
        return ContentView(user: app.data)
    }
}
