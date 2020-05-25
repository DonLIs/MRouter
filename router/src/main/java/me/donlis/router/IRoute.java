package me.donlis.router;

import java.util.Map;

import me.donlis.annotation.RouterBean;

public interface IRoute {

    void load(Map<String, RouterBean> maps);

}
