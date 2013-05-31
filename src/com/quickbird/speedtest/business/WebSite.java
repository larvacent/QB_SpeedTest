package com.quickbird.speedtest.business;

import com.quickbird.enums.WiteSiteTestStatus;

public class WebSite {

	private int id;
	private String address;
	private String name;
	private boolean isChecked;
	private Integer icon;
	private int ping = -1;
	private int degree = -1;
	private int classify;
	private WiteSiteTestStatus status = WiteSiteTestStatus.Error;

	public WebSite() {
	}

	public void resetDegree() {
		this.degree = -1;
	}

	public void resetStatus() {
		this.status = WiteSiteTestStatus.Error;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public Integer getIcon() {
		return icon;
	}

	public void setIcon(Integer icon) {
		this.icon = icon;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public int getClassify() {
		return classify;
	}

	public void setClassify(int classify) {
		this.classify = classify;
	}

	public WiteSiteTestStatus getStatus() {
		return status;
	}

	public void setStatus(WiteSiteTestStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "WebSite [id=" + id + ", address=" + address + ", name=" + name
				+ ", isChecked=" + isChecked + ", icon=" + icon + ", ping="
				+ ping + ", degree=" + degree + ", classify=" + classify
				+ ", status=" + status + "]";
	}

}
