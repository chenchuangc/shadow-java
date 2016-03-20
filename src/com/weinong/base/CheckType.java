package com.weinong.base;

public enum CheckType {

	empty("参数为空"), not_number("不是数字"), not_mobile("不是手机号");

	private String label;

	private CheckType(String label) {
		this.label = label;
	}

	public boolean check(Object value) {
		if (this == empty) { return null == value || value.toString().trim().equals(""); }
		if (this == not_number) { return null == value || value.toString().matches("^(-|\\+)?[0-9]+(\\.[0-9]+)?$"); }
		if (this == not_mobile) { return null == value || value.toString().matches("^1[3458][0-9]{9}$"); }
		return false;
	}

	public String getLabel() {
		return label;
	}

}