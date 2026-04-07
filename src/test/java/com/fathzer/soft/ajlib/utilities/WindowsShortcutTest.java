package com.fathzer.soft.ajlib.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class WindowsShortcutTest {

    private boolean throwsIOExceptionWhenOpened(File file) {
        try (InputStream stream = new FileInputStream(file)) {
            // This test only makes sense on systems where we can't read the file
            return false;
        } catch (IOException e) {
            return true;
        }
    }

    /**
     * Test for bug 20260404: isPotentialValidLink throws an exception on non readable file with wrong extension or size
     */
    @Test
    void testBug20260404(@TempDir Path tempDir) throws IOException {
        // isPotentialValidLink should return false on directories
        assertFalse(WindowsShortcut.isPotentialValidLink(tempDir.toFile()));

        // Perform checks on read protected files
        File file = buildProtected(tempDir, "test.lnk", f -> {});
        assumeTrue(throwsIOExceptionWhenOpened(file));

        // isPotentialValidLink should return false if file is empty or smaller than the minimum link size
        assertFalse(WindowsShortcut.isPotentialValidLink(file));
        
        // isPotentialValidLink should return false if extension is not .lnk
        assertFalse(WindowsShortcut.isPotentialValidLink(buildProtected(tempDir, "test.txt", f -> {})));
    }

    private File buildProtected(Path tempDir, String name, Consumer<File> configurator) throws IOException {
        File file = tempDir.resolve(name).toFile();
        file.createNewFile();
        configurator.accept(file);
        file.setReadable(false, false);
        return file;
    }

    @Test
    void testIsPotentialValidLink_LongEnoughButWrongMagic(@TempDir Path tempDir) throws IOException {
        // Create a file that's long enough (>=100 bytes) but contains wrong magic header
        File file = tempDir.resolve("test.lnk").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Write 100 bytes with wrong magic header (magic should be 0x0000004C at offset 0)
            byte[] content = new byte[100];
            // Set wrong magic at offset 0 (0x12345678 instead of 0x0000004C)
            content[0] = 0x78;
            content[1] = 0x56;
            content[2] = 0x34;
            content[3] = 0x12;
            fos.write(content);
        }
        assertFalse(WindowsShortcut.isPotentialValidLink(file));
    }

    @Test
    void testIsPotentialValidLink_WithCorrectMagic(@TempDir Path tempDir) throws IOException {
        // Create a file that contains the correct magic header
        File file = tempDir.resolve("test.lnk").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Write 100 bytes with correct magic header (0x0000004C at offset 0)
            byte[] content = new byte[100];
            // Set correct magic at offset 0 (0x0000004C in little endian)
            content[0] = 0x4C;
            content[1] = 0x00;
            content[2] = 0x00;
            content[3] = 0x00;
            fos.write(content);
        }
        assertTrue(WindowsShortcut.isPotentialValidLink(file));
        assertThrows(ParseException.class, () -> new WindowsShortcut(file));
    }

    @Test
    void testConstructorAndAccessors(@TempDir Path tempDir) throws IOException, ParseException {
        // Create a valid Windows shortcut file
        File file = tempDir.resolve("test.lnk").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] link = ShortcutCreator.buildLnkFile();
            fos.write(link);
        }
        
        // Verify the file is detected as a potential valid link
        assertTrue(WindowsShortcut.isPotentialValidLink(file));
        
        // Test constructor and accessors
        WindowsShortcut shortcut = new WindowsShortcut(file);
        
        // Test basic properties
        assertFalse(shortcut.isDirectory());
        assertTrue(shortcut.isLocal());
        assertEquals("C:\\Windows\\System32\\notepad.exe", shortcut.getRealFilename());
        
        // Test optional properties (should be null in minimal shortcut)
        assertEquals("Notepad", shortcut.getDescription());
        assertNull(shortcut.getRelativePath());
        assertNull(shortcut.getWorkingDirectory());
        assertNull(shortcut.getCommandLineArguments());
    }
}
