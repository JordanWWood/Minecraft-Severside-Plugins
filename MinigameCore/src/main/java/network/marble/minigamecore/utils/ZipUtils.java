package network.marble.minigamecore.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

public class ZipUtils {
    public static String unzipFile(String zipLocation, String fileToUnzip) throws IOException {
        String output = null;
        ZipFile zip = new ZipFile(zipLocation);
        Enumeration<? extends ZipEntry> enu = zip.entries();
        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = enu.nextElement();
            if (zipEntry.getName().equals(fileToUnzip)) {
                output = IOUtils.toString(zip.getInputStream(zipEntry), StandardCharsets.UTF_8.name());
                break;
            }
        }
        zip.close();
        return output;
    }

    public static void unzip(String zipLocation, String unpackDirectory) throws IOException {
        ZipFile zip = new ZipFile(zipLocation);
        Enumeration<? extends ZipEntry> enu = zip.entries();
        File unpack = new File(unpackDirectory);
        if (!unpack.exists()) unpack.mkdirs();
        while (enu.hasMoreElements()) {
            ZipEntry zipEntry = enu.nextElement();
            String name = zipEntry.getName();
            File file = new File(unpackDirectory+"/"+name);
            if (name.endsWith("/")) {
                file.mkdirs();
                continue;
            }

            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            InputStream is = zip.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
            is.close();
            fos.close();
        }
        zip.close();
    }

    /**
     * @deprecated Doesn't replicate folder structure
     */
    @Deprecated
    public static void zip(String packDirectory, String zipLocation) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipLocation);
        ZipOutputStream zos = new ZipOutputStream(fos);
        File srcFile = new File(packDirectory);
        addDirectoryToZip(zos, srcFile);
    }

    private static void addDirectoryToZip(ZipOutputStream zos, File srcFile) throws IOException {
        File[] files = srcFile.listFiles();
        if (files == null) for (File file : files) {

            if (file.isDirectory()) {
                addDirectoryToZip(zos, file);
                continue;
            }

            byte[] buffer = new byte[1024];

            FileInputStream fis = new FileInputStream(file);

            zos.putNextEntry(new ZipEntry(file.getName()));

            int length;

            while ((length = fis.read(buffer)) > 0) zos.write(buffer, 0, length);

            zos.closeEntry();
            fis.close();
        }

    }

}
