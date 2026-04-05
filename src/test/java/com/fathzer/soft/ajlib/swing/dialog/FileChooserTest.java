package com.fathzer.soft.ajlib.swing.dialog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import com.fathzer.soft.ajlib.utilities.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class FileChooserTest {

    @Test
    void testFileUtilsGetCanonicalWithMockedFileInputStream() throws IOException {
        try (MockedConstruction<FileInputStream> mocked =
            Mockito.mockConstruction(FileInputStream.class, (mock, context) -> {
                throw new IOException("Permission denied");
            })) {

            File file = mock(File.class);
            when(file.exists()).thenReturn(true);
            when(file.isFile()).thenReturn(true);
            when(file.getName()).thenReturn("test.lnk");

            FileUtils.getCanonical(file);
        }
    }

    /**
     * Test for bug 20260404: FileChooser hangs when non accessible file is selected
     */
    @Test
    void testBug20260404(@TempDir Path tempDir) {
        FileChooser chooser = new FileChooser() {
            @Override
            public File getSelectedFile() {
                // Mock a file that make new FileInputStream fail with an IOException
                File file = mock(File.class);
                when(file.exists()).thenReturn(true);
                when(file.getName()).thenReturn("test.txt");
                when(file.getPath()).thenThrow(new FileNotFoundException("Access denied"));
                return file;
            }
        };
        chooser.setDialogType(FileChooser.SAVE_DIALOG);
        String disabledCause = chooser.getDisabledCause();
        System.out.println("Disabled cause: " + disabledCause);
    }
}
