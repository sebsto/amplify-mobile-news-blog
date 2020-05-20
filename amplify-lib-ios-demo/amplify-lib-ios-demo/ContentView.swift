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

        }

    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
