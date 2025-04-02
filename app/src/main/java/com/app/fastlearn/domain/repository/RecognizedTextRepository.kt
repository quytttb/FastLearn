package com.app.fastlearn.domain.repository

    import com.app.fastlearn.domain.model.RecognizedText
    import java.util.concurrent.ConcurrentHashMap

    class RecognizedTextRepository {
        private val dataStore = ConcurrentHashMap<String, RecognizedText>()

        fun saveRecognizedText(data: RecognizedText): String {
            val id = data.id
            dataStore[id] = data
            return id
        }

        fun getRecognizedTextById(id: String): RecognizedText? {
            return dataStore[id]
        }
    }