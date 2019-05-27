package entrance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main{

    private List<String> result = new CopyOnWriteArrayList<>();
    private List<String> listFiles = new ArrayList<>();

    public static void main(String[] args) {

        if(args.length != 2){
            return;
        }

        Main search = new Main();
        String root = args[0];
        String text = args[1];
        List<String> ext = new ArrayList<>();
        ext.add(".txt");
        search.parallelSearch(root, text, ext);
        search.showResult();
    }

    private void parallelSearch(String root, String text, List<String> ext) {
        searchInDirectories(root, ext);

        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < listFiles.size() / 2; i++) {
                if (searchTextInFile(text, listFiles.get(i))) {
                    result.add(listFiles.get(i));
                }
            }
        });

        Thread threadTwo = new Thread(() -> {
            for (int i = listFiles.size() / 2; i < listFiles.size(); i++) {
                if (searchTextInFile(text, listFiles.get(i))) {
                    result.add(listFiles.get(i));
                }
            }
        });

        threadOne.start();
        threadTwo.start();
        try {
            threadOne.join();
            threadTwo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void searchInDirectories(String directory, List<String> ext) {
        File file = new File(directory);
        for (File element : file.listFiles()) {
            if (element.isDirectory()) {
                searchInDirectories(element.getAbsolutePath(), ext);
            } else {
                for (String name : ext) {
                    if (element.getName().endsWith(name)) {
                        this.listFiles.add(element.getAbsolutePath());
                    }
                }
            }
        }
    }

    private boolean searchTextInFile(final String text, final String path) {
        File file = new File(path);
        boolean flag = false;
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (scanner != null) {
            try {
                while (scanner.hasNextLine()) {
                    if (scanner.nextLine().contains(text)) {
                        flag = true;
                        break;
                    }
                }
            } finally {
                scanner.close();
            }
        }
        return flag;
    }

    private void showResult() {
        for (String str : this.result) {
            System.out.println(str);
        }
    }
}
