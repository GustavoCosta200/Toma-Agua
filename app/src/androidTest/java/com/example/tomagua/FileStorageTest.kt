package com.example.tomagua

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tomagua.data.storage.FilePhotoStorage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilePhotoStorageTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val storage = FilePhotoStorage(context)

    @Test
    fun createPendingPhoto_returnsContentUriFromOurProvider() {
        val pending = storage.createPendingPhoto()

        assertEquals("content", pending.uri.scheme)
        assertEquals("${context.packageName}.fileprovider", pending.uri.authority)
        assertTrue(pending.fileName.endsWith(".jpg"))
    }

    @Test
    fun delete_removesExistingFile() = runBlocking {
        val pending = storage.createPendingPhoto()
        val file = storage.fileFor(pending.fileName)
        file.writeText("fake image")
        assertTrue(file.exists())

        storage.delete(pending.fileName)

        assertFalse(file.exists())
    }

    @Test
    fun fileFor_ignoresPathTraversal() {
        assertEquals(
            storage.fileFor("x.jpg").parentFile,
            storage.fileFor("../x.jpg").parentFile
        )
    }
}