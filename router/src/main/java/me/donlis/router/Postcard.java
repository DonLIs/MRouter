package me.donlis.router;

public class Postcard {

    private String path;

    private Class<?> clazz;

    public Postcard(String path){
        this.path = path;
    }

    public void navigation(){
        MRouter.getInstance().navigation(null,this);
    }

    public String getPath() {
        return path;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
