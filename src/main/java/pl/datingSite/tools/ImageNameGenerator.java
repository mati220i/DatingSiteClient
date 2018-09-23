package pl.datingSite.tools;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ImageNameGenerator {

    private List<File> images;
    private File directoryPath;
    private File[] files;
    private int currentStep;

    public ImageNameGenerator(File directoryPath) {
        images = new LinkedList<>();
        this.directoryPath = directoryPath;
        files = directoryPath.listFiles();
        this.currentStep = -1;

        for(int i = 0; i < files.length; i++) {
            if(files[i].isFile())
                images.add(files[i]);
        }
    }

    public void resetListToFirstElement() {
        this.currentStep = 0;
    }

    /**
     *
     * @param next - if next = 1, method get next image from list, if next = 0, get previous image
     * @return current image
     */
    public File getCurrentImage(boolean next) {
        if(next) {
            currentStep++;
            if (currentStep < images.size()) {
                return images.get(currentStep);
            } else {
                currentStep--;
                return images.get(currentStep);
            }
        } else {
            currentStep--;
            if(currentStep >= 0) {
                return images.get(currentStep);
            } else {
                currentStep++;
                return images.get(currentStep);
            }
        }
    }

    public void setNameCurrentImage(String filename) {
        if(currentStep >= 0) {
            int counter = -1;
            //File newFile = new File(images.get(currentStep).getParent() +  "\\" + filename + ".jpg");
            File newFile;
            do {
                counter++;
                if(counter == 0)
                    newFile = new File(images.get(currentStep).getParent() +  "\\" + filename + "-.jpg");
                else
                    newFile = new File(images.get(currentStep).getParent() +  "\\" + filename + "-" + counter + ".jpg");
            } while (!images.get(currentStep).renameTo(newFile));
        }
    }

    public int countFiles() {
        return images.size();
    }

    public int getCurrent() {
        return currentStep;
    }
}
