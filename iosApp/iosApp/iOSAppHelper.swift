import Foundation
import ComposeApp

class iOSAppHelper {
    static let shared = iOSAppHelper()

    func handleIncomingImage(url: URL) {
        let fileManager = FileManager.default

        // 创建并定位 shareTmp 文件夹
        let documents = fileManager.urls(for: .documentDirectory, in: .userDomainMask).first!
        let shareTmp = documents.appendingPathComponent("shareTmp", isDirectory: true)

        // 确保文件夹存在
        if !fileManager.fileExists(atPath: shareTmp.path) {
            do {
                try fileManager.createDirectory(at: shareTmp, withIntermediateDirectories: true, attributes: nil)
            } catch {
                print("❌ 创建 shareTmp 文件夹失败: \(error)")
                return
            }
        }

        // 清空 shareTmp 文件夹
        do {
            let contents = try fileManager.contentsOfDirectory(atPath: shareTmp.path)
            for file in contents {
                let fileURL = shareTmp.appendingPathComponent(file)
                try fileManager.removeItem(at: fileURL)
            }
        } catch {
            print("⚠️ 清空 shareTmp 文件夹失败: \(error)")
        }

        // 复制图片到 shareTmp
        let destURL = shareTmp.appendingPathComponent(url.lastPathComponent)

        do {
            if fileManager.fileExists(atPath: destURL.path) {
                try fileManager.removeItem(at: destURL) // ✅ 改成传 URL
            }

            try fileManager.copyItem(at: url, to: destURL)

            print("✅ 图片已复制到 shareTmp: \(destURL.path)")

            // 调用 Kotlin 层
            MainViewControllerKt.onImageReceived(path: destURL.path)
        } catch {
            print("❌ 图片复制失败: \(error)")
        }
    }
}
