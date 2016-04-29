package ashu.com.sdcardscanner;

import java.io.Serializable;

/**
 * Created by Ashwini on 4/29/16.
 */
public class FileProperty implements Serializable{
    private String title;
    private String data;

    public FileProperty(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setData(String data) {
        this.data = data;
    }
}
