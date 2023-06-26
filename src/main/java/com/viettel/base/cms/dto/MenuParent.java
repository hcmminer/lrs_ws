package com.viettel.base.cms.dto;

import java.util.List;

public class MenuParent {
    String title;
    boolean root;
    String icon; //: 'flaticon2-architecture-and-city',
    String svg; //: './assets/media/svg/icons/Design/Layers.svg',
    String page; //: '/dashboard-management',
    String code; //: 'ALARM_DASHBOARD',
    String bullet; //: 'dot',
    List<SubMenu> submenu; //

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSvg() {
        return svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBullet() {
        return bullet;
    }

    public void setBullet(String bullet) {
        this.bullet = bullet;
    }

    public List<SubMenu> getSubmenu() {
        return submenu;
    }

    public void setSubmenu(List<SubMenu> submenu) {
        this.submenu = submenu;
    }
}
