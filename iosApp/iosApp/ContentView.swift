import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

//struct Live2DContainerView: UIViewControllerRepresentable {
//    
//    func makeUIViewController(context: Context) -> ViewController {
//        let live2D = delegate.start()
////        if(live2D != nil)  {
////            delegate.initializeCubism()
////            delegate.viewController.initializeSprite()
////        }
//        return live2D!
//    }
//    
//    func updateUIViewController(_ uiViewController: ViewController, context: Context) {
//        // 不需要更新逻辑可留空
//    }
//}


struct Live2DContainerView: UIViewRepresentable {

    func makeUIView(context: Context) -> Live2DView {
        return Live2DView()
    }

    func updateUIView(_ uiView: Live2DView, context: Context) {

    }
}


struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
//        ZStack {
//                    // 背景颜色
//                    Color.green
//                        .ignoresSafeArea()
//
//                    // Live2D 容器视图
//                    Live2DContainerView()
//                        .frame(width: 300, height: 400) // 设置小一点
//                }
    }
}



