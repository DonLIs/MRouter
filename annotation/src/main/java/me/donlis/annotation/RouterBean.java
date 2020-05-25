package me.donlis.annotation;


import javax.lang.model.element.Element;

public class RouterBean {

    private Element element;

    private Class<?> clazz;

    private String path;

    public RouterBean(Element element, Class<?> clazz, String path){
        this.element = element;
        this.clazz = clazz;
        this.path = path;
    }

    public static RouterBean newInstance(Class<?> clazz, String path){
        return new RouterBean(null,clazz,path);
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
