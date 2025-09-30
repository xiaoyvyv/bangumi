import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    print("📩 收到分享的 URL：\(url)")
                    iOSAppHelper.shared.handleIncomingImage(url: url)
                }
        }
    }
}
