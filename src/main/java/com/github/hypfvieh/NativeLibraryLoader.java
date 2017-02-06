package com.github.hypfvieh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Helper class to load a native library either from a given path, the classpath or system library path.
 */
public final class NativeLibraryLoader {

    private static final AtomicBoolean ENABLED = new AtomicBoolean(true);

    public static void setEnabled(boolean _enabled) {
        ENABLED.set(_enabled);
    }

    /**
     * Private constructor - this class will never be instanced
     */
    private NativeLibraryLoader() {
    }

    /**
     * Load the given _libName from one of the given pathes (will search for the library and uses first match).
     *
     * @param _libName library to load
     * @param _searchPathes pathes to search
     */
    public static void loadLibrary(String _libName, String... _searchPathes) {
        if (!ENABLED.get()) {
            return;
        }
        if (_searchPathes != null && _searchPathes.length > 0) {
            findProperNativeLib(_libName, Arrays.asList(_searchPathes));
        }
    }

    /**
     * Tries to find a proper library for loading.
     * Search pathes are iterated and search for path/$jvmArch/_libName.
     * If that fails path/_libName is tried.
     * If that also fails, classpath is considered (same pattern, first with $jvmArch, then without).
     *
     * @param _libName library to load
     * @param _searchPathes pathes to search
     *
     * @throws RuntimeException if library could no be loaded from any given path (including classpath)
     */
    private static void findProperNativeLib(String _libName, List<String> _searchPathes) {
        String arch = System.getProperty("os.arch");
        for (String path : _searchPathes) {
            File file = new File(concatFilePath(false, path, arch, _libName));
            if (!file.exists()) { // file exists in file system
                file = new File(concatFilePath(false, path, _libName));
            }

            if (loadLib(file.getAbsolutePath()) == null) { // library could be loaded, skip further tries
                return;
            }
        }

        // if we reach this point, library was not found in filesystem, so we try classpath
        Throwable loadLibError = null;
        for (String path : _searchPathes) {

            String fileNameWithPath = concatFilePath(false, path, arch, _libName);
            InputStream libAsStream = NativeLibraryLoader.class.getClassLoader().getResourceAsStream(fileNameWithPath);
            if (libAsStream == null) {
                fileNameWithPath = concatFilePath(false, path, _libName);
                libAsStream = NativeLibraryLoader.class.getClassLoader().getResourceAsStream(fileNameWithPath);
            }
            if (libAsStream != null) {
                String fileExt = getFileExtension(fileNameWithPath);
                String prefix = fileNameWithPath.replace(new File(fileNameWithPath).getParent(), "").replace("." + fileExt, "");

                try {
                    // extract the library
                    File tmpFile = extractToTemp(libAsStream, prefix, fileExt);
                    // try to load it
                    loadLibError = loadLib(tmpFile.getAbsolutePath());
                    if (loadLibError == null) { // load successful
                        return;
                    }
                } catch (IOException _ex) {
                    // ignore Exception, we may have other options to try
                }
            }
        }

        // last option: try loading from system library path
        System.loadLibrary(_libName);

    }

    /**
     * Tries to load a library from the given path.
     * Will catches exceptions and return them to the caller.
     * Will return null if no exception has been thrown.
     *
     * @param _lib
     * @return Exception or null
     */
    private static Throwable loadLib(String _lib) {
        try {
            System.load(_lib);
            return null;
        } catch (Throwable _ex) {
            return _ex;
        }
    }

    /**
     * Extract the file behind InputStream _fileToExtract to the tmp-folder.
     *
     * @param _fileToExtract InputStream with file to extract
     * @param _tmpName temp file name
     * @param _fileSuffix temp file suffix
     *
     * @return temp file object
     * @throws IOException on any error
     */
    private static File extractToTemp(InputStream _fileToExtract, String _tmpName, String _fileSuffix) throws IOException {
        if (_fileToExtract == null) {
            throw new IOException("Null stream");
        }
        File tempFile = File.createTempFile(_tmpName, _fileSuffix);
        tempFile.deleteOnExit();

        if (!tempFile.exists()) {
            throw new FileNotFoundException("File " + tempFile.getAbsolutePath() + " could not be created");
        }

        byte[] buffer = new byte[1024];
        int readBytes;

        OutputStream os = new FileOutputStream(tempFile);
        try {
            while ((readBytes = _fileToExtract.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            os.close();
            _fileToExtract.close();
        }
        return tempFile;
    }

    /**
     * Concats a path from all given parts, using the path delimiter for the currently used platform.
     * @param _includeTrailingDelimiter include delimiter after last token
     * @param _parts parts to concat
     * @return concatinated string
     */
    public static String concatFilePath(boolean _includeTrailingDelimiter, String..._parts) {
        if (_parts == null) {
            return null;
        }
        StringBuilder allParts = new StringBuilder();

        for (int i = 0; i < _parts.length; i++) {
            if (_parts[i] == null) {
                continue;
            }
            allParts.append(_parts[i]);

            if (!_parts[i].endsWith(File.separator)) {
                allParts.append(File.separator);
            }
        }

        if (!_includeTrailingDelimiter && allParts.length() > 0) {
            return allParts.substring(0, allParts.lastIndexOf(File.separator));
        }

        return allParts.toString();
    }

    /**
     * Extracts the file extension (part behind last dot of a filename).
     * Only returns the extension, without the leading dot.
     *
     * @param _fileName
     * @return extension, empty string if not dot was found in filename or null if given String was null
     */
    public static String getFileExtension(String _fileName) {
        if (_fileName == null) {
            return null;
        }
        int lastDot = _fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return _fileName.substring(lastDot + 1);
    }
}
