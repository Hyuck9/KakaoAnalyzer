package me.hyuck.kakaoanalyzer.util

import me.hyuck.kakaoanalyzer.db.entity.Chat
import java.io.File

object FileUtils {

    fun deleteChatFile(chat: Chat): Boolean {
        val chatFileDir: File = File(chat.filePath).parentFile ?: return true
        return if (chatFileDir.exists()) {
            chatFileDir.listFiles()?.let { files -> files.forEach { it.delete() } }
            chatFileDir.delete()
        } else {
            true
        }
    }
}