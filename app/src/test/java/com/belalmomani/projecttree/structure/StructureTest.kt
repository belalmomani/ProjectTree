package com.belalmomani.projecttree.structure

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StructureTest {

    private fun file(p: String) = """{"path":"$p","type":"file"}"""
    private fun folder(p: String) = """{"path":"$p","type":"folder"}"""
    private fun doc(vararg items: String) =
        """{"projectName":"Demo","items":[${items.joinToString(",")}]}"""

    @Test
    fun validStructureCountsFoldersAndFiles() {
        val r = StructureParser.parse(
            doc(folder("app"), file("app/build.gradle.kts"), file("app/src/main/Main.kt"))
        )
        assertTrue(r.errors.toString(), r.ok)
        assertEquals(3, r.folderCount)
        assertEquals(2, r.fileCount)
        assertEquals("app", r.nodes.first().path)
        assertEquals("Demo", r.meta.name)
    }

    @Test
    fun rejectsParentTraversal() {
        assertFalse(StructureParser.parse(doc(file("../evil.txt"))).ok)
        assertFalse(StructureParser.parse(doc(file("a/../../evil.txt"))).ok)
    }

    @Test
    fun rejectsAbsolutePaths() {
        assertFalse(StructureParser.parse(doc(file("/etc/passwd"))).ok)
        assertFalse(StructureParser.parse(doc(file("C:/Windows/x.txt"))).ok)
    }

    @Test
    fun rejectsDuplicates() {
        assertFalse(StructureParser.parse(doc(file("a.txt"), file("a.txt"))).ok)
    }

    @Test
    fun rejectsFileFolderConflict() {
        assertFalse(StructureParser.parse(doc(file("a"), file("a/b.txt"))).ok)
        assertFalse(StructureParser.parse(doc(file("a/b.txt"), file("a"))).ok)
    }

    @Test
    fun acceptsDirectoryTypeAndIgnoresContent() {
        val r = StructureParser.parse(
            """{"items":[{"path":"x","type":"directory"},{"path":"x/a.txt","type":"file","content":"hello"}]}"""
        )
        assertTrue(r.errors.toString(), r.ok)
        assertEquals(1, r.folderCount)
        assertEquals(1, r.fileCount)
    }

    @Test
    fun acceptsNestedChildren() {
        val r = StructureParser.parse(
            """{"items":[{"name":"root","type":"folder","children":[{"name":"a.txt","type":"file"}]}]}"""
        )
        assertTrue(r.errors.toString(), r.ok)
        assertEquals(listOf("root", "root/a.txt"), r.nodes.map { it.path })
    }

    @Test
    fun rejectsInvalidJson() {
        assertFalse(StructureParser.parse("{not json").ok)
        assertFalse(StructureParser.parse("").ok)
    }

    @Test
    fun zipContainsEmptyFilesAndFolders() {
        val r = StructureParser.parse(doc(folder("app"), file("app/build.gradle.kts")))
        val bos = ByteArrayOutputStream()
        ZipExporter.write(bos, "Demo", r.nodes)

        val names = mutableListOf<String>()
        ZipInputStream(ByteArrayInputStream(bos.toByteArray())).use { zis ->
            var e = zis.nextEntry
            while (e != null) {
                names.add(e.name)
                if (!e.isDirectory) assertEquals(0, zis.readBytes().size)
                e = zis.nextEntry
            }
        }
        assertTrue(names.contains("Demo/app/"))
        assertTrue(names.contains("Demo/app/build.gradle.kts"))
    }
}
