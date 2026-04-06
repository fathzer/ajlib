package com.fathzer.soft.ajlib.utilities;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Creates a valid Windows Shell Link (.lnk) file pointing to notepad.exe.
 * Implements the MS-SHLLINK specification (Shell Link Binary File Format).
 *
 * Spec: https://docs.microsoft.com/en-us/openspecs/windows_protocols/ms-shllink
 */
public class ShortcutCreator {

    // ── Constants ────────────────────────────────────────────────────────────

    /** Shell Link class identifier (fixed GUID, defined by MS-SHLLINK §2.1) */
    private static final byte[] LINK_CLSID = {
        0x01, (byte)0x14, 0x02, 0x00,  0x00, 0x00, 0x00, 0x00,
        (byte)0xC0, 0x00, 0x00, 0x00,  0x00, 0x00, 0x00, 0x46
    };

    /** LinkFlags (§2.1.1) – we set HasLinkTargetIDList | HasLinkInfo | IsUnicode */
    private static final int LINK_FLAGS =
            0x00000001 |   // HasLinkTargetIDList
            0x00000002 |   // HasLinkInfo
            0x00000004 |   // HasName  (description)
            0x00000080;    // IsUnicode

    /** FileAttributes for a normal file (§2.1.2) */
    private static final int FILE_ATTRIBUTES = 0x00000020; // FILE_ATTRIBUTE_ARCHIVE

    /** Show command: SW_SHOWNORMAL */
    private static final int SHOW_CMD = 0x00000001;

    // ── Builder ──────────────────────────────────────────────────────────────

    static byte[] buildLnkFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 1. Shell Link Header (76 bytes, §2.1)
        baos.write(shellLinkHeader());

        // 2. LinkTargetIDList (§2.2) – minimal ID list for notepad.exe
        baos.write(linkTargetIDList());

        // 3. LinkInfo (§2.3) – local path via VolumeID + LocalBasePath
        baos.write(linkInfo());

        // 4. StringData: NameString (Description) – optional but valid
        baos.write(stringData("Notepad"));

        // 5. ExtraData – TerminalBlock (4 zero bytes, §2.5)
        baos.write(le32(0));

        return baos.toByteArray();
    }

    // ── Shell Link Header (§2.1) – exactly 76 bytes ──────────────────────────

    private static byte[] shellLinkHeader() {
        ByteBuffer b = ByteBuffer.allocate(76).order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(0x4C);                 // HeaderSize = 76
        b.put(LINK_CLSID);             // LinkCLSID (16 bytes)
        b.putInt(LINK_FLAGS);          // LinkFlags
        b.putInt(FILE_ATTRIBUTES);     // FileAttributes
        putFileTime(b, 0);             // CreationTime
        putFileTime(b, 0);             // AccessTime
        putFileTime(b, 0);             // WriteTime
        b.putInt(0);                   // FileSize
        b.putInt(0);                   // IconIndex
        b.putInt(SHOW_CMD);            // ShowCommand
        b.putShort((short) 0);         // HotKey
        b.putShort((short) 0);         // Reserved1
        b.putInt(0);                   // Reserved2
        b.putInt(0);                   // Reserved3
        return b.array();
    }

    // ── LinkTargetIDList (§2.2) ──────────────────────────────────────────────
    //
    // Minimal but valid IDList that encodes:
    //   My Computer → C:\ → Windows → System32 → notepad.exe
    //
    // We use the simplest possible ItemID encoding accepted by Explorer:
    // a raw-path item list (0x1F/0x2F/0x31 shell items) just enough for the
    // spec; real shortcuts also work with just a LocalBasePath in LinkInfo.

    private static byte[] linkTargetIDList() throws IOException {
        ByteArrayOutputStream items = new ByteArrayOutputStream();

        // Root: "My Computer" shell item (0x1F, CSIDL_DRIVES)
        items.write(shellItemMyComputer());
        // Drive: C:\
        items.write(shellItemDrive("C:"));
        // Folder: Windows
        items.write(shellItemFolder("Windows"));
        // Folder: System32
        items.write(shellItemFolder("System32"));
        // File: notepad.exe
        items.write(shellItemFile("notepad.exe"));
        // Terminal ItemID (2 zero bytes)
        items.write(le16(0));

        byte[] itemsBytes = items.toByteArray();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // IDListSize (2 bytes) + IDList
        out.write(le16(itemsBytes.length));
        out.write(itemsBytes);
        return out.toByteArray();
    }

    /** Simple "My Computer" shell item */
    private static byte[] shellItemMyComputer() throws IOException {
        // Type 0x1F = root folder; CSIDL_DRIVES = 0x11
        byte[] data = {
            0x1F, 0x00,         // type flags (My Computer)
            0x11,               // CSIDL_DRIVES (My Computer)
            (byte) 0xEF         // flags
        };
        return wrapItemID(data);
    }

    /** Simple drive ItemID (e.g. "C:") */
    private static byte[] shellItemDrive(String drive) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        data.write(0x2F);                           // type: drive letter
        data.write(drive.getBytes("US-ASCII"));     // "C:"
        data.write(0x00);                           // null terminator
        // pad to 21 bytes of data (common drive item size)
        while (data.size() < 21) data.write(0x00);
        return wrapItemID(data.toByteArray());
    }

    /** Minimal folder ItemID */
    private static byte[] shellItemFolder(String name) throws IOException {
        return shellItemEntry(0x31, name);
    }

    /** Minimal file ItemID */
    private static byte[] shellItemFile(String name) throws IOException {
        return shellItemEntry(0x32, name);
    }

    private static byte[] shellItemEntry(int typeFlags, String name) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        data.write(typeFlags);         // type byte
        data.write(0x00);              // unknown
        data.write(le32(0));           // file size (0 for folders)
        data.write(le16(0));           // date modified
        data.write(le16(0));           // time modified
        data.write(le16(0x20));        // file attributes (ARCHIVE)
        data.write(name.getBytes("US-ASCII"));
        data.write(0x00);              // null terminator
        if (data.size() % 2 != 0) data.write(0x00); // word-align
        return wrapItemID(data.toByteArray());
    }

    /** Prepend the 2-byte ItemIDSize to an item's data */
    private static byte[] wrapItemID(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int size = data.length + 2; // +2 for the size field itself
        out.write(le16(size));
        out.write(data);
        return out.toByteArray();
    }

    // ── LinkInfo (§2.3) ──────────────────────────────────────────────────────
    //
    // This is the most important section for path resolution.
    // We use LocalPath only (no network share).

    private static byte[] linkInfo() throws IOException {
        final String localBasePath  = "C:\\Windows\\System32\\notepad.exe";
        final String commonPathSuff = "";

        // All offsets are relative to the start of LinkInfo
        final int headerSize        = 0x1C;  // LinkInfoHeaderSize (28)
        final int volumeIDOffset    = headerSize;

        // VolumeID block (§2.3.1)
        byte[] volumeID = buildVolumeID("C:\\");
        int localBasePathOffset     = volumeIDOffset + volumeID.length;

        // CommonNetworkRelativeLinkOffset = 0 (not present)
        int commonPathSuffixOffset  = localBasePathOffset +
                                      localBasePath.length() + 1;
        int linkInfoSize            = commonPathSuffixOffset +
                                      commonPathSuff.length() + 1;

        ByteBuffer b = ByteBuffer.allocate(linkInfoSize)
                                 .order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(linkInfoSize);             // LinkInfoSize
        b.putInt(headerSize);              // LinkInfoHeaderSize
        b.putInt(0x00000001);              // LinkInfoFlags: VolumeIDAndLocalBasePath
        b.putInt(volumeIDOffset);          // VolumeIDOffset
        b.putInt(localBasePathOffset);     // LocalBasePathOffset
        b.putInt(0);                       // CommonNetworkRelativeLinkOffset (absent)
        b.putInt(commonPathSuffixOffset);  // CommonPathSuffixOffset
        b.put(volumeID);                   // VolumeID
        // LocalBasePath (null-terminated ASCII)
        b.put(localBasePath.getBytes("US-ASCII"));
        b.put((byte) 0x00);
        // CommonPathSuffix (empty, null-terminated)
        b.put((byte) 0x00);

        // Prepend size (4 bytes) – wait, LinkInfoSize already includes itself.
        // The structure is self-describing; no extra length prefix needed.
        return b.array();
    }

    /** VolumeID structure (§2.3.1) for a local fixed drive */
    private static byte[] buildVolumeID(String rootPath) throws IOException {
        final int DRIVE_FIXED = 0x00000003;
        final byte[] label = "Windows".getBytes("US-ASCII");

        // VolumeIDSize = 16 (header) + label + null
        int size = 16 + label.length + 1;
        ByteBuffer b = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        b.putInt(size);            // VolumeIDSize
        b.putInt(DRIVE_FIXED);    // DriveType
        b.putInt(0x12345678);     // DriveSerialNumber (arbitrary)
        b.putInt(16);             // VolumeLabelOffset = right after header
        b.put(label);
        b.put((byte) 0x00);
        return b.array();
    }

    // ── StringData (§2.4) ────────────────────────────────────────────────────
    //
    // Each string is: CountCharacters (2 bytes LE) + UTF-16LE chars.
    // Because we set IsUnicode in LinkFlags, strings are UTF-16LE.

    private static byte[] stringData(String s) throws IOException {
        byte[] utf16 = s.getBytes("UTF-16LE");
        int charCount = s.length();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(le16(charCount));
        out.write(utf16);
        return out.toByteArray();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static byte[] le16(int value) {
        return new byte[] { (byte)(value & 0xFF), (byte)((value >> 8) & 0xFF) };
    }

    private static byte[] le32(int value) {
        return new byte[] {
            (byte)( value        & 0xFF),
            (byte)((value >>  8) & 0xFF),
            (byte)((value >> 16) & 0xFF),
            (byte)((value >> 24) & 0xFF)
        };
    }

    private static void putFileTime(ByteBuffer b, long windowsTime) {
        b.putLong(windowsTime); // FILETIME is a 64-bit LE value
    }
}