package com.sameerasw.airsync.utils.transfer

object FileTransferProtocol {
    fun buildInit(
        id: String,
        name: String,
        size: Int,
        mime: String,
        checksum: String?
    ): String {
        val checksumLine = if (checksum.isNullOrBlank()) "" else "\n            ,\"checksum\": \"$checksum\""
        return """
        {
            "type": "fileTransferInit",
            "data": {
                "id": "$id",
                "name": "$name",
                "size": $size,
                "mime": "$mime"$checksumLine
            }
        }
        """.trimIndent()
    }

    fun buildChunk(id: String, index: Int, base64Chunk: String): String = """
        {
            "type": "fileChunk",
            "data": {
                "id": "$id",
                "index": $index,
                "chunk": "$base64Chunk"
            }
        }
    """.trimIndent()

    fun buildComplete(id: String, name: String, size: Int, checksum: String?): String {
        val checksumLine = if (checksum.isNullOrBlank()) "" else "\n                ,\"checksum\": \"$checksum\""
        return """
        {
            "type": "fileTransferComplete",
            "data": {
                "id": "$id",
                "name": "$name",
                "size": $size$checksumLine
            }
        }
        """.trimIndent()
    }

    fun buildChunkAck(id: String, index: Int): String = """
        {
            "type": "fileChunkAck",
            "data": { "id": "$id", "index": $index }
        }
    """.trimIndent()

    fun buildTransferVerified(id: String, verified: Boolean): String = """
        {
            "type": "transferVerified",
            "data": { "id": "$id", "verified": $verified }
        }
    """.trimIndent()
}
