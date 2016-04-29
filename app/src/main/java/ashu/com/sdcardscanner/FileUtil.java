package ashu.com.sdcardscanner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Ashwini on 4/29/16.
 */
public class FileUtil {
    private FileUtil() {
    }

    public static List<File> getFiles(File dir,List<File> files) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    files.add(listFile[i]);
                    getFiles(listFile[i],files);

                } else {
                    files.add(listFile[i]);
                }
            }
        }
        return files;
    }

    public static long calculateAverageFileSize(List<File> fileList) {
        long averageSize = 0;
        for (File file : fileList) {
            averageSize += file.length();
        }
        return averageSize / fileList.size();
    }

    public static TreeMap<String, Integer> calculateHighestFrequencyFileExtensions(List<File> fileList) {
        HashMap<String, Integer> fileExtensionMap = new HashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(fileExtensionMap);
        TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

        for (File file : fileList) {
            if (getFileExtension(file.getName()) != null) {
                if (fileExtensionMap.get(getFileExtension(file.getName())) != null) {
                    Integer count = fileExtensionMap.get(getFileExtension(file.getName()));
                    fileExtensionMap.put(getFileExtension(file.getName()), count + 1);
                } else {
                    fileExtensionMap.put(getFileExtension(file.getName()), 1);
                }
            }
        }
        sorted_map.putAll(fileExtensionMap);
        return sorted_map;
    }

    private static String getFileExtension(String name) {
        String filenameArray[] = name.split("\\.");
        String extension = null;
        if (filenameArray.length > 1) {
            extension = filenameArray[filenameArray.length - 1];
        }
        return extension;
    }

    private static class ValueComparator implements Comparator<String> {
        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        @Override
        public int compare(String lhs, String rhs) {
            if (base.get(lhs) >= base.get(rhs)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static List<File> getBiggestFiles(List<File> fileList) {
        File[] files = fileList.toArray(new File[fileList.size()]);
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                int n1 = (int) file1.length();
                int n2 = (int) file2.length();
                return n2 - n1;
            }
        });
        File[] tenBigFiles = new File[10];
        for (int i = 0; i < 10; i++) {
            tenBigFiles[i] = files[i];
        }
        return new ArrayList<File>(Arrays.asList(tenBigFiles));
    }
}
