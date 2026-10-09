package com.example.tomagua.data.storage

import android.content.Context
import androidx.core.content.FileProvider
import com.example.tomagua.domain.storage.PendingPhoto
import com.example.tomagua.domain.storage.PhotoStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class FilePhotoStorage @Inject constructor(
    @ApplicationContext private val context: Context
) : PhotoStorage {
    //filesDir é privado do app: não aparece na galeria e some com o app.
    private val photosDir: File
        get() = File(context.filesDir, PHOTOS_DIR)

    override fun createPendingPhoto(): PendingPhoto {
        photosDir.mkdirs()
        // UUID evita colisão e não vaza horário no nome do arquivo.
        val fileName = "${UUID.randomUUID()}.jpg"
        val authority = "${context.packageName}.$AUTHORITY_SUFFIX"
        val uri = FileProvider.getUriForFile(context, authority, fileFor(fileName))
        return PendingPhoto(fileName = fileName, uri = uri)
    }

    // File(nome).name descarta qualquer "../" que viesse no valor: o arquivo
    // sempre resolve dentro de photosDir, mesmo que o banco tenha lixo.
    override fun fileFor(fileName: String): File =
        File(photosDir, File(fileName).name)

    override suspend fun delete(fileName: String) {
        withContext(Dispatchers.IO) { fileFor(fileName).delete() }
    }

    override suspend fun deleteAll(fileNames: List<String>) {
        withContext(Dispatchers.IO) { fileNames.forEach { fileFor(it).delete() } }
    }

    private companion object {
        const val PHOTOS_DIR = "consumption_photos"
        const val AUTHORITY_SUFFIX = "fileprovider"
    }
}