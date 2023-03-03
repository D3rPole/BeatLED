package UI.V2;

public class Item {
    private String path;
    private String displayedText;
    public Object obj;

    Item(){}
    Item(String displayedText, Object obj){
        this.displayedText = displayedText;
        this.obj = obj;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public void setDisplayedText(String displayedText) {
        this.displayedText = displayedText;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getPath() {
        return path;
    }

    public Object getObj() {
        if(obj == null) return null;
        return obj;
    }

    @Override
    public String toString() {
        if(obj == null || displayedText != null){
            return displayedText;
        }
        return obj.toString();
    }
}
