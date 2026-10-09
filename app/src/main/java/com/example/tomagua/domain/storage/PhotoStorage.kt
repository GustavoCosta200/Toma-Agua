package com.example.tomagua.domain.storage

import android.net.Uri
import java.io.File

/** Foto reservada para a câmera escrever: o nome vai pro banco, a URI vai pra câmera. */
data class PendingPhoto (
    val fileName: String,
    val uri: Uri
)

/**
 * Contrato para guardar e localizar as fotos de consumo.
 * Os ViewModels dependem só desta interface; só o nome do arquivo circula pelo app e pelo banco.
 */
interface PhotoStorage{
    /** Reserva um nome único e devolve a URI (content://) para o TakePicture escrever. */
    fun createPendingPhoto(): PendingPhoto

    /** Resolve o nome salvo no banco para o arquivo real. Não garante que ele exista. */
    fun fileFor(fileName: String): File

    suspend fun delete(fileName: String)

    suspend fun deleteAll(fileNames: List<String>)
}